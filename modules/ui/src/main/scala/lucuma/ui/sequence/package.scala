// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.core.model.sequence.StepConfig
import lucuma.ui.utils.Render
import cats.syntax.eq.*
import lucuma.core.enums.GcalLampType

given Render[StepConfig] = Render.by:
  case StepConfig.Bias                                                           => "B"
  case StepConfig.Dark                                                           => "D"
  case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Arc  => "A"
  case gcal @ StepConfig.Gcal(_, _, _, _) if gcal.lampType === GcalLampType.Flat => "F"
  case StepConfig.Science(_, _)                                                  => "O"
  case _                                                                         => ""
