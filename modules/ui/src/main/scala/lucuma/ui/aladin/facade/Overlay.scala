// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

@js.native
trait AladinOverlay extends js.Object:
  def addFootprints(s: js.Array[AladinOverlay.Shapes]): Unit = js.native
  def add(s:           AladinOverlay.Shapes): Unit           = js.native

object AladinOverlay:
  type Shapes = AladinCircle | AladinFootprint | AladinPolyline
