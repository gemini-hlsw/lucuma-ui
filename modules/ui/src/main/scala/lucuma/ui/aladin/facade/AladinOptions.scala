// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import scala.scalajs.js

// This will be the props object used from JS-land
@js.native
trait AladinOptions extends js.Object {
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
  var log: js.UndefOr[Boolean]
}
