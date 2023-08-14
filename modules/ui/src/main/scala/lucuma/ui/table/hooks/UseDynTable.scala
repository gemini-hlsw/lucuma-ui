// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import lucuma.react.SizePx
import lucuma.react.table.*
import lucuma.ui.reusability.given

/**
 * Provides values to be passed to the table definition:
 *   - `columnSizing`: To be passed directly to table `state` as `PartialTableState.columnSizing`.
 *   - `columnVisibility`: To be passed directly to table `state` as
 *     `PartialTableState.columnVisibility`.
 *   - `onColumnSizingChangeHandler`: To be passed directly to table `onColumnSizingChange`.
 */
class UseDynTable(
  colState:                        DynTable.ColState,
  val onColumnSizingChangeHandler: Updater[ColumnSizing] => Callback
):
  export colState.{computedVisibility => columnVisibility, resized => columnSizing}

object UseDynTable:
  private val hook = CustomHook[(DynTable, SizePx)] // (dynTable, width)
    .useStateBy(_._1.initialState) // colState
    .useEffectWithDepsBy((props, _) => // Recompute columns upon resize
      props._2
    )((props, colState) => width => colState.modState(s => props._1.adjustColSizes(width)(s)))
    .buildReturning: (props, colState) =>
      val (dynTable, width) = props

      val onColumnSizingChangeHandler: Updater[ColumnSizing] => Callback =
        (_: Updater[ColumnSizing]) match
          case Updater.Set(v)  =>
            colState.modState: s =>
              dynTable.adjustColSizes(width)(DynTable.ColState.resized.replace(v)(s))
          case Updater.Mod(fn) =>
            colState.modState: s =>
              dynTable.adjustColSizes(width)(DynTable.ColState.resized.modify(fn)(s))

      UseDynTable(colState.value, onColumnSizingChangeHandler)

  object HooksApiExt:
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
