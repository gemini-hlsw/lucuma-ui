// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import cats.effect.IO
import cats.syntax.all.*
import crystal.react.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import japgolly.scalajs.react.util.DefaultEffects.{Async => DefaultA}
import lucuma.core.util.NewType
import lucuma.react.table.*
import lucuma.ui.reusability.given

case class TableOptionsWithStateStore[F[_], T](
  tableOptions: TableOptions[T],
  stateStore:   TableStateStore[F]
)

private object UseReactTableWithStateStore:
  private object PrefsLoaded extends NewType[Boolean]

  private object CanSave extends NewType[Boolean]

  private def hook[T] =
    CustomHook[TableOptionsWithStateStore[DefaultA, T]]
      .useReactTableBy(_.tableOptions)
      .useRef(PrefsLoaded(false))
      .useRef(CanSave(false))
      .useEffectOnMountBy((props, table, prefsLoadad, canSave) =>
        (props.stateStore.load() >>=
          (mod => prefsLoadad.setAsync(PrefsLoaded(true)) >> table.modState(mod).to[DefaultA]))
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
      final def useReactTableWithStateStore[T](
        options: TableOptionsWithStateStore[DefaultA, T]
      )(using
        step:    Step
      ): step.Next[Table[T]] =
        useReactTableWithStateStoreBy(_ => options)

      final def useReactTableWithStateStoreBy[T](
        options: Ctx => TableOptionsWithStateStore[DefaultA, T]
      )(using
        step:    Step
      ): step.Next[Table[T]] =
        api.customBy(ctx => hook(options(ctx)))

    final class Secondary[Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ) extends Primary[Ctx, Step](api):
      def useReactTableWithStateStoreBy[T](
        tableDefWithOptions: CtxFn[TableOptionsWithStateStore[DefaultA, T]]
      )(implicit
        step:                Step
      ): step.Next[Table[T]] =
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
