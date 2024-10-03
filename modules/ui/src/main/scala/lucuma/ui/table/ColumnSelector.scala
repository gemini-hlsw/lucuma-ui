// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.Button
import lucuma.react.primereact.Checkbox
import lucuma.react.primereact.MenuItem
import lucuma.react.primereact.PopupMenu
import lucuma.react.primereact.hooks.all.*
import lucuma.react.table.ColumnId
import lucuma.react.table.Table
import lucuma.ui.primereact.LucumaPrimeStyles

case class ColumnSelector[T, M](
  table:       Table[T, M],
  columnNames: ColumnId => Option[String],
  clazz:       Css = Css.Empty
) extends ReactFnProps(ColumnSelector.component)

object ColumnSelector:
  private type Props[T, M] = ColumnSelector[T, M]

  private def componentBuilder[T, M] =
    ScalaFnComponent
      .withHooks[Props[T, M]]
      .usePopupMenuRef
      .render { (props, menuRef) =>
        val menuItems =
          props.table
            .getAllColumns()
            .drop(1)
            .filter(col => props.columnNames(col.id).isDefined)
            .map { column =>
              val colId = column.id
              MenuItem.Custom(
                <.div(
                  LucumaPrimeStyles.CheckboxWithLabel,
                  Checkbox(id = colId.value,
                           checked = column.getIsVisible(),
                           onChange = _ => column.toggleVisibility()
                  ),
                  <.label(^.htmlFor := colId.value, props.columnNames(colId))
                )
              )
            }
        React.Fragment(
          Button(
            label = "Columns",
            icon = "pi pi-chevron-down",
            iconPos = Button.IconPosition.Right,
            text = true,
            clazz = props.clazz,
            severity = Button.Severity.Secondary,
            onClickE = menuRef.toggle
          ),
          PopupMenu(model = menuItems, clazz = props.clazz).withRef(menuRef.ref)
        )
      }

  private val component = componentBuilder[Any, Any]
