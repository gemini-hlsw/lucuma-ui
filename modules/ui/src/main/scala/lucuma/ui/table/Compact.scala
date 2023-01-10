// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import react.semanticui.collections.table.TableCompact.Compact

sealed trait Compact

object Compact extends Compact:
  object Very extends Compact
