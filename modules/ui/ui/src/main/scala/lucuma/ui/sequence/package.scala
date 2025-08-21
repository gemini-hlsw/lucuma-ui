// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.option.*
import eu.timepit.refined.types.numeric.PosInt
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.ObserveClass
import lucuma.core.math.SignalToNoise
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2FpuMask
import lucuma.core.model.sequence.gmos
import lucuma.core.util.NewType
import lucuma.react.SizePx
import lucuma.react.common.*
import lucuma.react.primereact.Tag
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.tooltip.*
import lucuma.react.syntax.*
import lucuma.schemas.model.enums.StepExecutionState
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles
import lucuma.ui.utils.Render
import lucuma.ui.utils.zipWithMappedIndex

object SequenceRowHeight:
  val Regular: SizePx   = 25.toPx
  val WithExtra: SizePx = 60.toPx

object StepIndex extends NewType[PosInt]:
  val One: StepIndex = StepIndex(PosInt.unsafeFrom(1))
type StepIndex = StepIndex.Type

private def renderStepType(icon: VdomNode, tooltip: String): VdomNode =
  <.span(icon).withTooltip(content = tooltip, showDelay = 100, position = Tooltip.Position.Bottom)

extension (stepTypeDisplay: StepTypeDisplay)
  private def icon: VdomNode =
    stepTypeDisplay match
      case StepTypeDisplay.Bias   => SequenceIcons.StepType.Bias
      case StepTypeDisplay.Dark   => SequenceIcons.StepType.Dark
      case StepTypeDisplay.Arc    => SequenceIcons.StepType.Arc
      case StepTypeDisplay.Flat   => SequenceIcons.StepType.Flat
      case StepTypeDisplay.Object => SequenceIcons.StepType.Object

given Render[StepTypeDisplay] = Render.by: stepType =>
  renderStepType(stepType.icon, stepType.name)

given Render[Option[DatasetQaState]] = Render.by: qaState =>
  LucumaIcons.Circle.withClass:
    qaState match
      case Some(DatasetQaState.Pass)   => LucumaStyles.IndicatorOK
      case Some(DatasetQaState.Usable) => LucumaStyles.IndicatorWarning
      case Some(DatasetQaState.Fail)   => LucumaStyles.IndicatorFail
      case None                        => LucumaStyles.IndicatorUnknown

given Render[StepExecutionState] = Render.by:
  case StepExecutionState.NotStarted | StepExecutionState.Completed =>
    EmptyVdom
  case other @ (StepExecutionState.Ongoing | StepExecutionState.Aborted |
      StepExecutionState.Stopped | StepExecutionState.Abandoned) =>
    Tag(
      other match
        case StepExecutionState.Ongoing   => "Ongoing"
        case StepExecutionState.Aborted   => "Aborted"
        case StepExecutionState.Stopped   => "Stopped Early"
        case StepExecutionState.Abandoned => "Abandoned",
      severity = other match
        case StepExecutionState.Ongoing | StepExecutionState.Stopped => Tag.Severity.Info
        case _                                                       => Tag.Severity.Danger
    )

extension [D, R <: SequenceRow[D]](list: List[R])
  /* Zip list with `StepIndex` and return the indexed list and the next index */
  def zipWithStepIndex(
    initial: StepIndex = StepIndex(PosInt.unsafeFrom(1))
  ): (List[(R, StepIndex)], StepIndex) =
    (list.zipWithMappedIndex(index => initial.modifyValue(i => PosInt.unsafeFrom(i.value + index))),
     initial.modifyValue(i => PosInt.unsafeFrom(i.value + list.size))
    )

extension [D](instrumentConfig: D)
  def shouldShowAcquisitionSn: Boolean =
    instrumentConfig match
      case gmos.DynamicConfig.GmosNorth(_, _, _, _, _, _, None)                       => true
      case gmos.DynamicConfig.GmosSouth(_, _, _, _, _, _, None)                       => true
      case Flamingos2DynamicConfig(_, _, Flamingos2FpuMask.Imaging, _, _, _, _, _, _) => true
      case _                                                                          => false

extension [D](step: Step[D])
  def getSignalToNoise(
    signalToNoise: Option[SignalToNoise]
  ): Option[SignalToNoise] =
    step.observeClass match
      case ObserveClass.Acquisition if step.instrumentConfig.shouldShowAcquisitionSn =>
        signalToNoise
      case ObserveClass.Science                                                      => signalToNoise
      case _                                                                         => none
