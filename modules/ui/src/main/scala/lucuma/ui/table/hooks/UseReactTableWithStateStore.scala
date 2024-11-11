// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
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

case class TableOptionsWithStateStore[F[_], T, M](
  tableOptions: TableOptions[T, M],
  stateStore:   TableStateStore[F]
)

private object UseReactTableWithStateStore:
  private object PrefsLoaded extends NewType[Boolean]

  private object CanSave extends NewType[Boolean]

  private def hook[T, M] =
    CustomHook[TableOptionsWithStateStore[DefaultA, T, M]]
      .useReactTableBy(_.tableOptions)
      .useState(PrefsLoaded(false))
      .useRef(CanSave(false))
      .useEffectOnMountBy((props, table, prefsLoadad, canSave) =>
        (props.stateStore.load() >>=
          (mod =>
            val newState: TableState = mod(table.getState())

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
      )
      .useEffectWithDepsBy((_, table, _, _) => table.getState())((props, _, prefsLoadad, canSave) =>
        state =>
          // Don't save prefs while we are still attempting to load them or if we just loaded them.
          props.stateStore
            .save(state)
            .whenA(canSave.value.value)
            >>
              canSave
                .setAsync(CanSave(true))
                .whenA(prefsLoadad.value.value && !canSave.value.value)
      )
      .buildReturning((_, table, _, _) => table)

  object HooksApiExt:
    sealed class Primary[Ctx, Step <: HooksApi.AbstractStep](api: HooksApi.Primary[Ctx, Step]):
      final def useReactTableWithStateStore[T, M](
        options: TableOptionsWithStateStore[DefaultA, T, M]
      )(using
        step:    Step
      ): step.Next[Table[T, M]] =
        useReactTableWithStateStoreBy(_ => options)

      final def useReactTableWithStateStoreBy[T, M](
        options: Ctx => TableOptionsWithStateStore[DefaultA, T, M]
      )(using
        step:    Step
      ): step.Next[Table[T, M]] =
        api.customBy(ctx => hook(options(ctx)))

    final class Secondary[Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ) extends Primary[Ctx, Step](api):
      def useReactTableWithStateStoreBy[T, M](
        tableDefWithOptions: CtxFn[TableOptionsWithStateStore[DefaultA, T, M]]
      )(using
        step:                Step
      ): step.Next[Table[T, M]] =
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
