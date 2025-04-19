// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.Eq
import cats.derived.*
import cats.syntax.eq.*
import cats.syntax.option.*
import lucuma.core.enums.GcalLampType
import lucuma.core.model.sequence.StepConfig

enum StepTypeDisplay(val name: String) derives Eq:
  case Bias   extends StepTypeDisplay("Bias")
  case Dark   extends StepTypeDisplay("Dark")
  case Arc    extends StepTypeDisplay("Arc")
  case Flat   extends StepTypeDisplay("Flat")
  case Object extends StepTypeDisplay("Object")

object StepTypeDisplay:
  val fromStepConfig: StepConfig => Option[StepTypeDisplay] =
    case StepConfig.Bias                                                           =>
      StepTypeDisplay.Bias.some
    case StepConfig.Dark                                                           =>
      StepTypeDisplay.Dark.some
    case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Arc  =>
      StepTypeDisplay.Arc.some
    case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Flat =>
      StepTypeDisplay.Flat.some
    case StepConfig.Science                                                        =>
      StepTypeDisplay.Object.some
    case _                                                                         =>
      none
