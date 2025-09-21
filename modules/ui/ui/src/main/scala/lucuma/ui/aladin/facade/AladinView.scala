// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.html

import scala.scalajs.js

@js.native
trait AladinView extends js.Object:
  val aladin: js.Object                 = js.native
  val view: ZoomHold                    = js.native
  val zoom: Zoom                        = js.native
  val imageCanvas: html.Canvas          = js.native
  val catalogCanvas: html.Canvas        = js.native
  val realDragging: js.UndefOr[Boolean] = js.native
