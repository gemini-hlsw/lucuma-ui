// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyMap
import cats.implicits.*
import crystal.react.ReuseView
import crystal.react.hooks.*
import crystal.react.reuse.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.hooks.Hooks.UseState
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.ags.AgsAnalysis
import lucuma.ags.AgsGuideQuality
import lucuma.ags.GuideStarCandidate
import lucuma.core.enums.GmosSouthFilter
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.enums.GmosSouthGrating
import lucuma.core.enums.GuideProbe
import lucuma.core.enums.GuideSpeed
import lucuma.core.enums.PortDisposition
import lucuma.core.geom.Area
import lucuma.core.math.*
import lucuma.core.model.SiderealTracking
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import lucuma.ui.aladin.*
import lucuma.ui.visualization.*
import monocle.macros.GenLens
import lucuma.core.enums.F2Fpu
import lucuma.core.enums.F2Disperser
import lucuma.core.enums.F2Filter

case class AladinContainer(
  fov:         ReuseView[Fov],
  coordinates: Coordinates
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

extension (o: Offset)
  def toStringOffset: String =
    f"(p: ${o.p.toAngle.toMicroarcseconds / 1e6}%2.3f, q: ${o.q.toAngle.toMicroarcseconds / 1e6}%2.3f)"

enum InstrumentType:
  case GMOS, F2

object AladinContainer {
  type Props = AladinContainer

  val coordinates = GenLens[AladinContainer](_.coordinates)

  implicit val propsReuse: Reusability[Props] =
    Reusability.by_==

  implicit val reuseDouble: Reusability[Double] = Reusability.double(0.00001)

  val component = ScalaFnComponent[Props]: props =>
    for {
      // View coordinates (in case the user pans)
      currentPos         <- useState(props.coordinates)
      // Ref to the aladin component
      aladinRef          <- useState(none[Aladin])
      // resize detector
      resize             <- useResizeDetector
      flip               <- useState(true)
      // Toggle state for SVGVisualizationOverlay CSS
      fpuVisible         <- useState(true)
      ccdVisible         <- useState(true)
      candidatesVisible  <- useState(true)
      patrolFieldVisible <- useState(true)
      probeVisible       <- useState(true)
      fullScreen         <- useStateView(AladinFullScreen.Normal)
      instrument         <- useState[InstrumentType](InstrumentType.F2)
      gmosConf           <- useState(
                              BasicConfiguration.GmosSouthLongSlit(
                                grating = GmosSouthGrating.R400_G5325,
                                filter = GmosSouthFilter.HeII.some,
                                fpu = GmosSouthFpu.LongSlit_2_00,
                                centralWavelength = CentralWavelength(Wavelength.fromIntNanometers(500).get)
                              )
                            )
      f2Conf             <- useState(
                              BasicConfiguration.F2LongSlit(
                                disperser = F2Disperser.R1200HK,
                                filter = F2Filter.H,
                                fpu = F2Fpu.LongSlit2
                              )
                            )

    } yield
      /**
       * Called when the position changes, i.e. aladin pans. We want to offset the visualization to
       * keep the internal target correct
       */

      def onPositionChanged(u: PositionChanged): Callback = {
        val viewCoords = Coordinates(u.ra, u.dec)
        currentPos.setState(viewCoords)
      }

      val screenOffset =
        currentPos.value.diff(props.coordinates).offset

      def onZoom(aladin: Aladin) = (v: Fov) => props.fov.set(v)

      def customizeAladin(v: Aladin): Callback =
        aladinRef.setState(Some(v)) *>
          v.onZoomCB(onZoom(v)) *> // re render on zoom
          v.onPositionChangedCB(onPositionChanged)

      val gs          = props.coordinates
      //
      // Get the appropriate configuration based on selected instrument
      val currentConf = instrument.value match {
        case InstrumentType.GMOS => gmosConf.value.some
        case InstrumentType.F2   =>
          f2Conf.value.some
      }

      val shapes = instrument.value match {
        case InstrumentType.GMOS =>
          GmosGeometry.gmosGeometry(
            props.coordinates,
            None,
            None,
            Angle.Angle0.some,
            currentConf,
            PortDisposition.Side,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                Angle.Angle0,
                Area.MinArea
              )
              .some,
            VisualizationStyles.GuideStarCandidateVisible
          )
        case InstrumentType.F2   =>
          Flamingos2Geometry.f2Geometry(
            props.coordinates,
            None,
            None,
            Angle.Angle0.some,
            currentConf,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                Angle.Angle0,
                Area.MinArea
              )
              .some,
            VisualizationStyles.GuideStarCandidateVisible
          )
      }

      def toggler(
        id:    String,
        item:  String,
        state: UseState[Boolean],
        clazz: Css = Css("toggle-container")
      ) =
        <.div(
          clazz,
          <.input(
            ^.`type`  := "checkbox",
            ^.id      := s"overlay-toggle-$id",
            ^.checked := state.value,
            ^.onChange --> state.setState(!state.value)
          ),
          <.label(
            ^.htmlFor := s"overlay-toggle-$id",
            s"Show/Hide $item"
          )
        )

      def visibilityClasses = instrument.value match {
        case InstrumentType.GMOS =>
          VisualizationStyles.GmosFpuVisible.when_(fpuVisible.value) |+|
            VisualizationStyles.GmosCcdVisible.when_(ccdVisible.value) |+|
            VisualizationStyles.GmosCandidatesAreaVisible.when_(
              candidatesVisible.value
            ) |+|
            VisualizationStyles.GmosPatrolFieldVisible.when_(
              patrolFieldVisible.value
            ) |+|
            VisualizationStyles.GmosProbeVisible.when_(probeVisible.value)
        case InstrumentType.F2   =>
          VisualizationStyles.F2FpuVisible.when_(fpuVisible.value) |+|
            VisualizationStyles.F2ScienceAreaVisible.when_(ccdVisible.value) |+|
            VisualizationStyles.F2CandidatesAreaVisible.when_(
              candidatesVisible.value
            ) |+|
            VisualizationStyles.F2PatrolFieldVisible.when_(
              patrolFieldVisible.value
            ) |+|
            VisualizationStyles.F2ProbeArmVisible.when_(probeVisible.value)
      }

      <.div(
        Css("react-aladin-container"),
        aladinRef.value.map(AladinZoomControl(_)),
        AladinFullScreenControl(fullScreen),
        // This happens during a second render. If we let the height to be zero, aladin
        // will take it as 1. This height ends up being a denominator, which, if low,
        // will make aladin request a large amount of tiles and end up freezing the demo.
        if (resize.height.exists(_ >= 100))
          ReactFragment(
            (resize.width, resize.height, shapes.flatMap(NonEmptyMap.fromMap))
              .mapN((w, h, s) =>
                SVGVisualizationOverlay(
                  w,
                  h,
                  props.fov.get,
                  screenOffset,
                  s,
                  clazz = visibilityClasses
                )
              ),
            (resize.width, resize.height)
              .mapN(
                TargetsOverlay(
                  _,
                  _,
                  props.fov.get,
                  currentPos.value.diff(props.coordinates).offset,
                  props.coordinates,
                  List(
                    SVGTarget
                      .CrosshairTarget(props.coordinates, Css("science-target"), 10)
                      .some,
                    gs.some.map(SVGTarget.CircleTarget(_, Css("guidestar"), 3))
                  ).flatten
                )
              ),
            ReactAladin(
              Css("react-aladin") |+| Css("test").when_(flip.value),
              AladinOptions(
                showReticle = true,
                showLayersControl = false,
                target = props.aladinCoordsStr,
                fov = props.fov.get.x,
                showGotoControl = false,
                showZoomControl = false,
                showCooLocation = true,
                showFullscreenControl = false,
                showProjectionControl = false
              ),
              customize = customizeAladin(_)
            ),
            <.div(
              Css("aladin-controls"),
              <.div(
                Css("config-togglers"),
                <.label("FOV: ", props.fov.get.toStringAngle),
                <.label("Coord: ", props.aladinCoordsStr),
                <.label("Pos: ", currentPos.value.toString),
                <.label("Offset: ", currentPos.value.diff(props.coordinates).offset.toStringOffset)
              ),
              <.div(
                Css("config-togglers"),
                toggler("fpu", "FPU", fpuVisible),
                toggler("ccd", "Science CCD", ccdVisible),
                toggler("candidates", "Candidates Field", candidatesVisible),
                toggler("patrol-field", "Patrol Field", patrolFieldVisible),
                toggler("probe", "Probe", probeVisible)
              ),
              <.div(
                Css("config-controls"),
                <.label(^.htmlFor := "instrument-selector", "Select Instrument:"),
                <.select(
                  ^.id    := "instrument-selector",
                  ^.value := (instrument.value match {
                    case InstrumentType.GMOS => "gmos"
                    case InstrumentType.F2   => "f2"
                  }),
                  ^.onChange ==> ((r: ReactUIEventFromInput) =>
                    r.target.value match {
                      case "gmos" => instrument.setState(InstrumentType.GMOS)
                      case "f2"   => instrument.setState(InstrumentType.F2)
                      case _      => Callback.empty
                    }
                  )
                )(
                  <.option(
                    ^.key   := "gmos",
                    ^.value := "gmos",
                    "GMOS"
                  ),
                  <.option(
                    ^.key   := "f2",
                    ^.value := "f2",
                    "Flamingos2"
                  )
                )
              ),
              instrument.value match {
                case InstrumentType.GMOS =>
                  <.div(
                    Css("config-controls"),
                    <.label(^.htmlFor := "fpu-selector", "Select FPU:"),
                    <.select(
                      ^.id    := "fpu-selector",
                      ^.value := gmosConf.value.fpu.tag,
                      ^.onChange ==> ((r: ReactUIEventFromInput) =>
                        Enumerated[GmosSouthFpu]
                          .fromTag(r.target.value)
                          .map(fpu =>
                            gmosConf.setState(
                              gmosConf.value.copy(fpu = fpu)
                            )
                          )
                          .getOrElse(Callback.empty)
                      )
                    )(
                      Enumerated[GmosSouthFpu].all
                        .filter(_.tag.startsWith("LongSlit"))
                        .map(fpu =>
                          <.option(
                            ^.key   := fpu.tag,
                            ^.value := fpu.tag,
                            fpu.longName
                          )
                        )
                        .toTagMod
                    )
                  )
                case InstrumentType.F2   =>
                  <.div(
                    Css("config-controls"),
                    <.label(^.htmlFor := "f2-fpu-selector", "Select F2 FPU:"),
                    <.select(
                      ^.id    := "f2-fpu-selector",
                      ^.value := f2Conf.value.fpu.tag,
                      ^.onChange ==> ((r: ReactUIEventFromInput) =>
                        Enumerated[F2Fpu]
                          .fromTag(r.target.value)
                          .map(fpu =>
                            f2Conf.setState(
                              f2Conf.value.copy(fpu = fpu)
                            )
                          )
                          .getOrElse(Callback.empty)
                      )
                    )(
                      Enumerated[F2Fpu].all
                        .filter(_.tag.startsWith("LongSlit"))
                        .map(fpu =>
                          <.option(
                            ^.key   := fpu.tag,
                            ^.value := fpu.tag,
                            fpu.longName
                          )
                        )
                        .toTagMod
                    )
                  )
              }
            )
          )
        else EmptyVdom
      )
        .withRef(resize.ref)
}
