// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import cats.syntax.eq.*
import crystal.react.View
import lucuma.react.primereact.MenuItem
import lucuma.ui.enums.Theme

object ThemeSubMenu:
  def apply(theme: View[Theme]): MenuItem =
    MenuItem.SubMenu(
      label = "Theme",
      icon = ThemeIcons.Eclipse
    )(
      MenuItem.Item(
        label = "Dark",
        icon = ThemeIcons.Moon,
        disabled = theme.get === Theme.Dark,
        command = theme.set(Theme.Dark)
      ),
      MenuItem.Item(
        label = "Light",
        icon = ThemeIcons.SunBright,
        disabled = theme.get === Theme.Light,
        command = theme.set(Theme.Light)
      ),
      MenuItem.Item(
        label = "System",
        icon = ThemeIcons.Display,
        disabled = theme.get === Theme.System,
        command = theme.set(Theme.System)
      )
    )
