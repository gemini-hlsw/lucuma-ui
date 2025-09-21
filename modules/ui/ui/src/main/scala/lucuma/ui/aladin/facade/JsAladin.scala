// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("aladin-lite", "Aladin")
class JsAladin(val aladinDiv: Element, val options: AladinOptions) extends js.Object {
  def setImageSurvey(s:           String): Unit                                       = js.native
  def setBaseImageLayer(s:        String): Unit                                       = js.native
  def getBaseImageLayer(): HpxImageSurvey                                             = js.native
  def setOverlayImageLayer(i:     HpxImageSurvey): Unit                               = js.native
  def getOverlayImageLayer(): HpxImageSurvey                                          = js.native
  def getFovForObject(objectName: String, callback: js.Function1[Double, Unit]): Unit = js.native
  def createImageSurvey(
    id:       String,
    name:     String,
    rootUrl:  String,
    cooFrame: String,
    maxOrder: Double,
    options:  js.Object
  ): HpxImageSurvey = js.native
  def addCatalog(c:               AladinCatalog): Unit                                = js.native
  def addOverlay(c:               AladinOverlay): Unit                                = js.native
  def gotoRaDec(ra:               Double, dec:      Double): Unit                     = js.native
  def getRaDec(): js.Array[Double]                                                    = js.native
  def gotoObject(q:               String, cb:       GoToObjectCallback): Unit         = js.native
  def animateToRaDec(ra:          Double, dec:      Double, time: Double): Unit       = js.native
  def recalculateView(): Unit                                                         = js.native
  def increaseZoom(): Unit                                                            = js.native
  def decreaseZoom(): Unit                                                            = js.native
  def getZoomFactor(): Double                                                         = js.native
  def setZoomFactor(f:            Double): js.Object                                  = js.native
  def requestRedraw(): Unit                                                           = js.native
  def toggleFullscreen(): Unit                                                        = js.native
  def fixLayoutDimensions(): Unit                                                     = js.native
  def view: AladinView                                                                = js.native
  def getParentDiv(): Element                                                         = js.native
  def getSize(): js.Array[Double]                                                     = js.native
  def getFov(): js.Array[Double]                                                      = js.native
  def setFov(fovDegrees:          Double): js.Array[Double]                           = js.native
  def box(): Unit                                                                     = js.native
  def pix2world(x:                Double, y:        Double): js.Array[Double]         = js.native
  def world2pix(x:                Double, y:        Double): js.Array[Double]         = js.native
  def on(n:                       String, f:        js.Function): Unit                = js.native
}
