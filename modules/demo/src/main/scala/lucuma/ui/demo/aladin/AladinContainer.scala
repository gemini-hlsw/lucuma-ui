// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyList
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
import lucuma.core.enums.Flamingos2Disperser
import lucuma.core.enums.Flamingos2Filter
import lucuma.core.enums.Flamingos2Fpu
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
  case GMOS, Flamingos2

object AladinContainer {
  type Props = AladinContainer

  val coordinates = GenLens[AladinContainer](_.coordinates)

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
      instrument         <- useState[InstrumentType](InstrumentType.Flamingos2)
      gmosConf           <- useState(
                              BasicConfiguration.GmosSouthLongSlit(
                                grating = GmosSouthGrating.R400_G5325,
                                filter = GmosSouthFilter.HeII.some,
                                fpu = GmosSouthFpu.LongSlit_2_00,
                                centralWavelength = CentralWavelength(Wavelength.fromIntNanometers(500).get)
                              )
                            )
      f2Conf             <- useState(
                              BasicConfiguration.Flamingos2LongSlit(
                                disperser = Flamingos2Disperser.R1200HK,
                                filter = Flamingos2Filter.H,
                                fpu = Flamingos2Fpu.LongSlit2
                              )
                            )
      portDisposition    <- useState(PortDisposition.Side)
      posAngle           <- useState(Angle.Angle0)
      survey             <- useState(instrument.value match {
                              case InstrumentType.GMOS       => ImageSurvey.DSS
                              case InstrumentType.Flamingos2 => ImageSurvey.TWOMASS
                            })
      // State for science and acquisition offsets
      scienceOffset      <- useState(Offset.Zero)
      acquisitionOffset  <- useState(Offset.Zero)

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
      val currentConf = instrument.value match
        case InstrumentType.GMOS       => gmosConf.value.some
        case InstrumentType.Flamingos2 => f2Conf.value.some

      // Convert individual offsets to NonEmptyList format needed by the geometries
      val scienceOffsetList     =
        NonEmptyList.one(scienceOffset.value).some.filter(_ => scienceOffset.value != Offset.Zero)
      val acquisitionOffsetList = NonEmptyList
        .one(acquisitionOffset.value)
        .some
        .filter(_ => acquisitionOffset.value != Offset.Zero)

      val shapes = instrument.value match {
        case InstrumentType.GMOS       =>
          GmosGeometry.gmosGeometry(
            props.coordinates,
            scienceOffsetList,
            acquisitionOffsetList,
            posAngle.value.some,
            currentConf,
            portDisposition.value,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                posAngle.value,
                Area.MinArea
              )
              .some,
            VisualizationStyles.GuideStarCandidateVisible
          )
        case InstrumentType.Flamingos2 =>
          Flamingos2Geometry.f2Geometry(
            props.coordinates,
            scienceOffsetList,
            acquisitionOffsetList,
            posAngle.value.some,
            currentConf,
            portDisposition.value,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                posAngle.value,
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
        case InstrumentType.GMOS       =>
          VisualizationStyles.GmosFpuVisible.when_(fpuVisible.value) |+|
            VisualizationStyles.GmosCcdVisible.when_(ccdVisible.value) |+|
            VisualizationStyles.GmosCandidatesAreaVisible.when_(
              candidatesVisible.value
            ) |+|
            VisualizationStyles.GmosPatrolFieldVisible.when_(
              patrolFieldVisible.value
            ) |+|
            VisualizationStyles.GmosProbeVisible.when_(probeVisible.value)
        case InstrumentType.Flamingos2 =>
          VisualizationStyles.Flamingos2FpuVisible.when_(fpuVisible.value) |+|
            VisualizationStyles.Flamingos2ScienceAreaVisible.when_(ccdVisible.value) |+|
            VisualizationStyles.Flamingos2CandidatesAreaVisible.when_(
              candidatesVisible.value
            ) |+|
            VisualizationStyles.Flamingos2PatrolFieldVisible.when_(
              patrolFieldVisible.value
            ) |+|
            VisualizationStyles.Flamingos2ProbeArmVisible.when_(probeVisible.value)
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
                showProjectionControl = false,
                survey = survey.value
              ),
              customize = customizeAladin(_)
            ),
            <.div(
              Css("aladin-controls"),
              <.div(
                Css("descriptionconfig-togglers"),
                <.label("FOV: "),
                props.fov.get.toStringAngle,
                <.label("Coord: "),
                props.aladinCoordsStr,
                <.label("Pos: "),
                currentPos.value.toString,
                <.label("Offset: "),
                currentPos.value.diff(props.coordinates).offset.toStringOffset,
                <.label("PA: "),
                s"${posAngle.value.toDoubleDegrees}°",
                <.label("Science Offset: "),
                scienceOffset.value.toStringOffset,
                <.label("Acquisition Offset: "),
                acquisitionOffset.value.toStringOffset
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
                <.label(^.htmlFor     := "instrument-selector", "Select Instrument:"),
                <.select(
                  ^.id    := "instrument-selector",
                  ^.value := (instrument.value match {
                    case InstrumentType.GMOS       => "gmos"
                    case InstrumentType.Flamingos2 => "f2"
                  }),
                  ^.onChange ==> ((r: ReactUIEventFromInput) =>
                    r.target.value match {
                      case "gmos" => instrument.setState(InstrumentType.GMOS)
                      case "f2"   => instrument.setState(InstrumentType.Flamingos2)
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
                ),
                <.label(^.htmlFor     := "port-selector", "Select Port Disposition:"),
                <.select(
                  ^.id    := "port-selector",
                  ^.value := portDisposition.value.tag,
                  ^.onChange ==> ((r: ReactUIEventFromInput) =>
                    Enumerated[PortDisposition]
                      .fromTag(r.target.value)
                      .map(port => portDisposition.setState(port))
                      .getOrElse(Callback.empty)
                  )
                )(
                  Enumerated[PortDisposition].all
                    .map(port =>
                      <.option(
                        ^.key   := port.tag,
                        ^.value := port.tag,
                        port.tag.capitalize
                      )
                    )
                    .toTagMod
                ),
                <.label(^.htmlFor     := "pa-input", "Position Angle (°):"),
                <.input(
                  ^.`type` := "number",
                  ^.id     := "pa-input",
                  ^.min    := "0",
                  ^.max    := "360",
                  ^.step   := "5",
                  ^.value  := posAngle.value.toDoubleDegrees.toString,
                  ^.onChange ==> ((e: ReactEventFromInput) =>
                    e.target.value.toDoubleOption
                      .map(Angle.fromDoubleDegrees)
                      .map(posAngle.setState)
                      .getOrEmpty
                  )
                )
              ),
              instrument.value match {
                case InstrumentType.GMOS       =>
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
                case InstrumentType.Flamingos2 =>
                  <.div(
                    Css("config-controls"),
                    <.label(^.htmlFor := "f2-fpu-selector", "Select Flamingos2 FPU:"),
                    <.select(
                      ^.id    := "f2-fpu-selector",
                      ^.value := f2Conf.value.fpu.tag,
                      ^.onChange ==> ((r: ReactUIEventFromInput) =>
                        Enumerated[Flamingos2Fpu]
                          .fromTag(r.target.value)
                          .map(fpu =>
                            f2Conf.setState(
                              f2Conf.value.copy(fpu = fpu)
                            )
                          )
                          .getOrElse(Callback.empty)
                      )
                    )(
                      Enumerated[Flamingos2Fpu].all
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
              },
              <.div(
                Css("config-controls"),
                <.label(^.htmlFor := "survey-selector", "Select Survey:"),
                <.select(
                  ^.id    := "survey-selector",
                  ^.value := survey.value.tag,
                  ^.onChange ==> ((r: ReactUIEventFromInput) =>
                    Enumerated[ImageSurvey]
                      .fromTag(r.target.value)
                      .map { s =>
                        survey.setState(s)
                      }
                      .getOrElse(Callback.empty)
                  )
                )(
                  Enumerated[ImageSurvey].all
                    .map(s =>
                      <.option(
                        ^.key   := s.tag,
                        ^.value := s.tag,
                        s.name
                      )
                    )
                    .toTagMod
                )
              ),
              // Offset controls in a separate row
              <.div(
                Css("config-controls"),
                <.h5("Offset Controls"),
                <.div(
                  Css("offset-control"),
                  <.label("Science Offset (arcsec):"),
                  <.div(
                    <.label("p: "),
                    <.input(
                      ^.`type` := "number",
                      ^.step   := "5",
                      ^.value  := {
                        val (p, _) = Offset.signedDecimalArcseconds.get(scienceOffset.value)
                        p.toString
                      },
                      ^.onChange ==> ((e: ReactEventFromInput) => {
                        val pValue = e.target.value.toDoubleOption
                          .map(Angle.fromDoubleArcseconds)
                          .getOrElse(Angle.Angle0)
                        scienceOffset.modState(_.copy(p = pValue.p))
                      })
                    ),
                    <.label("q: "),
                    <.input(
                      ^.`type` := "number",
                      ^.step   := "5",
                      ^.value  := {
                        val (_, q) = Offset.signedDecimalArcseconds.get(scienceOffset.value)
                        q.toString
                      },
                      ^.onChange ==> ((e: ReactEventFromInput) => {
                        val qValue = e.target.value.toDoubleOption
                          .map(Angle.fromDoubleArcseconds)
                          .getOrElse(Angle.Angle0)
                        scienceOffset.modState(_.copy(q = qValue.q))
                      })
                    ),
                    <.button(
                      ^.onClick --> scienceOffset.setState(Offset.Zero),
                      "Reset"
                    )
                  )
                ),
                <.div(
                  Css("offset-control"),
                  <.label("Acquisition Offset (arcsec):"),
                  <.div(
                    <.label("p: "),
                    <.input(
                      ^.`type` := "number",
                      ^.step   := "5",
                      ^.value  := {
                        val (p, _) = Offset.signedDecimalArcseconds.get(acquisitionOffset.value)
                        p.toString
                      },
                      ^.onChange ==> ((e: ReactEventFromInput) => {
                        val pValue = e.target.value.toDoubleOption
                          .map(Angle.fromDoubleArcseconds)
                          .getOrElse(Angle.Angle0)
                        acquisitionOffset.modState(_.copy(p = pValue.p))
                      })
                    ),
                    <.label("q: "),
                    <.input(
                      ^.`type` := "number",
                      ^.step   := "5",
                      ^.value  := {
                        val (_, q) = Offset.signedDecimalArcseconds.get(acquisitionOffset.value)
                        q.toString
                      },
                      ^.onChange ==> ((e: ReactEventFromInput) => {
                        val qValue = e.target.value.toDoubleOption
                          .map(Angle.fromDoubleArcseconds)
                          .getOrElse(Angle.Angle0)
                        acquisitionOffset.modState(_.copy(q = qValue.q))
                      })
                    ),
                    <.button(
                      ^.onClick --> acquisitionOffset.setState(Offset.Zero),
                      "Reset"
                    )
                  )
                )
              )
            )
          )
        else EmptyVdom
      )
        .withRef(resize.ref)
}
