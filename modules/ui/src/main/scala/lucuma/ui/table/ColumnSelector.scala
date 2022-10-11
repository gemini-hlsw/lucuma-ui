// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import crystal.react.View
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import react.common.*
import react.semanticui.modules.checkbox.Checkbox
import react.semanticui.modules.dropdown.*
import reactST.{tanstackTableCore => raw}

case class ColumnSelector[T](
  table:         raw.mod.Table[T],
  columnNames:   Map[String, String],
  hiddenColumns: View[Set[String]],
  clazz:         Css = Css.Empty
) extends ReactFnProps(ColumnSelector.component)

object ColumnSelector:
  private type Props[T] = ColumnSelector[T]

  private def componentBuilder[T] = ScalaFnComponent[Props[T]](props =>
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
          .toTagMod { column =>
            val colId = column.id
            DropdownItem()(^.key := colId)(
              <.div(
                Checkbox(
                  label = props.columnNames(colId),
                  checked = column.getIsVisible(),
                  onChange = (value: Boolean) =>
                    Callback(column.toggleVisibility()) >>
                      props.hiddenColumns
                        .mod(cols => if (value) cols - colId else cols + colId)
                )
              )
            )
          }
      )
    )
  )

  private val component = componentBuilder[Any]
