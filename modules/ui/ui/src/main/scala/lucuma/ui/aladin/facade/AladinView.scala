// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

@js.native
trait AladinView extends js.Object:
  val zoom: Zoom                        = js.native
  val realDragging: js.UndefOr[Boolean] = js.native
