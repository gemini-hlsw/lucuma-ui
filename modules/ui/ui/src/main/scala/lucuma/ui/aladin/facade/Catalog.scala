// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

@js.native
trait AladinCatalog extends js.Object:
  def addSources(s: AladinSource | js.Array[AladinSource]): Unit = js.native
