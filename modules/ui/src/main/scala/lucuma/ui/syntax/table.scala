// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import lucuma.react.table.*
import japgolly.scalajs.react.ReactMouseEvent
import japgolly.scalajs.react.Callback
import cats.syntax.eq.*

trait table:
  extension [T, TM](row: Row[T, TM])
    def getMultiRowSelectedHandler(table: Table[T, TM]): ReactMouseEvent => Callback =
      (e: ReactMouseEvent) =>
        val isShiftPressed: Boolean   = e.shiftKey
        val isCmdCtrlPressed: Boolean = e.metaKey || e.ctrlKey

        val selectedRows: List[Row[T, TM]] = table.getSelectedRowModel().rows

        // If cmd is pressed add to the selection
        table.toggleAllRowsSelected(false).unless(isCmdCtrlPressed) >> (
          if (isShiftPressed && selectedRows.nonEmpty) {
            // If shift is pressed extend
            val allRows: List[(Row[T, TM], Int)] = table.getRowModel().rows.zipWithIndex
            val currentId: RowId                 = row.id
            // selectedRow is not empty, these won't fail
            val firstId: RowId                   = selectedRows.head.id
            val lastId: RowId                    = selectedRows.last.id
            val indexOfCurrent: Int              = allRows.indexWhere(_._1.id == currentId)
            val indexOfFirst: Int                = allRows.indexWhere(_._1.id == firstId)
            val indexOfLast: Int                 = allRows.indexWhere(_._1.id == lastId)
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
          } else row.toggleSelected()
        )

object table extends table
