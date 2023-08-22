// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object SequenceIcons:
  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCircle")
  val faCircle: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCrosshairs")
  val faCrosshairs: FAIcon = js.native

  // This is tedious but lets us do proper tree-shaking
  FontAwesome.library.add(
    faCircle,
    faCrosshairs
  )

  val Circle     = FontAwesomeIcon(faCircle)
  val Crosshairs = FontAwesomeIcon(faCrosshairs)
