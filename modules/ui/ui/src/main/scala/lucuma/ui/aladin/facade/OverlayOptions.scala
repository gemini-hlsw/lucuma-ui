// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

// This will be the props object used from JS-land
@js.native
trait OverlayOptions extends js.Object {
  var color: js.UndefOr[String]
  var name: js.UndefOr[String]
  var lineWidth: js.UndefOr[Double]
}

object OverlayOptions {
  def apply(
    name:      js.UndefOr[String] = js.undefined,
    color:     js.UndefOr[String] = js.undefined,
    lineWidth: js.UndefOr[Double] = js.undefined
  ): OverlayOptions = {
    val p = (new js.Object()).asInstanceOf[OverlayOptions]
    name.foreach(v => p.name = v)
    color.foreach(v => p.color = v)
    lineWidth.foreach(v => p.lineWidth = v)
    p
  }
}
