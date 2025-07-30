// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.table.RowId

type HeaderOrRow[+R] = Either[HeaderRow, R]

case class HeaderRow(rowId: RowId, content: VdomNode):
  def toHeaderOrRow[R]: HeaderOrRow[R] = this.asLeft

extension [R](row: R) def toHeaderOrRow: HeaderOrRow[R] = row.asRight
