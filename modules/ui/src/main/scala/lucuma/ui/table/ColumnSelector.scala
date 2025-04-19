// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
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
import lucuma.react.table.ColumnVisibility
import lucuma.react.table.Visibility
import lucuma.ui.primereact.LucumaPrimeStyles

case class ColumnSelector(
  allColumns:             List[(ColumnId, String)],
  columnVisibility:       ColumnVisibility,
  toggleColumnVisibility: ColumnId => Callback,
  clazz:                  Css = Css.Empty
) extends ReactFnProps(ColumnSelector.component)

object ColumnSelector:
  private type Props = ColumnSelector

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .usePopupMenuRef
      .render: (props, menuRef) =>
        val menuItems =
          props.allColumns
            .map: (colId, colName) =>
              MenuItem.Custom(
                <.div(LucumaPrimeStyles.CheckboxWithLabel)(
                  Checkbox(
                    id = colId.value,
                    checked = !props.columnVisibility.value.get(colId).contains(Visibility.Hidden),
                    onChange = _ => props.toggleColumnVisibility(colId)
                  ),
                  <.label(^.htmlFor := colId.value, colName)
                )
              )

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
