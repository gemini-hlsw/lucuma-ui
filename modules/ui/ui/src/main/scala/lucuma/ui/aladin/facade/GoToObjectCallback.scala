// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import japgolly.scalajs.react.Callback

import scala.scalajs.js

class GoToObjectCallback(succ: (Double, Double) => Callback, e: Callback) extends js.Object {
  val success: js.Function1[js.Array[Double], Unit] = (raDec: js.Array[Double]) =>
    succ(raDec(0), raDec(1)).runNow()
  val error: js.Function0[Unit]                     = () => e.runNow()
}
