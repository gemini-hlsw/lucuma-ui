// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

@js.native
trait PolylineOptions extends js.Object {
  var color: js.UndefOr[String]
}

object PolylineOptions {
  def apply(
    color: js.UndefOr[String] = js.undefined
  ): PolylineOptions = {
    val p = (new js.Object()).asInstanceOf[PolylineOptions]
    color.foreach(v => p.color = v)
    p
  }
}
