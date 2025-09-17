// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import lucuma.core.math.*
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*

import scala.scalajs.js

type Aladin        = lucuma.ui.aladin.facade.JsAladin
type AladinOptions = lucuma.ui.aladin.facade.AladinOptions

/**
 * ALadin field of view angles horizontally and vertically
 *
 * @param x
 *   Horizontal (RA) field of view
 * @param y
 *   Vertical (Dec) field of view
 */
case class Fov(x: Angle, y: Angle) {
  def toStringAngle: String =
    f"(x/RA: ${x.toDoubleDegrees}%2.3f, y/Dec: ${y.toDoubleDegrees}%1.3f)"
}

object Fov:
  def square(a: Angle): Fov = Fov(a, a)

case class PositionChanged(ra: RightAscension, dec: Declination, dragging: Boolean)

object PositionChanged:
  def fromJs(p: JsPositionChanged): PositionChanged =
    PositionChanged(
      RightAscension.fromDoubleDegrees(p.ra),
      Declination.fromDoubleDegrees(p.dec).getOrElse(Declination.Zero),
      p.dragging
    )

case class MouseMoved(ra: RightAscension, dec: Declination, x: Double, y: Double)

object MouseMoved:
  def fromJs(p: JsMouseMoved): MouseMoved =
    MouseMoved(
      RightAscension.fromDoubleDegrees(p.ra),
      Declination.fromDoubleDegrees(p.dec).getOrElse(Declination.Zero),
      p.x,
      p.y
    )

enum CooFrame:
  case J2000    extends CooFrame
  case J2000d   extends CooFrame
  case Galactic extends CooFrame

object CooFrame:
  given EnumValue[CooFrame] = EnumValue.toLowerCaseString

  def fromString(s: String): Option[CooFrame] =
    s match
      case "j2000"    => Some(J2000)
      case "j2000d"   => Some(J2000d)
      case "galactic" => Some(Galactic)

enum ImageSurvey(val tag: String, val name: String, val id: String) derives Enumerated:
  // See
  // https://github.com/cds-astro/aladin-lite/blob/master/src/js/DefaultHiPSList.js
  case DSS     extends ImageSurvey("dss_color", "DSS", "P/DSS2/color")
  case TWOMASS extends ImageSurvey("twomass_color", "2MASS", "P/2MASS/color")

object AladinOptions:
  val Default: AladinOptions = apply()

  def apply(
    fov:                      js.UndefOr[Angle] = js.undefined,
    target:                   js.UndefOr[String] = js.undefined,
    survey:                   js.UndefOr[ImageSurvey] = js.undefined,
    cooFrame:                 js.UndefOr[CooFrame] = js.undefined,
    showReticle:              js.UndefOr[Boolean] = js.undefined,
    showZoomControl:          js.UndefOr[Boolean] = js.undefined,
    showFullscreenControl:    js.UndefOr[Boolean] = js.undefined,
    showLayersControl:        js.UndefOr[Boolean] = js.undefined,
    showGotoControl:          js.UndefOr[Boolean] = js.undefined,
    showCooGridControl:       js.UndefOr[Boolean] = js.undefined,
    showSettingsControl:      js.UndefOr[Boolean] = js.undefined,
    showStatusBar:            js.UndefOr[Boolean] = js.undefined,
    showCooLocation:          js.UndefOr[Boolean] = js.undefined,
    showProjectionControl:    js.UndefOr[Boolean] = js.undefined,
    showShareControl:         js.UndefOr[Boolean] = js.undefined,
    showSimbadPointerControl: js.UndefOr[Boolean] = js.undefined,
    showFrame:                js.UndefOr[Boolean] = js.undefined,
    showCoordinates:          js.UndefOr[Boolean] = js.undefined,
    showFov:                  js.UndefOr[Boolean] = js.undefined,
    fullScreen:               js.UndefOr[Boolean] = js.undefined,
    reticleColor:             js.UndefOr[String] = js.undefined,
    reticleSize:              js.UndefOr[Double] = js.undefined,
    imageSurvey:              js.UndefOr[String] = js.undefined,
    baseImageLayer:           js.UndefOr[String] = js.undefined,
    log:                      js.UndefOr[Boolean] = false
  ): AladinOptions = {
    val p = new js.Object().asInstanceOf[AladinOptions]
    fov.foreach(v => p.fov = v.toDoubleDegrees)
    target.foreach(v => p.target = v)
    survey.foreach(v => p.survey = v.id)
    cooFrame.foreach(v => p.cooFrame = v.toJs)
    reticleColor.foreach(v => p.reticleColor = v: String)
    reticleSize.foreach(v => p.reticleSize = v)
    imageSurvey.foreach(v => p.imageSurvey = v)
    baseImageLayer.foreach(v => p.baseImageLayer = v)
    showReticle.foreach(v => p.showReticle = v)
    showZoomControl.foreach(v => p.showZoomControl = v)
    showFullscreenControl.foreach(v => p.showFullscreenControl = v)
    showLayersControl.foreach(v => p.showLayersControl = v)
    showGotoControl.foreach(v => p.showGotoControl = v)
    showCooGridControl.foreach(v => p.showCooGridControl = v)
    showSettingsControl.foreach(v => p.showSettingsControl = v)
    showStatusBar.foreach(v => p.showStatusBar = v)
    showCooLocation.foreach(v => p.showCooLocation = v)
    showProjectionControl.foreach(v => p.showProjectionControl = v)
    showShareControl.foreach(v => p.showShareControl = v)
    showSimbadPointerControl.foreach(v => p.showSimbadPointerControl = v)
    showFrame.foreach(v => p.showFrame = v)
    showCoordinates.foreach(v => p.showCoordinates = v)
    showFov.foreach(v => p.showFov = v)
    fullScreen.foreach(v => p.fullScreen = v)
    log.foreach(v => p.log = v)
    p
  }
