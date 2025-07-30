// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.enums

import lucuma.core.util.Enumerated

enum StepExecutionState(val tag: String) derives Enumerated:
  case NotStarted extends StepExecutionState("NOT_STARTED")
  case Ongoing    extends StepExecutionState("ONGOING")
  case Aborted    extends StepExecutionState("ABORTED")
  case Completed  extends StepExecutionState("COMPLETED")
  case Stopped    extends StepExecutionState("STOPPED")
  case Abandoned  extends StepExecutionState("ABANDONED")
