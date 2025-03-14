// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.react.aladin

import cats.Semigroup
import cats.data.NonEmptyMap
import cats.syntax.all.*
import crystal.react.hooks.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.svg_<^.*
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.jts.JtsShape
import lucuma.core.geom.jts.interpreter.given
import lucuma.core.math.Offset
import lucuma.react.common.*
import lucuma.ui.aladin.*
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.Polygon

import scala.math.*

final case class VisualizationOverlay(
  width:        Int,
  height:       Int,
  fov:          Fov,
  screenOffset: Offset,
  shapes:       NonEmptyMap[Css, ShapeExpression],
  clazz:        Css = Css.Empty
) extends ReactFnProps[VisualizationOverlay](VisualizationOverlay.component)

object VisualizationOverlay {
  type Props = VisualizationOverlay
  val geometryUnionSemigroup: Semigroup[Geometry] =
    Semigroup.instance(_.union(_))

  // The values on the geometry are in microarcseconds
  // They are fairly large and break is some browsers
  // We apply a scaling factor uniformily
  val scale = (v: Double) => rint(v / 1000)

  val JtsPolygon    = Css("viz-polygon")
  val JtsCollection = Css("viz-collecttion")
  val JtsGuides     = Css("viz-guides")
  val JtsSvg        = Css("visualization-overlay-svg")

  def forGeometry(css: Css, g: Geometry): VdomNode =
    g match {
      case p: Polygon            =>
        val points = p.getCoordinates
          .map(c => s"${scale(c.x)},${scale(c.y)}")
          .mkString(" ")
        <.polygon(css |+| JtsPolygon, ^.points := points)
      case p: GeometryCollection =>
        <.g(
          css |+| JtsCollection,
          p.geometries.map(forGeometry(css, _)).toTagMod
        )
      case _                     => EmptyVdom
    }

  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")

  val component =
    ScalaFnComponent
      .withHooks[Props]
      .useSerialStateBy(_.shapes)
      .useMemoBy((_, s) => s) { (_, _) => shapes =>
        // Render the svg
        val evald: NonEmptyMap[Css, JtsShape] = shapes.value.value
          .fmap(_.eval)
          .map {
            case jts: JtsShape => jts
            case x             => sys.error(s"Whoa unexpected shape type: $x")
          }
        val composite                         = evald
          .map(_.g)
          .reduce(geometryUnionSemigroup)
        (evald, composite)
      }
      .render { (p, _, shapes) =>
        val (evaldShapes, composite) = shapes.value
        val envelope                 = composite.getBoundary.getEnvelopeInternal

        // We should calculate the viewbox of the whole geometry
        // dimension in micro arcseconds
        val (x, y, w, h) =
          (envelope.getMinX, envelope.getMinY, envelope.getWidth, envelope.getHeight)

        // Shift factors on x/y, basically the percentage shifted on x/y
        val px = abs(x / w) - 0.5
        val py = abs(y / h) - 0.5
        // scaling factors on x/y
        val sx = p.fov.x.toMicroarcseconds / w
        val sy = p.fov.y.toMicroarcseconds / h

        // Offset amount
        val offP =
          Offset.P.signedDecimalArcseconds.get(p.screenOffset.p).toDouble * 1e6

        val offQ =
          Offset.Q.signedDecimalArcseconds.get(p.screenOffset.q).toDouble * 1e6

        // Do the shifting and offseting via viewbox
        val viewBoxX = scale(x + px * w) * sx + scale(offP)
        val viewBoxY = scale(y + py * h) * sy + scale(offQ)
        val viewBoxW = scale(w) * sx
        val viewBoxH = scale(h) * sy

        val viewBox = s"$viewBoxX $viewBoxY $viewBoxW $viewBoxH"

        val svg = <.svg(
          JtsSvg |+| p.clazz,
          ^.viewBox    := viewBox,
          canvasWidth  := s"${p.width}px",
          canvasHeight := s"${p.height}px",
          <.g(
            ^.transform := s"scale(1, -1)",
            evaldShapes.toNel
              .map { case (css, shape) =>
                forGeometry(css, shape.g)
              }
              .toList
              .toTagMod
          ),
          <.rect(
            JtsGuides,
            ^.x         := scale(x),
            ^.y         := scale(y),
            ^.width     := scale(w),
            ^.height    := scale(h)
          ),
          <.line(
            JtsGuides,
            ^.x1        := scale(x),
            ^.y1        := scale(y),
            ^.x2        := scale(x + w),
            ^.y2        := scale(y + h)
          ),
          <.line(
            JtsGuides,
            ^.x1        := scale(x),
            ^.y1        := -scale(y),
            ^.x2        := scale(x + w),
            ^.y2        := -scale(y + h)
          )
        )
        svg
      }
}
