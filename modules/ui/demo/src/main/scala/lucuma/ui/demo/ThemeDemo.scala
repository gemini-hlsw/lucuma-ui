// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.refined.*
import lucuma.ui.enums.Theme
import lucuma.ui.hooks.*
import lucuma.ui.primereact.EnumDropdownView
import lucuma.ui.primereact.given

object ThemeDemo:
  val component = ScalaFnComponent
    .withHooks[Unit]
    .useTheme()
    .render: (_, theme) =>
      <.div(
        <.h2("Theme!"),
        <.div(EnumDropdownView("theme".refined, theme))
      )
