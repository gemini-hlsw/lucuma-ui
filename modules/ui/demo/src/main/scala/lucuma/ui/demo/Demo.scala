// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.ui.syntax.all.given

import scala.scalajs.js.annotation.*

@JSExportTopLevel("Demo")
object Demo extends AppMain {
  override protected val rootComponent: VdomElement =
    <.div(
      <.div(
        FormComponent()
      ),
      <.div(
        IconsDemo.component()
      ),
      <.div(
        ThemeDemo.component()
      )
    )
}
