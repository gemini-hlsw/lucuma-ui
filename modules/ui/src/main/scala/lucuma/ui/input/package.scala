// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import lucuma.core.optics.Format

package object input {
  type InputFormat[A] = Format[String, A]
}
