// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import lucuma.core.optics.Format

package object forms {
  type InputFormat[A] = Format[String, A]

  type ChangeAuditor[A] = (String, InputFormat[A]) => AuditResult[A]
}
