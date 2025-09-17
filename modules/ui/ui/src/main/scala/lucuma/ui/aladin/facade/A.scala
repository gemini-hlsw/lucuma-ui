// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("aladin-lite", JSImport.Default)
object A extends js.Object {
  def init: js.Promise[Unit]                                           = js.native
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
