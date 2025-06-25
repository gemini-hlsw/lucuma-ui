// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyList
import cats.data.NonEmptyMap
import cats.syntax.all.*
import crystal.react.ReuseView
import crystal.react.hooks.*
import crystal.react.reuse.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.feature.ReactFragment
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
import lucuma.core.enums.SequenceType
import lucuma.core.geom.Area
import lucuma.core.math.*
import lucuma.core.model.SiderealTracking
import lucuma.core.syntax.enumerated.*
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import lucuma.ui.aladin.*
import lucuma.ui.visualization.*
import monocle.macros.GenLens

import scalajs.js

case class AladinContainer(
  fov:         ReuseView[Fov],
  coordinates: Coordinates
) extends ReactFnProps[AladinContainer](AladinContainer.component)

extension (o: Offset)
  def toStringOffset: String =
    f"(p: ${o.p.toAngle.toMicroarcseconds / 1e6}%2.3f, q: ${o.q.toAngle.toMicroarcseconds / 1e6}%2.3f)"

enum InstrumentType:
  case GMOS, Flamingos2

enum GmosMode:
  case LongSlit, Imaging

object GmosMode:
  given Enumerated[GmosMode] = Enumerated.from(LongSlit, Imaging).withTag(_.toString)

case class VisualizationSettings(
  fpuVisible:         Boolean = true,
  ccdVisible:         Boolean = true,
  candidatesVisible:  Boolean = true,
  patrolFieldVisible: Boolean = true,
  probeVisible:       Boolean = true
)

object AladinContainer {
  type Props = AladinContainer

  val coordinates = GenLens[AladinContainer](_.coordinates)

  // Generate a 5x5 grid of offsets separated by 5 arcsec
  def generateOffsetGrid(
    shiftP: Angle = Angle.Angle0,
    shiftQ: Angle = Angle.Angle0
  ): NonEmptyList[Offset] = {
    val step        = 5
    val shiftOffset = Offset(shiftP.p, shiftQ.q)
    val offsets     =
      for {
        i <- -2 to 2
        j <- -2 to 2
      } yield Offset(Angle.fromDoubleArcseconds(step * i).p,
                     Angle.fromDoubleArcseconds(step * j).q
      ) + shiftOffset
    NonEmptyList.fromListUnsafe(offsets.toList)
  }

  def baseGmosConf(gmosMode: GmosMode) =
    if (gmosMode == GmosMode.LongSlit) {
      BasicConfiguration.GmosSouthLongSlit(
        grating = GmosSouthGrating.R400_G5325,
        filter = GmosSouthFilter.HeII.some,
        fpu = GmosSouthFpu.LongSlit_2_00,
        centralWavelength = CentralWavelength(Wavelength.fromIntNanometers(500).get)
      )
    } else
      BasicConfiguration.GmosSouthImaging(
        filter = NonEmptyList.one(GmosSouthFilter.HeII)
      )

  val component = ScalaFnComponent[Props]: props =>
    for {
      viewOffset      <- useStateView(Offset.Zero)
      // View coordinates (in case the user pans)
      currentPos      <- useState(props.coordinates.offsetBy(Angle.Angle0, viewOffset.get))
      // Ref to the aladin component
      aladinRef       <- useState(none[Aladin])
      // resize detector
      resize          <- useResizeDetector
      flip            <- useState(true)
      // Toggle state for SVGVisualizationOverlay CSS
      visSettings     <- useState(VisualizationSettings())
      fullScreen      <- useStateView(AladinFullScreen.Normal)
      instrument      <- useState(InstrumentType.GMOS)
      gmosMode        <- useState(GmosMode.Imaging)
      gmosConf        <- useStateView(baseGmosConf(gmosMode.value))
      f2Conf          <- useState(
                           BasicConfiguration.Flamingos2LongSlit(
                             disperser = Flamingos2Disperser.R1200HK,
                             filter = Flamingos2Filter.H,
                             fpu = Flamingos2Fpu.LongSlit2
                           )
                         )
      portDisposition <- useState(PortDisposition.Side)
      posAngle        <- useState(Angle.Angle0)
      survey          <- useState(instrument.value match {
                           case InstrumentType.GMOS       => ImageSurvey.DSS
                           case InstrumentType.Flamingos2 => ImageSurvey.TWOMASS
                         })
    } yield
      // Grid of offsets
      val scienceOffsetGrid     = AladinContainer.generateOffsetGrid()
      val acquisitionOffsetGrid = AladinContainer.generateOffsetGrid(
        Angle.fromDoubleArcseconds(1.5),
        Angle.fromDoubleArcseconds(1.5)
      )

      /**
       * Called when the position changes, i.e. aladin pans. We want to offset the visualization to
       * keep the internal target correct
       */
      def onPositionChanged(u: PositionChanged): Callback = {
        val viewCoords     = Coordinates(u.ra, u.dec)
        val offsetFromView = props.coordinates.diff(viewCoords).offset
        currentPos.setState(Some(viewCoords)) *>
          viewOffset.set(offsetFromView)
      }

      val baseCoordinatesForAladin: String =
        currentPos.value
          .map(Coordinates.fromHmsDms.reverseGet)
          .getOrElse(Coordinates.fromHmsDms.reverseGet(props.coordinates))

      val screenOffset =
        currentPos.value.map(_.diff(props.coordinates).offset).getOrElse(Offset.Zero)

      def onZoom = (v: Fov) => props.fov.set(v)

      def customizeAladin(v: Aladin): Callback =
        aladinRef.setState(Some(v)) *>
          v.onZoomCB(onZoom) *> // re render on zoom
          v.onPositionChangedCB(onPositionChanged)

      val gs          = props.coordinates
      //
      // Get the appropriate configuration based on selected instrument and mode
      val currentConf = instrument.value match
        case InstrumentType.GMOS       => gmosConf.get.some
        case InstrumentType.Flamingos2 => f2Conf.value.some

      // Use the generated offset grids
      val scienceOffsetList     = scienceOffsetGrid.some
      val acquisitionOffsetList = acquisitionOffsetGrid.some

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
        id:       String,
        item:     String,
        getValue: VisualizationSettings => Boolean,
        setValue: (VisualizationSettings, Boolean) => VisualizationSettings,
        clazz:    Css = Css("toggle-container")
      ) =
        <.div(
          clazz,
          <.input(
            ^.`type`  := "checkbox",
            ^.id      := s"overlay-toggle-$id",
            ^.checked := getValue(visSettings.value),
            ^.onChange --> visSettings.setState(
              setValue(visSettings.value, !getValue(visSettings.value))
            )
          ),
          <.label(
            ^.htmlFor := s"overlay-toggle-$id",
            s"Show/Hide $item"
          )
        )

      def enumeratedSelect[A](
        id:       String,
        label:    String,
        value:    A,
        onChange: A => Callback,
        filter:   A => Boolean = (_: A) => true,
        display:  js.UndefOr[A => String] = js.undefined
      )(using E: Enumerated[A]): VdomElement =
        ReactFragment(
          <.label(^.htmlFor := id, s"$label: "),
          <.select(
            ^.id    := id,
            ^.value := value.tag,
            ^.onChange ==> ((r: ReactUIEventFromInput) =>
              E.fromTag(r.target.value).map(onChange).getOrElse(Callback.empty)
            )
          )(
            E.all
              .filter(filter)
              .map(item =>
                <.option(
                  ^.key   := item.tag,
                  ^.value := item.tag,
                  display.map(_(item)).getOrElse(item.tag.capitalize)
                )
              )
              .toTagMod
          )
        )

      def visibilityClasses = instrument.value match {
        case InstrumentType.GMOS       =>
          VisualizationStyles.GmosFpuVisible.when_(visSettings.value.fpuVisible) |+|
            VisualizationStyles.GmosCcdVisible.when_(
              visSettings.value.ccdVisible && gmosMode.value == GmosMode.Imaging
            ) |+|
            VisualizationStyles.GmosCandidatesAreaVisible.when_(
              visSettings.value.candidatesVisible
            ) |+|
            VisualizationStyles.GmosPatrolFieldVisible.when_(
              visSettings.value.patrolFieldVisible
            ) |+|
            VisualizationStyles.GmosProbeVisible.when_(visSettings.value.probeVisible)
        case InstrumentType.Flamingos2 =>
          VisualizationStyles.Flamingos2FpuVisible.when_(visSettings.value.fpuVisible) |+|
            VisualizationStyles.Flamingos2ScienceAreaVisible.when_(visSettings.value.ccdVisible) |+|
            VisualizationStyles.Flamingos2CandidatesAreaVisible.when_(
              visSettings.value.candidatesVisible
            ) |+|
            VisualizationStyles.Flamingos2PatrolFieldVisible.when_(
              visSettings.value.patrolFieldVisible
            ) |+|
            VisualizationStyles.Flamingos2ProbeArmVisible.when_(visSettings.value.probeVisible)
      }

      val scienceOffsetIndicators =
        offsetIndicators(
          scienceOffsetList,
          props.coordinates,
          posAngle.value,
          SequenceType.Science,
          VisualizationStyles.ScienceOffsetPosition,
          true
        )

      val acquisitionOffsetIndicators =
        offsetIndicators(
          acquisitionOffsetList,
          props.coordinates,
          posAngle.value,
          SequenceType.Science,
          VisualizationStyles.AcquisitionOffsetPosition,
          true
        )

      val offsetTargets =
        // order is important, scienc to be drawn above acq
        (acquisitionOffsetIndicators |+| scienceOffsetIndicators).flattenOption

      val lsMode = gmosConf.zoom(BasicConfiguration.gmosSouthLongSlit)

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
                  screenOffset,
                  props.coordinates,
                  List(
                    SVGTarget
                      .CrosshairTarget(props.coordinates, Css("science-target"), 10)
                      .some,
                    gs.some.map(SVGTarget.CircleTarget(_, Css("guidestar"), 3))
                  ).flatten ++ offsetTargets
                )
              ),
            ReactAladin(
              Css("react-aladin") |+| Css("test").when_(flip.value),
              AladinOptions(
                showReticle = true,
                showLayersControl = false,
                target = baseCoordinatesForAladin,
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
                baseCoordinatesForAladin,
                <.label("Pos: "),
                currentPos.value.toString,
                <.label("Offset: "),
                screenOffset.toStringOffset,
                <.label("PA: "),
                s"${posAngle.value.toDoubleDegrees}°",
                <.label("Science Offsets: "),
                s"5x5 grid (${scienceOffsetGrid.size} positions)",
                <.label("Acquisition Offsets: "),
                s"5x5 grid (${acquisitionOffsetGrid.size} positions)"
              ),
              <.div(
                Css("config-togglers"),
                instrument.value match {
                  case InstrumentType.GMOS       =>
                    gmosMode.value match {
                      case GmosMode.LongSlit =>
                        toggler(
                          "fpu",
                          "GMOS FPU",
                          _.fpuVisible,
                          (s, v) => s.copy(fpuVisible = v)
                        )
                      case GmosMode.Imaging  => "No FPU (Imaging)"
                    }
                  case InstrumentType.Flamingos2 => "FPU"
                },
                toggler("ccd", "Science CCD", _.ccdVisible, (s, v) => s.copy(ccdVisible = v)),
                toggler("candidates",
                        "Candidates Field",
                        _.candidatesVisible,
                        (s, v) => s.copy(candidatesVisible = v)
                ),
                toggler("patrol-field",
                        "Patrol Field",
                        _.patrolFieldVisible,
                        (s, v) => s.copy(patrolFieldVisible = v)
                ),
                toggler("probe", "Probe", _.probeVisible, (s, v) => s.copy(probeVisible = v))
              ),
              <.div(
                Css("config-controls"),
                <.label(^.htmlFor     := "instrument-selector", "Select Instrument: "),
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
                enumeratedSelect(
                  "port-selector",
                  "Select Port Disposition: ",
                  portDisposition.value,
                  portDisposition.setState
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
                    enumeratedSelect(
                      "gmos-mode-selector",
                      "Select GMOS Mode",
                      gmosMode.value,
                      m => gmosMode.setState(m) *> gmosConf.mod(_ => baseGmosConf(m))
                    ),
                    gmosMode.value match {
                      case GmosMode.LongSlit =>
                        lsMode.asView.map: lsMode =>
                          enumeratedSelect(
                            "fpu-selector",
                            "Select FPU: ",
                            lsMode.get.fpu,
                            fpu => lsMode.mod(_.copy(fpu = fpu)),
                            _.tag.startsWith("LongSlit"),
                            _.longName
                          )
                      case GmosMode.Imaging  =>
                        <.p(Css("imaging-mode-info"), "Imaging mode - no FPU selection needed")
                    }
                  )
                case InstrumentType.Flamingos2 =>
                  <.div(
                    Css("config-controls"),
                    enumeratedSelect(
                      "f2-fpu-selector",
                      "Select Flamingos2 FPU",
                      f2Conf.value.fpu,
                      fpu => f2Conf.setState(f2Conf.value.copy(fpu = fpu)),
                      _.tag.startsWith("LongSlit"),
                      _.longName
                    )
                  )
              },
              <.div(
                Css("config-controls"),
                enumeratedSelect(
                  "survey-selector",
                  "Select Survey",
                  survey.value,
                  survey.setState,
                  display = _.name
                )
              ),
              // Offset grid information
              <.div(
                Css("config-controls"),
                <.h5("Offset Grids"),
                <.p("Science: 5×5 grid, 5″ separation"),
                <.p("Acquisition: 5×5 grid, 5″ separation, +1.5″ shift"),
                <.p(s"Total positions: ${scienceOffsetGrid.size} each")
              )
            )
          )
        else EmptyVdom
      )
        .withRef(resize.ref)
}
