// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import lucuma.react.SizePx
import lucuma.react.table.*
import lucuma.ui.reusability.given
import lucuma.ui.table.ColumnSize
import lucuma.ui.table.ColumnSize.*

/**
 * Provides values to be passed to the table definition:
 *   - `setInitialColWidths`: Convenience method to set column size for all colums to the ones
 *     passed to the hook. Eg: `cols.map(useDynTable.setInitialColWidths)`.
 *   - `columnSizing`: To be passed directly to table `state` as `PartialTableState.columnSizing`.
 *   - `columnVisibility`: To be passed directly to table `state` as
 *     `PartialTableState.columnVisibility`.
 *   - `onColumnSizingChangeHandler`: To be passed directly to table `onColumnSizingChange`.
 *
 * Make sure the table has `table-layout: fixed`.
 */
class UseDynTable(
  initialColumnSizes:              Map[ColumnId, ColumnSize],
  colState:                        DynTable.ColState,
  val onColumnSizingChangeHandler: Updater[ColumnSizing] => Callback
):
  def setInitialColWidths[R, TM, CM, TF](
    cols: List[ColumnDef[R, ?, TM, CM, TF, ?, ?]]
  ): List[ColumnDef[R, ?, TM, CM, TF, ?, ?]] =
    cols.map:
      case col @ ColumnDef.Single(_) => col.withColumnSize(initialColumnSizes(col.id))
      case col @ ColumnDef.Group(_)  => col.withColumnSize(initialColumnSizes(col.id))

  export colState.{computedVisibility => columnVisibility, resized => columnSizing}

object UseDynTable:
  def useDynTable(dynTableDef: DynTable, width: SizePx): HookResult[UseDynTable] =
    for
      colState <- useState(dynTableDef.initialState)
      _        <- useEffectWithDeps(width): w => // Recompute columns upon resize
                    CallbackTo(dynTableDef.adjustColSizes(w)(colState.value)) >>= colState.setState
    yield
      def onColumnSizingChangeHandler(updater: Updater[ColumnSizing]): Callback =
        colState.modState: oldState =>
          dynTableDef.adjustColSizes(width):
            updater match
              case Updater.Set(v)  => DynTable.ColState.resized.replace(v)(oldState)
              case Updater.Mod(fn) => DynTable.ColState.resized.modify(fn)(oldState)

      UseDynTable(dynTableDef.columnSizes, colState.value, onColumnSizingChangeHandler)

  private val hook: CustomHook[(DynTable, SizePx), UseDynTable] =
    CustomHook.fromHookResult(useDynTable(_, _))

  object HooksApiExt {
    sealed class Primary[Ctx, Step <: HooksApi.AbstractStep](api: HooksApi.Primary[Ctx, Step]):
      /**
       * Computes a dynamic table state, automatically computing column overflows and sizes.
       *
       * @param dynTable
       *   Dynamic table definition.
       * @param width
       *   Table width in pixels.
       */
      final def useDynTable(dynTable: DynTable, width: SizePx)(using
        step: Step
      ): step.Next[UseDynTable] =
        useDynTableBy(_ => (dynTable, width))

      /**
       * Computes a dynamic table state, automatically computing column overflows and sizes.
       *
       * @param dynTable
       *   Dynamic table definition.
       * @param width
       *   Table width in pixels.
       */
      final def useDynTableBy(props: Ctx => (DynTable, SizePx))(using
        step: Step
      ): step.Next[UseDynTable] =
        api.customBy(ctx => hook(props(ctx)))

    final class Secondary[Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ) extends Primary[Ctx, Step](api):
      /**
       * Computes a dynamic table state, automatically computing column overflows and sizes.
       *
       * @param dynTable
       *   Dynamic table definition.
       * @param width
       *   Table width in pixels.
       */
      def useDynTableBy(props: CtxFn[(DynTable, SizePx)])(using
        step: Step
      ): step.Next[UseDynTable] =
        useDynTableBy(step.squash(props)(_))
  }

  protected trait HooksApiExt:
    import HooksApiExt.*

    implicit def hooksExtDynTable1[Ctx, Step <: HooksApi.AbstractStep](
      api: HooksApi.Primary[Ctx, Step]
    ): Primary[Ctx, Step] =
      new Primary(api)

    implicit def hooksExtDynTable2[
      Ctx,
      CtxFn[_],
      Step <: HooksApi.SubsequentStep[Ctx, CtxFn]
    ](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ): Secondary[Ctx, CtxFn, Step] =
      new Secondary(api)

  object syntax extends HooksApiExt
