// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import cats.effect.IO
import cats.syntax.all.*
import crystal.react.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import lucuma.core.util.NewType
import lucuma.react.table.*
import lucuma.ui.reusability.given

case class TableOptionsWithStateStore[F[_], T, TM, CM, TF](
  tableOptions: TableOptions[T, TM, CM, TF],
  stateStore:   TableStateStore[F, TF]
)

private object UseReactTableWithStateStore:
  private object PrefsLoaded extends NewType[Boolean]

  private object CanSave extends NewType[Boolean]

  def useReactTableWithStateStore[T, TM, CM, TF](
    options: TableOptionsWithStateStore[DefaultA, T, TM, CM, TF]
  ): HookResult[Table[T, TM, CM, TF]] =
    for
      table       <- useReactTable(options.tableOptions)
      prefsLoadad <- useState(PrefsLoaded(false))
      canSave     <- useRef(CanSave(false))
      _           <- useEffectOnMount:
                       (options.stateStore.load() >>=
                         (mod =>
                           val newState: TableState[TF] = mod(table.getState())

                           // We apply partial state changes in case there are partial state overrides.
                           table.setColumnVisibility(newState.columnVisibility).to[DefaultA] >>
                             // table.setColumnOrder(newState.columnOrder).to[DefaultA] >> // Not implemented yet in lucuma-react
                             table.setColumnPinning(newState.columnPinning).to[DefaultA] >>
                             table.setRowPinning(newState.rowPinning).to[DefaultA] >>
                             table.setSorting(newState.sorting).to[DefaultA] >>
                             table.setExpanded(newState.expanded).to[DefaultA] >>
                             table.setColumnSizing(newState.columnSizing).to[DefaultA] >>
                             table.setColumnSizingInfo(newState.columnSizingInfo).to[DefaultA] >>
                             table.setRowSelection(newState.rowSelection).to[DefaultA] >>
                             prefsLoadad.setStateAsync(PrefsLoaded(true))
                         ))
                         .guaranteeCase(outcome => canSave.setAsync(CanSave(true)).unlessA(outcome.isSuccess))
      _           <- useEffectWithDeps(table.getState()): state =>
                       // Don't save prefs while we are still attempting to load them or if we just loaded them.
                       options.stateStore
                         .save(state)
                         .whenA(canSave.value.value)
                         >>
                           canSave
                             .setAsync(CanSave(true))
                             .whenA(prefsLoadad.value.value && !canSave.value.value)
    yield table

  private def hook[T, TM, CM, TF]
    : CustomHook[TableOptionsWithStateStore[DefaultA, T, TM, CM, TF], Table[T, TM, CM, TF]] =
    CustomHook.fromHookResult(useReactTableWithStateStore(_))

  object HooksApiExt:
    sealed class Primary[Ctx, Step <: HooksApi.AbstractStep](api: HooksApi.Primary[Ctx, Step]):
      final def useReactTableWithStateStore[T, TM, CM, TF](
        options: TableOptionsWithStateStore[DefaultA, T, TM, CM, TF]
      )(using
        step:    Step
      ): step.Next[Table[T, TM, CM, TF]] =
        useReactTableWithStateStoreBy(_ => options)

      final def useReactTableWithStateStoreBy[T, TM, CM, TF](
        options: Ctx => TableOptionsWithStateStore[DefaultA, T, TM, CM, TF]
      )(using
        step:    Step
      ): step.Next[Table[T, TM, CM, TF]] =
        api.customBy(ctx => hook(options(ctx)))

    final class Secondary[Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ) extends Primary[Ctx, Step](api):
      def useReactTableWithStateStoreBy[T, TM, CM, TF](
        tableDefWithOptions: CtxFn[TableOptionsWithStateStore[DefaultA, T, TM, CM, TF]]
      )(using
        step:                Step
      ): step.Next[Table[T, TM, CM, TF]] =
        super.useReactTableWithStateStoreBy(step.squash(tableDefWithOptions)(_))

  trait HooksApiExt:
    import HooksApiExt.*

    implicit def hooksExtReactTableWithStateStore1[Ctx, Step <: HooksApi.AbstractStep](
      api: HooksApi.Primary[Ctx, Step]
    ): Primary[Ctx, Step] =
      new Primary(api)

    implicit def hooksExtReactTableWithStateStore2[
      Ctx,
      CtxFn[_],
      Step <: HooksApi.SubsequentStep[Ctx, CtxFn]
    ](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ): Secondary[Ctx, CtxFn, Step] =
      new Secondary(api)

  object syntax extends HooksApiExt
