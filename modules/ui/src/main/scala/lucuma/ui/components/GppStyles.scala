// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import react.common.Css

trait GppStyles {
  val MoonSphere: Css  = Css("moon-sphere")
  val MoonLight: Css   = Css("moon-light")
  val MoonDark: Css    = Css("moon-dark")
  val MoonDivider: Css = Css("moon-divider")
  val MoonPhase: Css   = Css("moon-phase")
}

object GppStyles extends GppStyles
