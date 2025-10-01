// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyList
import cats.data.NonEmptyMap
import cats.syntax.all.*
import crystal.react.View
import crystal.react.hooks.*
import japgolly.scalajs.react.*
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
import lucuma.core.enums.SequenceType
import lucuma.core.geom.Area
import lucuma.core.math.*
import lucuma.core.model.SiderealTracking
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import lucuma.ui.aladin.*
import lucuma.ui.aladin.facade.ViewMode
import lucuma.ui.reusability
import lucuma.ui.visualization.*
import monocle.macros.GenLens

import scala.concurrent.duration.*

case class AladinContainer(
  fov:             View[Fov],
  coordinates:     Coordinates,
  viewOffset:      View[Offset],
  scienceOffset:   View[Option[NonEmptyList[Offset]]],
  posAngle:        View[Angle],
  instrument:      View[InstrumentType],
  configuration:   View[BasicConfiguration],
  portDisposition: View[PortDisposition],
  survey:          View[ImageSurvey],
  visSettings:     View[VisualizationSettings],
  zoomDuration:    FiniteDuration = 200.millis,
  panningEnabled:  View[Boolean],
  mousePosition:   View[Option[Coordinates]]
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

extension (o: Offset)
  def toStringOffset: String =
    f"(p: ${o.p.toAngle.toMicroarcseconds / 1e6}%2.3f, q: ${o.q.toAngle.toMicroarcseconds / 1e6}%2.3f)"

enum InstrumentType:
  case GMOS, Flamingos2

object InstrumentType:
  given Enumerated[InstrumentType] = Enumerated.from(GMOS, Flamingos2).withTag(_.toString)
  given Display[InstrumentType]    = Display.by[InstrumentType](_.toString, _.toString)

enum GmosMode:
  case LongSlit, Imaging

object GmosMode:
  given Enumerated[GmosMode] = Enumerated.from(LongSlit, Imaging).withTag(_.toString)
  given Display[GmosMode]    = Display.by[GmosMode](_.toString, _.toString)

given Display[PortDisposition] = Display.by[PortDisposition](_.tag, _.tag)
given Display[GmosSouthFpu]    = Display.by[GmosSouthFpu](_.tag, _.longName)
given Display[ImageSurvey]     = Display.by[ImageSurvey](_.tag, _.name)

case class VisualizationSettings(
  fpuVisible:            Boolean = true,
  ccdVisible:            Boolean = true,
  candidatesAreaVisible: Boolean = true,
  patrolFieldVisible:    Boolean = true,
  probeVisible:          Boolean = true
)

object AladinContainer {
  type Props = AladinContainer

  val coordinates = GenLens[AladinContainer](_.coordinates)

  // Generate a 5x5 grid of offsets separated by 5 arcsec
  def generateOffsetGrid(
    stepArcsec: Double = 5.0,
    shiftP:     Angle = Angle.Angle0,
    shiftQ:     Angle = Angle.Angle0
  ): NonEmptyList[Offset] = {
    val step        = Angle.fromDoubleArcseconds(stepArcsec)
    val shiftOffset = Offset(Offset.P(shiftP), Offset.Q(shiftQ))
    val offsets     =
      for {
        i <- -2 to 2
        j <- -2 to 2
      } yield Offset(Offset.P(step * i), Offset.Q(step * j)) + shiftOffset
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
      // View coordinates (in case the user pans)
      currentPos            <- useStateView(props.coordinates)
      // Ref to the aladin component
      aladinRef             <- useState(none[Aladin])
      // resize detector
      resize                <- useResizeDetector
      flip                  <- useState(true)
      fullScreen            <- useStateView(AladinFullScreen.Normal)
      acquisitionOffsetGrid <- useStateView(
                                 AladinContainer.generateOffsetGrid(
                                   stepArcsec = 5.0,
                                   shiftP = Angle.fromDoubleArcseconds(1.5),
                                   shiftQ = Angle.fromDoubleArcseconds(1.5)
                                 )
                               )
    } yield
      def isRelevantChange(prevOffset: Offset, newOffset: Offset): Boolean =
        prevOffset.distance(newOffset).toMicroarcseconds > (1e6 / 120)

      val viewOffset = props.viewOffset.withOnMod((prevOffset, newOffset) =>
        val relevantChange = isRelevantChange(prevOffset, newOffset)
        AladinStorage.saveOffset(newOffset).when_(relevantChange)
      )

      val vizOffset = viewOffset.get

      /**
       * Called when the position changes, i.e. aladin pans. We want to offset the visualization to
       * keep the internal target correct
       */
      def onPositionChanged = (u: PositionChanged) =>
        val viewCoords     = Coordinates(u.ra, u.dec)
        val newOffset      = viewCoords.diff(props.coordinates).offset
        val relevantChange = isRelevantChange(viewOffset.get, newOffset) && viewCoords
          .angularDistance(currentPos.get)
          .toMicroarcseconds > (1e6 / 50)
        currentPos.set(viewCoords) *>
          viewOffset.set(newOffset).when_(relevantChange)

      val aladinCoordsStr: String =
        Coordinates.fromHmsDms.reverseGet(props.coordinates) // Use stable initial coordinates

      def onZoom(a: Aladin) = (v: Fov) =>
        props.fov.set(v) *>
          AladinStorage.saveFov(v).unless_(a.isZooming)

      def customizeAladin(v: Aladin): Callback =
        aladinRef.setState(Some(v)) *>
          v.onZoomCB(onZoom(v)) *> // re render on zoom
          v.onPositionChangedCB(onPositionChanged) *>
          v.onMouseMoveCB(s => props.mousePosition.set(Some(Coordinates(s.ra, s.dec)))) *>
          v.setViewMode(ViewMode.Pan)

      val gs = props.coordinates

      // Get the appropriate configuration based on selected instrument and mode
      val currentConf = props.configuration.get.some

      // Use the generated offset grids
      val scienceOffsetList     = props.scienceOffset.get
      val acquisitionOffsetList = acquisitionOffsetGrid.get.some

      val shapes = props.instrument.get match {
        case InstrumentType.GMOS       =>
          GmosGeometry.gmosGeometry(
            props.coordinates,
            scienceOffsetList,
            acquisitionOffsetList,
            props.posAngle.get.some,
            currentConf,
            props.portDisposition.get,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                props.posAngle.get,
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
            props.posAngle.get.some,
            currentConf,
            props.portDisposition.get,
            AgsAnalysis
              .Usable(
                GuideProbe.GmosOIWFS,
                GuideStarCandidate(0L, SiderealTracking.const(gs), None).get,
                GuideSpeed.Fast,
                AgsGuideQuality.DeliversRequestedIq,
                props.posAngle.get,
                Area.MinArea
              )
              .some,
            VisualizationStyles.GuideStarCandidateVisible
          )
      }

      def visibilityClasses = props.instrument.get match {
        case InstrumentType.GMOS       =>
          val isImaging = props.configuration.get match {
            case _: BasicConfiguration.GmosSouthImaging => true
            case _                                      => false
          }
          VisualizationStyles.GmosFpuVisible.when_(props.visSettings.get.fpuVisible) |+|
            VisualizationStyles.GmosCcdVisible.when_(
              props.visSettings.get.ccdVisible && isImaging
            ) |+|
            VisualizationStyles.GmosCandidatesAreaVisible.when_(
              props.visSettings.get.candidatesAreaVisible
            ) |+|
            VisualizationStyles.GmosPatrolFieldVisible.when_(
              props.visSettings.get.patrolFieldVisible
            ) |+|
            VisualizationStyles.GmosProbeVisible.when_(props.visSettings.get.probeVisible)
        case InstrumentType.Flamingos2 =>
          VisualizationStyles.Flamingos2FpuVisible.when_(props.visSettings.get.fpuVisible) |+|
            VisualizationStyles.Flamingos2ScienceAreaVisible.when_(
              props.visSettings.get.ccdVisible
            ) |+|
            VisualizationStyles.Flamingos2CandidatesAreaVisible.when_(
              props.visSettings.get.candidatesAreaVisible
            ) |+|
            VisualizationStyles.Flamingos2PatrolFieldVisible.when_(
              props.visSettings.get.patrolFieldVisible
            ) |+|
            VisualizationStyles.Flamingos2ProbeArmVisible.when_(props.visSettings.get.probeVisible)
      }

      val scienceOffsetIndicators =
        offsetIndicators(
          scienceOffsetList,
          props.coordinates,
          props.posAngle.get,
          SequenceType.Science,
          VisualizationStyles.ScienceOffsetPosition,
          true
        )

      val acquisitionOffsetIndicators =
        offsetIndicators(
          acquisitionOffsetList,
          props.coordinates,
          props.posAngle.get,
          SequenceType.Science,
          VisualizationStyles.AcquisitionOffsetPosition,
          true
        )

      val offsetTargets =
        // order is important, scienc to be drawn above acq
        (acquisitionOffsetIndicators |+| scienceOffsetIndicators).flattenOption

      // Use explicit reusability that excludes target changes
      given Reusability[AladinOptions] = reusability.withoutTarget

      <.div(
        Css("react-aladin-container"),
        aladinRef.value.map(
          AladinZoomControl(
            _,
            duration = props.zoomDuration.toMillis.toInt
          )
        ),
        AladinFullScreenControl(fullScreen),
        // This happens during a second render. If we let the height to be zero, aladin
        // will take it as 1. This height ends up being a denominator, which, if low,
        // will make aladin request a large amount of tiles and end up freezing the demo.
        if (resize.height.exists(_ >= 100))
          <.div(
            Css("aladin-viewer"),
            (resize.width, resize.height, shapes.flatMap(NonEmptyMap.fromMap))
              .mapN((w, h, s) =>
                SVGVisualizationOverlay(
                  w,
                  h,
                  props.fov.get,
                  vizOffset,
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
                  vizOffset,
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
                showReticle = false,
                showLayersControl = false,
                target = aladinCoordsStr,
                fov = props.fov.get.x,
                showGotoControl = false,
                showZoomControl = false,
                showCooGridControl = false,
                showStatusBar = false,
                showFov = false,
                showFrame = false,
                showCooLocation = false,
                showFullscreenControl = false,
                showProjectionControl = false,
                survey = props.survey.get
              ),
              customize = customizeAladin(_),
              panningEnabled = props.panningEnabled.get
            )
          )
        else EmptyVdom
      )
        .withRef(resize.ref)
}
