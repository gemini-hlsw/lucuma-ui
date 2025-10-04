// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyList
import cats.effect.IO
import cats.syntax.all.*
import crystal.react.View
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.GmosSouthFilter
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.enums.GmosSouthGrating
import lucuma.core.enums.PortDisposition
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.react.primereact.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import lucuma.ui.aladin.*
import lucuma.ui.primereact.CheckboxView
import lucuma.ui.primereact.FormEnumDropdownView
import lucuma.ui.primereact.given_ViewLike_View
import monocle.macros.GenLens
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

import scala.concurrent.duration.*

case class AladinControlsPanel(
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
  zoomDuration:    View[FiniteDuration],
  panningEnabled:  View[Boolean],
  mousePosition:   Option[Coordinates]
) extends ReactFnProps[AladinControlsPanel](AladinControlsPanel.component)

object AladinControlsPanel {
  type Props = AladinControlsPanel
  given Logger[IO] = NoOpLogger[IO]

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
      currentPos                     <- useStateView(props.coordinates)
      acquisitionOffsetGrid          <- useStateView(
                                          AladinContainer
                                            .generateOffsetGrid(
                                              stepArcsec = 5.0,
                                              shiftP = Angle.fromDoubleArcseconds(1.5),
                                              shiftQ = Angle.fromDoubleArcseconds(1.5)
                                            )
                                            .some
                                        )
      scienceOffsetDialogVisible     <- useStateView(false)
      acquisitionOffsetDialogVisible <- useStateView(false)
    } yield

      val viewOffset =
        props.viewOffset.withOnMod(AladinStorage.saveOffset)

      val vizOffset = viewOffset.get

      val viewFov =
        props.fov.withOnMod(AladinStorage.saveFov)

      val lsMode = props.configuration.zoom(BasicConfiguration.gmosSouthLongSlit)

      <.div(
        Css("aladin-controls-panel"),
        <.div(
          Css("aladin-info"),
          <.div(
            Css("info-row"),
            <.span(Css("info-label"), "FOV: "),
            <.span(Css("info-value"), props.fov.get.toStringAngle),
            <.span(Css("info-label"), "Target coord: "),
            <.span(Css("info-value"), Coordinates.fromHmsDms.reverseGet(props.coordinates)),
            <.span(Css("info-label"), "Current pos: "),
            <.span(Css("info-value"), currentPos.get.toString)
          ),
          <.div(
            Css("info-row"),
            <.span(Css("info-label"), "Offset: "),
            <.span(Css("info-value"), vizOffset.toStringOffset),
            <.span(Css("info-label"), "PA: "),
            <.span(Css("info-value"), s"${props.posAngle.get.toDoubleDegrees}°")
          ),
          <.div(
            Css("info-row"),
            <.span(Css("info-label"), "Mouse pos: "),
            <.span(Css("info-value"),
                   props.mousePosition.fold("N/A")(c => Coordinates.fromHmsDms.reverseGet(c))
            )
          )
        ),
        <.div(
          Css("controls-grid"),
          // Instrument Configuration
          <.div(
            Css("control-section"),
            <.h4("Instrument Configuration"),
            <.div(
              Css("input-group"),
              FormEnumDropdownView(
                id = NonEmptyString.unsafeFrom("instrument-selector"),
                value = props.instrument,
                label = "Instrument"
              )
            ),
            <.div(
              Css("input-group"),
              FormEnumDropdownView(
                id = NonEmptyString.unsafeFrom("port-selector"),
                value = props.portDisposition,
                label = "Port Disposition"
              )
            ),
            props.instrument.get match {
              case InstrumentType.GMOS       =>
                val gmosMode = props.configuration.zoom((config: BasicConfiguration) =>
                  config match {
                    case _: BasicConfiguration.GmosSouthLongSlit => GmosMode.LongSlit
                    case _: BasicConfiguration.GmosSouthImaging  => GmosMode.Imaging
                    case _                                       => GmosMode.Imaging
                  }
                ) { (f: GmosMode => GmosMode) => (config: BasicConfiguration) =>
                  val currentMode = config match {
                    case _: BasicConfiguration.GmosSouthLongSlit => GmosMode.LongSlit
                    case _: BasicConfiguration.GmosSouthImaging  => GmosMode.Imaging
                    case _                                       => GmosMode.Imaging
                  }
                  val newMode     = f(currentMode)
                  baseGmosConf(newMode)
                }
                <.div(
                  Css("input-group"),
                  FormEnumDropdownView(
                    id = NonEmptyString.unsafeFrom("gmos-mode-selector"),
                    value = gmosMode,
                    label = "GMOS Mode"
                  )
                )
              case InstrumentType.Flamingos2 => EmptyVdom
            },
            <.div(
              Css("input-group"),
              <.label(^.htmlFor := "pa-input", "Position Angle (°)"),
              InputNumber(
                id = "pa-input",
                value = props.posAngle.get.toDoubleDegrees,
                min = 0.0,
                max = 360.0,
                onValueChange = e =>
                  if (e.value != null) {
                    val doubleValue = e.value.asInstanceOf[Double]
                    props.posAngle.set(Angle.fromDoubleDegrees(doubleValue))
                  } else {
                    Callback.empty
                  }
              )
            )
          ),
          // Mode-specific Configuration
          <.div(
            Css("control-section"),
            <.h4("Mode Configuration"),
            props.instrument.get match {
              case InstrumentType.GMOS       =>
                props.configuration.get match {
                  case _: BasicConfiguration.GmosSouthLongSlit =>
                    <.div(
                      Css("input-group"),
                      lsMode.asView.map: lsMode =>
                        FormEnumDropdownView[View, GmosSouthFpu](
                          id = NonEmptyString.unsafeFrom("fpu-selector"),
                          value = lsMode.zoom(GenLens[BasicConfiguration.GmosSouthLongSlit](_.fpu)),
                          label = "FPU"
                        )
                    )
                  case _: BasicConfiguration.GmosSouthImaging  =>
                    <.p(Css("mode-info"), "Imaging mode - no FPU selection needed")
                  case _                                       =>
                    <.p(Css("mode-info"), "GMOS configuration")
                }
              case InstrumentType.Flamingos2 =>
                props.configuration.get match {
                  case f2: BasicConfiguration.Flamingos2LongSlit =>
                    <.p(Css("mode-info"), "Flamingos2 FPU: ", f2.fpu.longName)
                  case _                                         =>
                    <.p(Css("mode-info"), "Flamingos2 configuration")
                }
            },
            <.div(
              Css("input-group"),
              FormEnumDropdownView(
                id = NonEmptyString.unsafeFrom("survey-selector"),
                value = props.survey,
                label = "Survey"
              )
            )
          ),
          // View Offset Controls
          <.div(
            Css("control-section"),
            <.h4("View Offset"),
            <.div(
              Css("offset-controls"),
              <.div(
                Css("input-group"),
                <.label(^.htmlFor := "p-offset-input", "P Offset (″)"),
                InputNumber(
                  id = "p-offset-input",
                  value = Offset.P.signedDecimalArcseconds.get(viewOffset.get.p).toDouble,
                  onValueChange = e =>
                    if (e.value != null) {
                      val doubleValue = e.value.asInstanceOf[Double]
                      viewOffset.mod(offset =>
                        Offset(Offset.P(Angle.fromDoubleArcseconds(doubleValue)), offset.q)
                      )
                    } else {
                      Callback.empty
                    }
                )
              ),
              <.div(
                Css("input-group"),
                <.label(^.htmlFor := "q-offset-input", "Q Offset (″)"),
                InputNumber(
                  id = "q-offset-input",
                  value = Offset.Q.signedDecimalArcseconds.get(viewOffset.get.q).toDouble,
                  onValueChange = e =>
                    if (e.value != null) {
                      val doubleValue = e.value.asInstanceOf[Double]
                      viewOffset.mod(offset =>
                        Offset(offset.p, Offset.Q(Angle.fromDoubleArcseconds(doubleValue)))
                      )
                    } else {
                      Callback.empty
                    }
                )
              ),
              Button(
                label = "Reset Offset & FOV",
                onClick = viewOffset.set(Offset.Zero) *>
                  viewFov.set(AladinStorage.DefaultFov),
                severity = Button.Severity.Secondary,
                size = Button.Size.Small
              )
            )
          ),
          // Visualization Settings
          <.div(
            Css("control-section"),
            <.h4("Visualization Settings"),
            <.div(
              Css("toggles-grid"),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("fpu-visible"),
                  value = props.visSettings.zoom(_.fpuVisible)((f: Boolean => Boolean) =>
                    (vs: VisualizationSettings) => vs.copy(fpuVisible = f(vs.fpuVisible))
                  ),
                  label = "FPU Visible"
                )
              ),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("ccd-visible"),
                  value = props.visSettings.zoom(_.ccdVisible)((f: Boolean => Boolean) =>
                    (vs: VisualizationSettings) => vs.copy(ccdVisible = f(vs.ccdVisible))
                  ),
                  label = "CCD/Science Area Visible"
                )
              ),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("candidates-visible"),
                  value = props.visSettings.zoom(_.candidatesAreaVisible)((f: Boolean => Boolean) =>
                    (vs: VisualizationSettings) =>
                      vs.copy(candidatesAreaVisible = f(vs.candidatesAreaVisible))
                  ),
                  label = "Guide Star Area Visible"
                )
              ),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("patrol-field-visible"),
                  value = props.visSettings.zoom(_.patrolFieldVisible)((f: Boolean => Boolean) =>
                    (vs: VisualizationSettings) =>
                      vs.copy(patrolFieldVisible = f(vs.patrolFieldVisible))
                  ),
                  label = "Patrol Field Visible"
                )
              ),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("probe-visible"),
                  value = props.visSettings.zoom(_.probeVisible)((f: Boolean => Boolean) =>
                    (vs: VisualizationSettings) => vs.copy(probeVisible = f(vs.probeVisible))
                  ),
                  label = "Probe Visible"
                )
              ),
              <.div(
                Css("toggle-container"),
                CheckboxView(
                  id = NonEmptyString.unsafeFrom("panning-enabled"),
                  value = props.panningEnabled,
                  label = "Panning Enabled"
                )
              )
            ),
            <.div(
              Css("input-group"),
              <.label(^.htmlFor := "zoom-duration-input", "Zoom Duration (ms)"),
              InputNumber(
                id = "zoom-duration-input",
                value = props.zoomDuration.get.toMillis.toDouble,
                min = 50.0,
                max = 2000.0,
                onValueChange = e =>
                  if (e.value != null) {
                    val doubleValue = e.value.asInstanceOf[Double]
                    props.zoomDuration.set(doubleValue.toLong.millis)
                  } else {
                    Callback.empty
                  }
              )
            )
          )
        )
      )
}
