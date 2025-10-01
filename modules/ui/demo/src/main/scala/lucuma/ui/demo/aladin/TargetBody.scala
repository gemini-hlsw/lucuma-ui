// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.syntax.all.*
import crystal.react.hooks.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.Flamingos2Disperser
import lucuma.core.enums.Flamingos2Filter
import lucuma.core.enums.Flamingos2Fpu
import lucuma.core.enums.PortDisposition
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.react.gridlayout.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.ui.aladin.*
import lucuma.ui.hooks.*
import lucuma.ui.reusability.given

import scala.concurrent.duration.*
import scala.scalajs.js

@js.native
trait SourceData extends js.Object {
  var name: String  = js.native
  var size: Double  = js.native
  var otype: String = js.native
}

object SourceData {
  def apply(name: String, size: Double, otype: String): SourceData = {
    val p = (new js.Object()).asInstanceOf[SourceData]
    p.name = name
    p.size = size
    p.otype = otype
    p
  }

}

case class AladinTile(s: Size, c: Coordinates)
    extends ReactFnProps[AladinTile](AladinTile.component)

object AladinTile {
  type Props = AladinTile

  protected given Reusability[Props] = Reusability.never

  val targetH = 16
  val targetW = 12

  private val layoutLg: Layout = Layout(
    List(
      LayoutItem(x = 0, y = 0, w = targetW, h = 16, i = "target"),
      LayoutItem(x = 0, y = 16, w = 12, h = 12, i = "controls")
    )
  )

  private val layoutMd: Layout = Layout(
    List(
      LayoutItem(x = 0, y = 0, w = targetW, h = 16, i = "target"),
      LayoutItem(x = 0, y = 16, w = 10, h = 12, i = "controls")
    )
  )

  private val layouts: Map[BreakpointName, (Int, Int, Layout)] =
    Map(
      (BreakpointName.lg, (1200, 12, layoutLg)),
      (BreakpointName.md, (996, 10, layoutMd))
      // (BreakpointName.sm, (768, 8, layout)),
      // (BreakpointName.xs, (480, 6, layout))
    )

  given Reusability[Fov] = Reusability.derive

  val component =
    ScalaFnComponent[Props]: props =>
      for {
        s               <- useResizeDetector
        viewOffset      <- useStateView(AladinStorage.loadOffset)
        fov             <- useStateView(AladinStorage.loadFov)
        scienceOffset   <- useStateView(AladinContainer.generateOffsetGrid().some)
        posAngle        <- useStateView(Angle.Angle0)
        instrument      <- useStateView(InstrumentType.GMOS)
        configuration   <-
          useStateView[BasicConfiguration](AladinContainer.baseGmosConf(GmosMode.Imaging))
        portDisposition <- useStateView(PortDisposition.Side)
        survey          <- useStateView(ImageSurvey.DSS)
        visSettings     <- useStateView(VisualizationSettings())
        zoomDuration    <- useStateView(200.millis)
        panningEnabled  <- useStateView(true)
        mousePosition   <- useStateView(none[Coordinates])
        _               <- useEffectWithDeps(instrument.get): instr =>
                             val newSurvey = instr match {
                               case InstrumentType.GMOS       => ImageSurvey.DSS
                               case InstrumentType.Flamingos2 => ImageSurvey.TWOMASS
                             }
                             val newConfig = instr match {
                               case InstrumentType.GMOS       => AladinContainer.baseGmosConf(GmosMode.Imaging)
                               case InstrumentType.Flamingos2 =>
                                 BasicConfiguration.Flamingos2LongSlit(
                                   disperser = Flamingos2Disperser.R1200HK,
                                   filter = Flamingos2Filter.H,
                                   fpu = Flamingos2Fpu.LongSlit2
                                 )
                             }
                             survey.set(newSurvey) *> configuration.set(newConfig)
      } yield <.div(
        ^.height := "100%",
        ^.width  := "100%",
        ResponsiveReactGridLayout(
          width = s.width.foldMap(_.toInt),
          containerPadding = (1, 1),
          rowHeight = 30,
          draggableHandle = ".tileTitle",
          useCSSTransforms = false, // Not ideal, but fixes flicker on first update (0.18.3).
          layouts = layouts
        )(
          <.div(
            ^.height := "100%",
            ^.width  := "100%",
            ^.key    := "target",
            ^.cls    := "tile",
            AladinContainer(
              fov,
              props.c,
              viewOffset,
              scienceOffset,
              posAngle,
              instrument,
              configuration,
              portDisposition,
              survey,
              visSettings,
              zoomDuration.get,
              panningEnabled,
              mousePosition
            )
          ),
          <.div(
            ^.height := "100%",
            ^.width  := "100%",
            ^.key    := "controls",
            ^.cls    := "tile",
            AladinControlsPanel(
              fov,
              props.c,
              viewOffset,
              scienceOffset,
              posAngle,
              instrument,
              configuration,
              portDisposition,
              survey,
              visSettings,
              zoomDuration,
              panningEnabled,
              mousePosition.get
            )
          )
        )
      )

}

case class TargetBody(
) extends ReactFnProps[TargetBody](TargetBody.component) {}

object TargetBody:
  private type Props = TargetBody

  protected given Reusability[Props] = Reusability.derive

  val coords = (RightAscension.fromStringHMS.getOption("02:09:33.319"),
                Declination.fromStringSignedDMS.getOption("-4:37:31.11")
  ).mapN(Coordinates.apply).getOrElse(Coordinates.Zero)

  val component =
    ScalaFnComponent[Props]: _ =>
      for {
        _ <- useTheme()
        s <- useResizeDetector
      } yield AladinTile(
        Size(s.height.foldMap(_.toDouble), s.width.foldMap(_.toDouble)),
        coords
      )
