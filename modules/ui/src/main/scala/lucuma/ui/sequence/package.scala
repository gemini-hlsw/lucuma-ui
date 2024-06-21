// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.eq.*
import eu.timepit.refined.types.numeric.PosInt
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.ObserveClass
import lucuma.core.math.SignalToNoise
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.util.NewType
import lucuma.react.common.*
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.tooltip.*
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles
import lucuma.ui.utils.*
import lucuma.ui.utils.Render

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

extension [D, R <: SequenceRow[D]](list: List[R])
  /* Zip list with `StepIndex` and return the indexed list and the next index */
  def zipWithStepIndex(
    initial: StepIndex = StepIndex(PosInt.unsafeFrom(1))
  ): (List[(R, StepIndex)], StepIndex) =
    (list.zipWithMappedIndex(index => initial.modifyValue(i => PosInt.unsafeFrom(i.value + index))),
     initial.modifyValue(i => PosInt.unsafeFrom(i.value + list.size))
    )

extension (sn: Option[SignalToNoise])
  def showForFutureStep[D](r: Step[D]): Option[SignalToNoise] =
    sn.filter: _ =>
      r.instrumentConfig match
        case DynamicConfig.GmosNorth(_, _, _, _, _, _, fpu) =>
          val showScience = r.observeClass === ObserveClass.Science
          val showAcq     = r.observeClass === ObserveClass.Acquisition && fpu.isEmpty
          showScience || showAcq

        case DynamicConfig.GmosSouth(_, _, _, _, _, _, fpu) =>
          val showScience = r.observeClass === ObserveClass.Science
          val showAcq     = r.observeClass === ObserveClass.Acquisition && fpu.isEmpty
          showScience || showAcq

        case _ => false
