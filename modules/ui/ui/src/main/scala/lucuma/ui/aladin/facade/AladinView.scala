// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.HTMLCanvasElement

import scala.scalajs.js

enum ViewMode(val value: Int):
  case Pan               extends ViewMode(0)
  case Select            extends ViewMode(1)
  case ToolSimbadPointer extends ViewMode(2)
  case ToolColorPicker   extends ViewMode(3)

@js.native
trait AladinView extends js.Object:
  val zoom: Zoom                        = js.native
  val realDragging: js.UndefOr[Boolean] = js.native
  val catalogCanvas: HTMLCanvasElement  = js.native
  val aladin: JsAladin                  = js.native
  def setMode(mode: Int): Unit          = js.native
  def requestRedraw(): Unit             = js.native
