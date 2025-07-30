// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

/**
 * Aladin pixel scala in degrees per pixel
 *
 * @param x
 * @param y
 */
case class PixelScale(x: Double, y: Double)

object PixelScale {
  val Default: PixelScale = PixelScale(1, 1)
}

@js.native
trait JsPositionChanged extends js.Object:
  val ra: Double
  val dec: Double
  val dragging: Boolean

@js.native
trait JsMouseMoved extends js.Object:
  val ra: Double
  val dec: Double
  val x: Double
  val y: Double

@js.native
trait AladinColor extends js.Object

@js.native
trait ColorMap extends js.Object:
  def update(a: String): Unit = js.native

@js.native
trait AladinFootprint extends js.Object

@js.native
trait AladinPolyline extends js.Object

@js.native
trait AladinCircle extends js.Object

@js.native
trait HpxImageSurvey extends js.Object:
  def setAlpha(a: Double): Unit = js.native
  def getColorMap(): ColorMap   = js.native
