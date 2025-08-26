// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import cats.data.NonEmptyMap
import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.svg_<^.*
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.jts.JtsShape
import lucuma.core.geom.jts.interpreter.given
import lucuma.core.math.Offset
import lucuma.react.common.Css
import lucuma.react.common.ReactFnProps
import lucuma.ui.aladin.Fov
import lucuma.ui.syntax.all.given
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.util.PolygonExtracter

import scala.jdk.CollectionConverters.*

case class SVGVisualizationOverlay(
  width:        Int,
  height:       Int,
  fov:          Fov,
  screenOffset: Offset,
  shapes:       NonEmptyMap[Css, ShapeExpression],
  clazz:        Css = Css.Empty
) extends ReactFnProps(SVGVisualizationOverlay.component)

object SVGVisualizationOverlay {
  private type Props = SVGVisualizationOverlay

  extension (g: Geometry)
    // This deserves an explanation.
    // In the visualization we have included several geometriens includig the patrol field which
    // can be the intersection between all the patrol fields geometries at each pos angle position.
    //
    // Normally the intersection would be a polygon but in some edge cases the intersection becomes
    // disjoint and turns into a geometry collection.
    //
    // In this component (SVGVisualizationOverlay) we are going to make a union of all the
    // geometries in the visualization (including the patrol field) to calculate the envelope
    // and thus get the overall size.
    //
    // However the union is not defined for disjoint sets in the geometry library and we get an
    // exception.
    //
    // In the method below, as a workaround we detect geometry collections and convert them to
    // multi-polygons which are supported in a union even if they are disjoint
    //
    // It is debatable whether we should always do this for unions
    //
    // Some references:
    // https://app.shortcut.com/lucuma/story/5685/explore-java-lang-illegalargumentexception-operation-does-not-support-geometrycollection-arguments
    // https://github.com/locationtech/jts/issues/476#issuecomment-533451819
    //
    def resolveGeometryCollections =
      if (g.isGeometryCollection)
        // it is possible to have a geometry collection with something else than polygons but
        // not in our use case
        val pgs = PolygonExtracter
          .getPolygons(g)
          .asScala
          .collect:
            case p: Polygon => p
        g.getFactory.createMultiPolygon(pgs.toArray)
      else g

  private def forGeometry(css: Css, g: Geometry): VdomNode =
    g match {
      case p: Polygon            =>
        val points = p.getCoordinates
          .map(c => s"${scale(c.x)},${scale(c.y)}")
          .mkString(" ")
        <.polygon(css |+| VisualizationStyles.JtsPolygon, ^.points := points)
      case p: GeometryCollection =>
        <.g(
          css |+| VisualizationStyles.JtsCollection,
          p.geometries.map(forGeometry(css, _)).toTagMod
        )
      case _                     => EmptyVdom
    }

  private val component =
    ScalaFnComponent[Props] { p =>
      // Render the svg
      val evald: NonEmptyMap[Css, JtsShape] = p.shapes
        .fmap(_.eval)
        .map {
          case jts: JtsShape => jts
          case x             => sys.error(s"Whoa unexpected shape type: $x")
        }

      val composite = evald
        .map(_.g.resolveGeometryCollections)
        .reduce(using geometryUnionSemigroup)

      val envelope = composite.getBoundary.getEnvelopeInternal

      // We should calculate the viewbox of the whole geometry
      // dimension in micro arcseconds
      val (x, y, w, h) =
        (envelope.getMinX, envelope.getMinY, envelope.getWidth, envelope.getHeight)

      val (viewBoxX, viewBoxY, viewBoxW, viewBoxH) =
        calculateViewBox(x, y, w, h, p.fov, p.screenOffset)

      val svg = <.svg(
        VisualizationStyles.VisualizationSvg |+| p.clazz,
        ^.viewBox    := s"$viewBoxX $viewBoxY $viewBoxW $viewBoxH",
        canvasWidth  := s"${p.width}px",
        canvasHeight := s"${p.height}px",
        <.g(
          ^.transform := s"scale(1, -1)",
          evald.toNel
            .map { case (css, shape) =>
              forGeometry(css, shape.g)
            }
            .toList
            .toTagMod
        )
      )
      svg
    }
}
