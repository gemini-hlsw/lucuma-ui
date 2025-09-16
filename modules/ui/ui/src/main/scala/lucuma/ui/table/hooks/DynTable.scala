// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import cats.syntax.all.*
import lucuma.react.SizePx
import lucuma.react.table.*
import lucuma.ui.table.*
import lucuma.ui.table.ColumnSize.*
import lucuma.ui.table.hooks.DynTable.ColState
import monocle.Focus
import monocle.Lens

import scala.annotation.tailrec

/**
 * Definition of a dynamic table to be passed to `useDynTable`. Avoid creating in the hook call,
 * since it performs coherence checks upon creation. Pass a static instance to the hook instead.
 *
 * @param columnSizes
 *   Size definitions for all the columns in the table.
 * @param columnPriorities
 *   The order in which columns are removed by overflow. The ones at the beginning are removed
 *   first. Missing columns are not removed by overflow.
 * @param initialState
 */
case class DynTable(
  columnSizes:      Map[ColumnId, ColumnSize],
  columnPriorities: List[ColumnId],
  initialState:     DynTable.ColState
):
  // Check columns comply with the ones passed in columnSizes
  columnPriorities.foreach(colId =>
    assert(
      columnSizes.keySet.contains(colId),
      s"DynTable.initialState.columnPriorities contains unknown column [$colId] not in columnSizes"
    )
  )
  initialState.resized.value.keySet.foreach(colId =>
    assert(
      columnSizes.keySet.contains(colId),
      s"DynTable.initialState.resized contains unknown column [$colId] not in columnSizes"
    )
  )
  initialState.visibility.value.keySet.foreach(colId =>
    assert(
      columnSizes.keySet.contains(colId),
      s"DynTable.initialState.visibility contains unknown column [$colId] not in columnSizes"
    )
  )
  initialState.overflow.foreach(colId =>
    assert(
      columnSizes.keySet.contains(colId),
      s"DynTable.initialState.overflow contains unknown column [$colId] not in columnSizes"
    )
  )

  // This method:
  // - Adjusts resizable columns proportionally to available space (taking into account space taken by fixed columns).
  // - If all visible columns are at their minimum width and overflow the viewport,
  //     then starts dropping columns (as long as there are reamining droppable ones).
  def adjustColSizes(width: SizePx)(colState: DynTable.ColState): DynTable.ColState = {
    // Recurse at go1 when a column is dropped.
    // This level just to avoid clearing overflow on co-recursion
    // @tailrec // Scala 3.6.3 thinks there are no recursive calls. Let's leave this commented in case it gets fixed.
    def go1(colState: DynTable.ColState): DynTable.ColState =
      lazy val visibleColumns: Set[ColumnId] =
        columnSizes.keySet.filterNot(colId =>
          colState.visibility.value.get(colId).contains(Visibility.Hidden)
        ) -- colState.overflow

      lazy val currentVisibleColumnSizes: Map[ColumnId, SizePx] =
        visibleColumns
          .map: colId =>
            colId -> colState.resized.value.getOrElse(colId, columnSizes(colId).initial)
          .toMap

      // We define these here to avoid recomputing in every go2 iteration.
      lazy val nextOverflowCandidate: Option[ColumnId]        =
        columnPriorities.find(visibleColumns.contains)
      lazy val stateWithNextOverflowColumn: DynTable.ColState =
        DynTable.ColState.overflow.modify(_ ++ nextOverflowCandidate)(colState)

      if (width.value == 0) colState
      else {
        // Recurse at go2 when a column was shrunk/expanded beyond its bounds.
        @tailrec
        def go2(
          visibleCols:    Map[ColumnId, SizePx],
          fixedAccum:     Map[ColumnId, SizePx] = Map.empty,
          fixedSizeAccum: Int = 0
        ): DynTable.ColState = {
          val (boundedCols, unboundedCols)
            : (Iterable[(Option[ColumnId], SizePx)], Iterable[(ColumnId, SizePx)]) =
            visibleCols.partitionMap: (colId, colSize) =>
              columnSizes(colId) match
                case FixedSize(size)                                         =>
                  (none -> size).asLeft
                // Columns that reach or go beyond their bounds are treated as fixed.
                case Resizable(_, Some(min), _) if colSize.value < min.value =>
                  (colId.some -> min).asLeft
                case Resizable(_, _, Some(max)) if colSize.value > max.value =>
                  (colId.some -> max).asLeft
                case _                                                       =>
                  (colId -> colSize).asRight

          val boundedColsWidth: Int = boundedCols.map(_._2.value).sum
          val totalBounded: Int     = fixedSizeAccum + boundedColsWidth

          // If bounded columns are more than the viewport width, drop the lowest priority column and start again.
          if (totalBounded > width.value && nextOverflowCandidate.isDefined)
            // We must remove columns one by one, since removing one causes the rest to recompute.
            go1(stateWithNextOverflowColumn)
          else
            val spaceForUnbounded: Int = width.value - totalBounded

            val totalNewUnbounded: Int = unboundedCols.map(_._2.value).sum

            val ratio: Double = spaceForUnbounded.toDouble / totalNewUnbounded

            val newFixedAccum: Map[ColumnId, SizePx] =
              fixedAccum ++
                boundedCols.collect:
                  case (Some(colId), size) => colId -> size

            val unboundedColsAdjusted: Map[ColumnId, SizePx] =
              unboundedCols
                .map: (colId, width) =>
                  colId -> width.modify(x => (x * ratio).toInt)
                .toMap

            boundedCols match
              case Nil =>
                DynTable.ColState.resized.replace(
                  ColumnSizing(newFixedAccum ++ unboundedColsAdjusted)
                )(colState)
              case _   =>
                go2(unboundedColsAdjusted, newFixedAccum, totalBounded)
        }

        go2(currentVisibleColumnSizes)
      }

    go1(colState.resetOverflow)
  }

object DynTable:
  /**
   * State of the columns in a dynamic table.
   *
   * @param resized
   *   Resized columns. To be passed directly to table `state` as `PartialTableState.columnSizing`.
   * @param visibility
   *   Column visitibility without taking into account overflows. Do not pass to table. See
   *   `computedVisibility` instead.
   * @param overflow
   *   Columns removed becasue of overflow.
   */
  case class ColState(
    val resized:    ColumnSizing,
    val visibility: ColumnVisibility,
    val overflow:   Set[ColumnId] = Set.empty
  ):
    /**
     * Column visitibility. To be passed directly to table `state` as
     * `PartialTableState.columnVisibility`.
     */
    lazy val computedVisibility: ColumnVisibility =
      visibility.modify(_ ++ overflow.map(_ -> Visibility.Hidden))

    /**
     * The same state without overflows. Useful when recomputing them.
     */
    def resetOverflow: ColState =
      ColState.overflow.replace(Set.empty)(this)

  object ColState:
    val resized: Lens[ColState, ColumnSizing]        = Focus[ColState](_.resized)
    val visibility: Lens[ColState, ColumnVisibility] = Focus[ColState](_.visibility)
    val overflow: Lens[ColState, Set[ColumnId]]      = Focus[ColState](_.overflow)
