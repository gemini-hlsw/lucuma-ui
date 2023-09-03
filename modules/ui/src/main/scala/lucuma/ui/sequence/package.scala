// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import eu.timepit.refined.types.numeric.PosInt
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.NewType
import lucuma.react.common.*
import lucuma.react.floatingui.Placement
import lucuma.react.floatingui.syntax.*
import lucuma.ui.utils.Render
import lucuma.ui.utils.*

object StepIndex extends NewType[PosInt]
type StepIndex = StepIndex.Type

private def renderStepType(icon: VdomNode, tooltip: String): VdomNode =
  <.span(icon).withTooltip(tooltip, Placement.Right)

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

extension [D, R <: SequenceRow[D]](list: List[R])
  def zipWithStepIndex: List[(R, StepIndex)] =
    list.zipWithMappedIndex(i => StepIndex(PosInt.unsafeFrom(i + 1)))
