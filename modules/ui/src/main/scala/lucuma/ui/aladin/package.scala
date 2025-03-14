// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*

import scala.scalajs.js.JSConverters.*

/**
 * ALadin field of view angles horizontally and vertically
 *
 * @param x
 *   Horizontal (RA) field of view
 * @param y
 *   Vertical (Dec) field of view
 */
case class Fov(x: Angle, y: Angle)

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
  given enumCooFrame: EnumValue[CooFrame] = EnumValue.toLowerCaseString
  case J2000    extends CooFrame
  case J2000d   extends CooFrame
  case Galactic extends CooFrame

object CooFrame:
  def fromString(s: String): Option[CooFrame] =
    s match
      case "j2000"    => Some(J2000)
      case "j2000d"   => Some(J2000d)
      case "galactic" => Some(Galactic)
