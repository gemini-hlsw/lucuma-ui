// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css
import lucuma.react.common.ReactFnProps

case class SolarProgress(css: Css = Css.Empty) extends ReactFnProps(SolarProgress.component)

object SolarProgress {
  private type Props = SolarProgress

  private val component = ScalaFnComponent[Props] { p =>
    <.div(
      ^.cls := "solar-system",
      p.css,
      <.div(
        ^.cls := "mars-orbit orbit",
        <.div(^.cls    := "planet mars"),
        <.div(
          ^.cls := "earth-orbit orbit",
          <.div(^.cls := "planet earth"),
          <.div(^.cls := "venus-orbit orbit",
                <.div(^.cls := "planet venus"),
                <.div(^.cls := "mercury-orbit orbit",
                      <.div(^.cls := "planet mercury"),
                      <.div(^.cls := "sun")
                )
          )
        )
      )
    )
  }
}
