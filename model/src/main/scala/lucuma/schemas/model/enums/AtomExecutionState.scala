// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.enums

import lucuma.core.util.Enumerated

enum AtomExecutionState(val tag: String) derives Enumerated:
  case NotStarted extends AtomExecutionState("NOT_STARTED")
  case Ongoing    extends AtomExecutionState("ONGOING")
  case Completed  extends AtomExecutionState("COMPLETED")
  case Abandoned  extends AtomExecutionState("ABANDONED")
