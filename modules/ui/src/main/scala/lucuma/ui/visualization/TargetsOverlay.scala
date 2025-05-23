// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.types.numeric.NonNegInt
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.svg_<^.*
import lucuma.ags.AgsAnalysis
import lucuma.core.enums.SequenceType
import lucuma.core.math.Coordinates
import lucuma.core.math.Offset
import lucuma.react.common.Css
import lucuma.react.common.ReactFnComponent
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.Tooltip
import lucuma.ui.aladin.Fov
import lucuma.ui.syntax.all.given

import scala.math.*

sealed trait SVGTarget derives Eq {
  def coordinates: Coordinates
  def css: Css
}

object SVGTarget {
  case class CircleTarget(
    coordinates: Coordinates,
    css:         Css,
    radius:      Double,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class CrosshairTarget(
    coordinates: Coordinates,
    css:         Css,
    side:        Double,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class ScienceTarget(
    coordinates: Coordinates,
    css:         Css,
    selectedCss: Css,
    side:        Double,
    selected:    Boolean,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class LineTo(
    coordinates: Coordinates,
    destination: Coordinates,
    css:         Css,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class GuideStarCandidateTarget(
    coordinates: Coordinates,
    css:         Css,
    radius:      Double,
    analysis:    AgsAnalysis.Usable,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class GuideStarTarget(
    coordinates: Coordinates,
    css:         Css,
    radius:      Double,
    analysis:    AgsAnalysis.Usable,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  case class OffsetIndicator(
    coordinates: Coordinates,
    pos:         NonNegInt,
    offset:      Offset,
    oType:       SequenceType,
    css:         Css,
    radius:      Double,
    title:       Option[String] = None
  ) extends SVGTarget derives Eq

  given Reusability[SVGTarget] = Reusability.byEq
}

case class TargetsOverlay(
  width:           Int,
  height:          Int,
  fov:             Fov,
  screenOffset:    Offset,
  baseCoordinates: Coordinates,
  targets:         List[SVGTarget]
) extends ReactFnProps(TargetsOverlay)

object TargetsOverlay
    extends ReactFnComponent[TargetsOverlay](p =>
      val pixx = p.fov.x.toMicroarcseconds / p.width
      val pixy = p.fov.y.toMicroarcseconds / p.height
      val maxP = max(pixx, pixy)

      val (x0, y0, maxX, maxY, minSide) =
        p.targets.foldLeft(
          (Double.MaxValue, Double.MaxValue, Double.MinValue, Double.MinValue, 0.0)
        ) { case ((x, y, w, h, s), target) =>
          val side         = target match {
            case SVGTarget.CrosshairTarget(_, _, sidePx, _) =>
              maxP * sidePx
            case _                                          =>
              0.0
          }
          val offset       = target.coordinates.diff(p.baseCoordinates).offset
          // Offset amount
          val (offP, offQ) = offset.micros
          (x.min(offP), y.min(offQ), w.max(offP), h.max(offQ), s.max(side))
        }

      val w0 = abs(maxX - x0)
      val h0 = abs(maxY - y0)

      val (x, y, w, h) =
        if (w0 == 0 || h0 == 0) (x0 - 2 * minSide, y0 - 2 * minSide, minSide * 2, minSide * 2)
        else (x0, y0, w0, h0)

      val (viewBoxX, viewBoxY, viewBoxW, viewBoxH) =
        calculateViewBox(x, y, w, h, p.fov, p.screenOffset)

      val targetsWithOffsets: List[(Double, Double, SVGTarget)] = p.targets
        .fmap: target =>
          val offset       = target.coordinates.diff(p.baseCoordinates).offset
          val (offP, offQ) = offset.micros // Offset amount
          (offP, offQ, target)

      // 24 October 2024 - scalafix failing to parse with fewer braces
      val guideStarTooltips: List[VdomNode] = p.targets.collect {
        case SVGTarget.GuideStarCandidateTarget(_, _, _, ags, _) =>
          Tooltip(clazz = VisualizationStyles.VisualizationTooltip,
                  targetCss = ags.target.selector
          )(
            GuideStarTooltip(ags)
          )
        case SVGTarget.GuideStarTarget(_, _, _, ags, _)          =>
          Tooltip(clazz = VisualizationStyles.VisualizationTooltip,
                  targetCss = ags.target.selector
          )(
            GuideStarTooltip(ags)
          )
      }

      val svg: VdomNode = <.svg(
        VisualizationStyles.TargetsSvg,
        ^.viewBox    := s"$viewBoxX $viewBoxY $viewBoxW $viewBoxH",
        canvasWidth  := s"${p.width}px",
        canvasHeight := s"${p.height}px",
        <.g(
          VisualizationStyles.JtsTargets,
          targetsWithOffsets
            .collect[VdomNode] {
              case (offP, offQ, SVGTarget.CircleTarget(_, css, radius, title))    =>
                val pointCss = VisualizationStyles.CircleTarget |+| css

                <.circle(
                  ^.cx := scale(offP),
                  ^.cy := scale(offQ),
                  ^.r  := scale(maxP * radius),
                  pointCss,
                  title.map(<.title(_))
                )
              case (offP, offQ, SVGTarget.CrosshairTarget(_, css, sidePx, title)) =>
                val pointCss = VisualizationStyles.CrosshairTarget |+| css

                val side = scale(maxP * sidePx)
                <.g(
                  <.line(
                    ^.x1 := scale(offP) - side,
                    ^.x2 := scale(offP) + side,
                    ^.y1 := scale(offQ),
                    ^.y2 := scale(offQ),
                    pointCss
                  ),
                  <.line(
                    ^.x1 := scale(offP),
                    ^.x2 := scale(offP),
                    ^.y1 := scale(offQ) - side,
                    ^.y2 := scale(offQ) + side,
                    pointCss
                  ),
                  title.map(<.title(_))
                )

              case (offP,
                    offQ,
                    SVGTarget.ScienceTarget(_, css, selectedCss, sidePx, selected, title)
                  ) =>
                val pointCss = VisualizationStyles.CrosshairTarget |+| css

                CrossTarget(offP, offQ, maxP, sidePx, pointCss, selectedCss, selected, title)

              case (offP, offQ, SVGTarget.GuideStarCandidateTarget(_, css, radius, ags, _)) =>
                val pointCss = VisualizationStyles.GuideStarCandidateTarget |+| css
                GuideStarTarget(offP, offQ, maxP, radius, pointCss, ags)

              case (offP, offQ, SVGTarget.GuideStarTarget(_, css, radius, ags, _)) =>
                val pointCss = VisualizationStyles.GuideStarTarget |+| css
                GuideStarTarget(offP, offQ, maxP, radius, pointCss, ags)

              case (offP, offQ, SVGTarget.OffsetIndicator(_, idx, o, oType, css, radius, title)) =>
                val pointCss = VisualizationStyles.OffsetPosition |+| css
                OffsetSVG(offP, offQ, maxP, radius, pointCss, oType, idx, o)

              case (offP, offQ, SVGTarget.LineTo(_, d, css, title)) =>
                val destOffset = d.diff(p.baseCoordinates).offset
                // Offset amount
                val destP      =
                  Offset.P.signedDecimalArcseconds.get(destOffset.p).toDouble * 1e6

                val destQ =
                  Offset.Q.signedDecimalArcseconds.get(destOffset.q).toDouble * 1e6

                val pointCss = VisualizationStyles.ArrowBetweenTargets |+| css
                <.line(
                  ^.x1 := scale(offP),
                  ^.x2 := scale(destP),
                  ^.y1 := scale(offQ),
                  ^.y2 := scale(destQ),
                  pointCss,
                  title.map(<.title(_))
                )
            }
            .toTagMod
        )
      )

      val textTooltip: VdomNode = Tooltip(
        clazz = VisualizationStyles.VisualizationTooltip,
        targetCss = VisualizationStyles.VisualizationTooltipTarget
      )

      val tooltips: VdomNode = // Remount when targets change, so that the tooltips are reattached
        React.Fragment.withKey(p.targets.length)((textTooltip +: guideStarTooltips)*)

      React.Fragment(svg, tooltips)
    )
