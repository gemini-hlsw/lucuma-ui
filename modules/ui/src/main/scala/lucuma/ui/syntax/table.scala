// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.syntax.eq.*
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.ReactMouseEvent
import lucuma.react.table.*

trait table:
  extension [T, TM, CM, TF](table: Table[T, TM, CM, TF])
    def getMultiRowSelectedHandler(rowId: RowId): ReactMouseEvent => Callback =
      (e: ReactMouseEvent) =>
        val isShiftPressed: Boolean   = e.shiftKey
        val isCmdCtrlPressed: Boolean = e.metaKey || e.ctrlKey

        val selectedRows: List[Row[T, TM, CM, TF]] = table.getSelectedRowModel().rows

        // If cmd is pressed add to the selection
        table.toggleAllRowsSelected(false).unless(isCmdCtrlPressed) >> (
          if (isShiftPressed && selectedRows.nonEmpty) {
            // If shift is pressed extend
            val allRows: List[(Row[T, TM, CM, TF], Int)] = table.getRowModel().rows.zipWithIndex
            // selectedRow is not empty, these won't fail
            val firstId: RowId                           = selectedRows.head.id
            val lastId: RowId                            = selectedRows.last.id
            val indexOfCurrent: Int                      = allRows.indexWhere(_._1.id == rowId)
            val indexOfFirst: Int                        = allRows.indexWhere(_._1.id == firstId)
            val indexOfLast: Int                         = allRows.indexWhere(_._1.id == lastId)
            if (indexOfCurrent =!= -1 && indexOfFirst =!= -1 && indexOfLast =!= -1)
              table.setRowSelection:
                if (indexOfCurrent < indexOfFirst)
                  RowSelection(
                    allRows
                      .slice(indexOfCurrent, indexOfLast + 1)
                      .map { case (row, _) => row.id -> true }*
                  )
                else
                  RowSelection(
                    allRows
                      .slice(indexOfFirst, indexOfCurrent + 1)
                      .map { case (row, _) => row.id -> true }*
                  )
            else Callback.empty
          } else
            table.modRowSelection: rowSelection =>
              RowSelection:
                rowSelection.value + (rowId -> rowSelection.value.get(rowId).fold(true)(!_))
        )

object table extends table
