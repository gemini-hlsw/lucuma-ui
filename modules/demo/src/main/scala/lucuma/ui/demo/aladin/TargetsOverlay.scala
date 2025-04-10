// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.svg_<^.*
import lucuma.core.math.Coordinates
import lucuma.core.math.Offset
import lucuma.react.common.*
import lucuma.ui.aladin.Fov
import lucuma.ui.reusability.given

import scala.annotation.nowarn
import scala.math.*

final case class TargetsOverlay(
  width:           Int,
  height:          Int,
  fov:             Fov,
  screenOffset:    Offset,
  baseCoordinates: Coordinates,
  targets:         List[SVGTarget]
) extends ReactFnProps[TargetsOverlay](TargetsOverlay.component)

@nowarn
object TargetsOverlay {
  type Props = TargetsOverlay
  given Reusability[Double] = Reusability.double(1)
  given Reusability[Fov]    = Reusability.derive
  given Reusability[Props]  = Reusability.derive

  val JtsSvg    = Css("targets-overlay-svg")
  val JtsGuides = Css("viz-guides")

  val scale = (v: Double) => rint(v / 1000)

  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")
  val component    =
    ScalaFnComponent
      .withReuse[Props] { p =>
        val (x, y, maxX, maxY) =
          p.targets.foldLeft((Double.MaxValue, Double.MaxValue, Double.MinValue, Double.MinValue)) {
            case ((x, y, w, h), target) =>
              val offset = target.coordinates.diff(p.baseCoordinates).offset
              // Offset amount
              val offP   =
                Offset.P.signedDecimalArcseconds.get(offset.p).toDouble * 1e6

              val offQ =
                Offset.Q.signedDecimalArcseconds.get(offset.q).toDouble * 1e6

              (x.min(offP), y.min(offQ), w.max(offP), h.max(offQ))
          }

        val w = abs(maxX - x)
        val h = abs(maxY - y)

        // Shift factors on x/y, basically the percentage shifted on x/y
        val px   = abs(x / w) - 0.5
        val py   = abs(y / h) - 0.5
        // scaling factors on x/y
        val sx   = p.fov.x.toMicroarcseconds / w
        val sy   = p.fov.y.toMicroarcseconds / h
        val pixx = p.fov.x.toMicroarcseconds / p.width
        val pixy = p.fov.y.toMicroarcseconds / p.height
        val maxP = max(pixx, pixy)

        // Offset amount
        val offP =
          Offset.P.signedDecimalArcseconds.get(p.screenOffset.p).toDouble * 1e6

        val offQ =
          Offset.Q.signedDecimalArcseconds.get(p.screenOffset.q).toDouble * 1e6

        val viewBoxX = scale(x + px * w) * sx + scale(offP)
        val viewBoxY = scale(y + py * h) * sy + scale(offQ)
        val viewBoxW = scale(w) * sx
        val viewBoxH = scale(h) * sy

        val viewBox = s"$viewBoxX $viewBoxY $viewBoxW $viewBoxH"
        val svg     = <.svg(
          JtsSvg,
          ^.viewBox    := viewBox,
          canvasWidth  := s"${p.width}px",
          canvasHeight := s"${p.height}px",
          <.rect(
            JtsGuides,
            ^.x      := scale(x),
            ^.y      := scale(y),
            ^.width  := scale(w),
            ^.height := scale(h)
          ),
          <.line(
            JtsGuides,
            ^.x1     := scale(x),
            ^.y1     := scale(y),
            ^.x2     := scale(x + w),
            ^.y2     := scale(y + h)
          ),
          <.line(
            JtsGuides,
            ^.x1     := scale(x),
            ^.y1     := scale(y + h),
            ^.x2     := scale(x + w),
            ^.y2     := scale(y)
          ),
          p.targets.collect {
            case SVGTarget.CircleTarget(coordinates, css, radius, title)    =>
              val pointCss = Css("circle-target") |+| css
              val offset   = coordinates.diff(p.baseCoordinates).offset
              // Offset amount
              val offP     =
                Offset.P.signedDecimalArcseconds.get(offset.p).toDouble * 1e6

              val offQ =
                Offset.Q.signedDecimalArcseconds.get(offset.q).toDouble * 1e6

              <.circle(^.cx       := scale(offP),
                       ^.cy := scale(offQ),
                       ^.r  := scale(maxP * radius),
                       pointCss,
                       title.map(<.title(_))
              )
            case SVGTarget.CrosshairTarget(coordinates, css, sidePx, title) =>
              val pointCss = Css("crosshair-target") |+| css
              val offset   = coordinates.diff(p.baseCoordinates).offset
              // Offset amount
              val offP     =
                Offset.P.signedDecimalArcseconds.get(offset.p).toDouble * 1e6

              val offQ =
                Offset.Q.signedDecimalArcseconds.get(offset.q).toDouble * 1e6
              val side = scale(maxP * sidePx)
              <.g(
                <.line(^.x1       := scale(offP) - side,
                       ^.x2 := scale(offP) + side,
                       ^.y1 := scale(offQ),
                       ^.y2 := scale(offQ),
                       pointCss
                ),
                <.line(^.x1       := scale(offP),
                       ^.x2 := scale(offP),
                       ^.y1 := scale(offQ) - side,
                       ^.y2 := scale(offQ) + side,
                       pointCss
                ),
                title.map(<.title(_))
              )
          }.toTagMod
        )
        svg
      }
}
