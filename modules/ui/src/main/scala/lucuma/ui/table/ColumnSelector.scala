// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.table.ColumnId
import lucuma.react.table.Table
import lucuma.ui.primereact.LucumaStyles
import react.common.*
import react.primereact.Button
import react.primereact.Checkbox
import react.primereact.MenuItem
import react.primereact.PopupMenu
import react.primereact.hooks.all.*

case class ColumnSelector[T](
  table:       Table[T],
  columnNames: ColumnId => String,
  clazz:       Css = Css.Empty
) extends ReactFnProps(ColumnSelector.component)

object ColumnSelector:
  private type Props[T] = ColumnSelector[T]

  private def componentBuilder[T] =
    ScalaFnComponent
      .withHooks[Props[T]]
      .usePopupMenuRef
      .render { (props, menuRef) =>
        val menuItems = props.table.getAllColumns().drop(1).map { column =>
          val colId = column.id
          MenuItem.Custom(
            <.div(
              LucumaStyles.CheckboxWithLabel,
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

  private val component = componentBuilder[Any]
