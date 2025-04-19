// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object TableIcons:
  @js.native
  @JSImport("@fortawesome/pro-light-svg-icons", "faChevronRight")
  val faChevronRight: FAIcon = js.native

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
  FontAwesome.library.add(
    faChevronRight,
    faSort,
    faSortDown,
    faSortUp
  )

  val ChevronRight = FontAwesomeIcon(faChevronRight)
  val Sort         = FontAwesomeIcon(faSort)
  val SortDown     = FontAwesomeIcon(faSortDown)
  val SortUp       = FontAwesomeIcon(faSortUp)
