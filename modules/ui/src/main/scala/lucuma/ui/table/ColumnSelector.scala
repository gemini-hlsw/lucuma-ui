// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import crystal.react.View
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.table.ColumnId
import lucuma.react.table.Table
import react.common.*
import react.semanticui.modules.checkbox.Checkbox
import react.semanticui.modules.dropdown.*

case class ColumnSelector[T](
  table:       Table[T],
  columnNames: ColumnId => String, // id -> label
  clazz:       Css = Css.Empty
) extends ReactFnProps(ColumnSelector.component)

object ColumnSelector:
  private type Props[T] = ColumnSelector[T]

  private def componentBuilder[T] = ScalaFnComponent[Props[T]] { props =>
    Dropdown(
      item = true,
      simple = true,
      pointing = Pointing.TopRight,
      scrolling = true,
      text = "Columns",
      clazz = props.clazz
    )(
      DropdownMenu(
        props.table
          .getAllColumns()
          .drop(1)
          .map { column =>
            val colId = column.id
            DropdownItem()(^.key := colId.value)(
              <.div(
                Checkbox(
                  label = props.columnNames(colId),
                  checked = column.getIsVisible(),
                  onChange = (value: Boolean) => column.toggleVisibility()
                )
              )
            )
          }
          .toTagMod
      )
    )
  }

  private val component = componentBuilder[Any]
