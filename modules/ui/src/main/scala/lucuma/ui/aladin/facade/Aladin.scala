// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.Element

import scala.annotation.nowarn
import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import scala.scalajs.js.annotation.*

// This will be the props object used from JS-land
@js.native
trait SourceDraw extends js.Object:
  val fov: js.Array[Double]
  val width: Double
  val height: Double

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

// This will be the props object used from JS-land
@js.native
trait AladinOptions extends js.Object {
  var mountNodeClass: String
  var fov: js.UndefOr[Double]
  var target: js.UndefOr[String]
  var survey: js.UndefOr[String]
  var cooFrame: js.UndefOr[String]
  var showReticle: js.UndefOr[Boolean]
  var showZoomControl: js.UndefOr[Boolean]
  var showFullscreenControl: js.UndefOr[Boolean]
  var showLayersControl: js.UndefOr[Boolean]
  var showGotoControl: js.UndefOr[Boolean]
  var showCooGridControl: js.UndefOr[Boolean]
  var showSettingsControl: js.UndefOr[Boolean]
  var showStatusBar: js.UndefOr[Boolean]
  var showCooLocation: js.UndefOr[Boolean]
  var showProjectionControl: js.UndefOr[Boolean]
  var showShareControl: js.UndefOr[Boolean]
  var showSimbadPointerControl: js.UndefOr[Boolean]
  var showFrame: js.UndefOr[Boolean]
  var showCoordinates: js.UndefOr[Boolean]
  var showFov: js.UndefOr[Boolean]
  var fullScreen: js.UndefOr[Boolean]
  var reticleColor: js.UndefOr[String]
  var reticleSize: js.UndefOr[Double]
  var imageSurvey: js.UndefOr[String]
  var baseImageLayer: js.UndefOr[String]
  var customize: js.UndefOr[JsAladin => Unit]
}

@js.native
trait AladinSource extends js.Object:
  val x: Double       = js.native
  val y: Double       = js.native
  val data: js.Object = js.native

@js.native
trait AladinView extends js.Object:
  val aladin: js.Object                = js.native
  def fixLayoutDimensions(): js.Object = js.native

@js.native
trait JsAladin extends js.Object {
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
  def requestRedraw(): Unit                                                           = js.native
  def toggleFullscreen(): Unit                                                        = js.native
  def view: AladinView                                                                = js.native
  def getParentDiv(): Element                                                         = js.native
  def getSize(): js.Array[Double]                                                     = js.native
  def getFov(): js.Array[Double]                                                      = js.native
  def setZoom(fovDegrees:         Double): js.Array[Double]                           = js.native
  def box(): Unit                                                                     = js.native
  def pix2world(x:                Double, y:        Double): js.Array[Double]         = js.native
  def world2pix(x:                Double, y:        Double): js.Array[Double]         = js.native
  def on(n:                       String, f:        js.Function): Unit                = js.native
}

@js.native
@JSImport("aladin-lite", JSImport.Default)
@nowarn
object A extends js.Object {
  def aladin(divSelector: String, options:  AladinOptions): JsAladin   = js.native
  def aladin(divSelector: Element, options: AladinOptions): JsAladin   = js.native
  def catalog(c:          CatalogOptions): AladinCatalog               = js.native
  def graphicOverlay(c:   OverlayOptions): AladinOverlay               = js.native
  def polygon(raDecArray: js.Array[js.Array[Double]]): AladinFootprint = js.native
  def polyline(
    raDecArray: js.Array[js.Array[Double]],
    o:          js.UndefOr[PolylineOptions]
  ): AladinPolyline = js.native
  def circle(
    ra:        Double,
    dec:       Double,
    radiusDeg: Double,
    options:   js.UndefOr[js.Object] = js.undefined
  ): AladinCircle = js.native
  def source(
    ra:      Double,
    dec:     Double,
    data:    js.UndefOr[js.Object] = js.undefined,
    options: js.UndefOr[js.Object] = js.undefined
  ): AladinSource = js.native
  def marker(
    ra:      Double,
    dec:     Double,
    data:    js.UndefOr[js.Object] = js.undefined,
    options: js.UndefOr[js.Object] = js.undefined
  ): AladinSource = js.native
  def catalogFromURL(
    url:             String,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined,
    useProxy:        Boolean = false
  ): AladinCatalog = js.native
  def catalogFromSimbad(
    url:             String,
    radius:          Double,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
  def catalogFromNED(
    url:             String,
    radius:          Double,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
  def catalogFromVizieR(
    vizCatId:        String,
    target:          String,
    radius:          Double,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
}
