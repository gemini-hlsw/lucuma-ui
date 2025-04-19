// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object ThemeIcons:
  @js.native
  @JSImport("@fortawesome/pro-thin-svg-icons", "faDisplay")
  val faDisplay: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faEclipse")
  val faEclipse: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faMoon")
  val faMoon: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSun")
  val faSunBright: FAIcon = js.native

  // This is tedious but lets us do proper tree-shaking
  FontAwesome.library.add(
    faDisplay,
    faEclipse,
    faMoon,
    faSunBright
  )

  val Display   = FontAwesomeIcon(faEclipse)
  val Eclipse   = FontAwesomeIcon(faEclipse)
  val Moon      = FontAwesomeIcon(faMoon)
  val SunBright = FontAwesomeIcon(faSunBright)
