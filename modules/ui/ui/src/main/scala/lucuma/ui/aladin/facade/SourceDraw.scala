// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

// This will be the props object used from JS-land
@js.native
trait SourceDraw extends js.Object:
  val fov: js.Array[Double]
  val width: Double
  val height: Double
