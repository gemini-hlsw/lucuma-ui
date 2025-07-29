// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.fa.IconSize
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles
import lucuma.ui.syntax.all.given

object UnderConstruction:
  protected val component =
    ScalaFnComponent[Unit]: _ =>
      <.div(LucumaStyles.HVCenter)(
        <.div(
          <.div("Under Construction"),
          <.div(LucumaStyles.HVCenter)(
            LucumaIcons.Gears
              .withSize(IconSize.X5)
              .withTitle("Under construction")
          )
        )
      )

  inline def apply() = component()
