// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@js.native
trait ZoomHold extends js.Object:
  def zoom: Zoom

@js.native
trait Zoom extends js.Object:
  // The function in js is called `apply` which is a reserved word in Scala
  // Thus we use @JSName to rename it to `applyZoom`
  @JSName("apply")
  def applyZoom(o: js.Object): Unit = js.native
  def stopAnimation(): Unit         = js.native
