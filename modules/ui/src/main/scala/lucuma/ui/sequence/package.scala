// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.core.model.sequence.StepConfig
import lucuma.ui.utils.Render
import cats.syntax.eq.*
import lucuma.core.enums.GcalLampType
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.floatingui.syntax.*
import lucuma.react.floatingui.Placement

private def renderStepType(icon: VdomNode, tooltip: String): VdomNode =
  <.span(icon).withTooltip(tooltip, Placement.Right)

given Render[StepConfig] = Render.by:
  case StepConfig.Bias                                                           =>
    renderStepType(SequenceIcons.StepType.Bias, "Bias")
  case StepConfig.Dark                                                           =>
    renderStepType(SequenceIcons.StepType.Dark, "Dark")
  case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Arc  =>
    renderStepType(SequenceIcons.StepType.Arc, "Arc")
  case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Flat =>
    renderStepType(SequenceIcons.StepType.Flat, "Flat")
  case StepConfig.Science(_, _)                                                  =>
    renderStepType(SequenceIcons.StepType.Object, "Object")
  case _                                                                         =>
    EmptyVdom
