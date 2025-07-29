// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.svg_<^.*
import lucuma.react.common.Css
import lucuma.react.common.ReactFnComponent
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.tooltip.*
import lucuma.ui.syntax.all.given

case class CrossTarget(
  p:           Double,
  q:           Double,
  maxP:        Long,
  radius:      Double,
  pointCss:    Css,
  selectedCss: Css,
  selected:    Boolean,
  title:       Option[String]
) extends ReactFnProps(CrossTarget)

object CrossTarget
    extends ReactFnComponent[CrossTarget](p =>
      <.g(VisualizationStyles.VisualizationTooltipTarget)(
        <.circle(
          ^.cx := scale(p.p),
          ^.cy := scale(p.q),
          ^.r  := scale(p.maxP * (p.radius + 3)),
          p.selectedCss
        ).when(p.selected),
        <.circle(
          ^.cx := scale(p.p),
          ^.cy := scale(p.q),
          ^.r  := scale(p.maxP * p.radius),
          p.pointCss
        )
      ).withTooltipOptions(content = p.title.getOrElse("<>"))
    )
