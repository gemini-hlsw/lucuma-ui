// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import react.fa.FAIcon
import react.fa.FontAwesomeIcon
import react.fa.IconLibrary

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object TableIcons:
  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSort")
  val faSort: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSortDown")
  val faSortDown: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSortUp")
  val faSortUp: FAIcon = js.native

  // This is tedious but lets us do proper tree-shaking
  IconLibrary.add(
    faSort,
    faSortDown,
    faSortUp
  )

  val Sort     = FontAwesomeIcon(faSort)
  val SortDown = FontAwesomeIcon(faSortDown)
  val SortUp   = FontAwesomeIcon(faSortUp)
