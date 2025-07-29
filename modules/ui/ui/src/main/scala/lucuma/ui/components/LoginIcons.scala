// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object LoginIcons:
  @js.native
  @JSImport("@fortawesome/pro-light-svg-icons", "faClipboard")
  val faClipboard: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-light-svg-icons", "faClipboardCheck")
  val faClipboardCheck: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faTriangleExclamation")
  val faExclamationTriangle: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSkullCrossbones")
  val faSkullCrossbones: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faUserAstronaut")
  val faUserAstronaut: FAIcon = js.native

  // This is tedious but lets us do proper tree-shaking
  FontAwesome.library.add(
    faClipboard,
    faClipboardCheck,
    faExclamationTriangle,
    faSkullCrossbones,
    faUserAstronaut
  )

  val Clipboard           = FontAwesomeIcon(faClipboard)
  val ClipboardCheck      = FontAwesomeIcon(faClipboardCheck)
  val ExclamationTriangle = FontAwesomeIcon(faExclamationTriangle)
  val SkullCrossBones     = FontAwesomeIcon(faSkullCrossbones)
  val UserAstronaut       = FontAwesomeIcon(faUserAstronaut)
