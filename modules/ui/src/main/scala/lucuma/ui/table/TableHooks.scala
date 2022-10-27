// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.effect.IO
import cats.effect.Sync
import cats.syntax.all.*
import crystal.react.implicits.*
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import japgolly.scalajs.react.util.DefaultEffects.{Async => DefaultA}
import lucuma.core.util.NewType
import lucuma.react.table.*
import reactST.{tanstackTableCore => raw}

import scalajs.js.JSConverters.*
import scalajs.js

case class TableOptionsWithStateStore[F[_], T](
  tableOptions: TableOptions[T],
  stateStore:   TableStateStore[F]
)

private object TableHooks:
  private given Reusability[ColumnId]                  = Reusability.by(_.value)
  private given Reusability[Visibility]                = Reusability.by(_.value)
  private given Reusability[Map[ColumnId, Visibility]] = Reusability.map
  private given Reusability[ColumnVisibility]          = Reusability.by(_.value)
  private given Reusability[SortDirection]             = Reusability.by(_.toDescending)
  private given Reusability[ColumnSort]                = Reusability.derive
  private given Reusability[Sorting]                   = Reusability.by(_.value)
  private given Reusability[TableState]                =
    Reusability.by(state => (state.columnVisibility, state.sorting))

  private object PrefsLoaded extends NewType[Boolean]
  private type PrefsLoaded = PrefsLoaded.Type

  private object CanSave extends NewType[Boolean]
  private type CanSave = CanSave.Type

  private def hook[T] =
    CustomHook[TableOptionsWithStateStore[DefaultA, T]]
      .useReactTableBy(_.tableOptions)
      .useState(PrefsLoaded(false))
      .useRef(CanSave(false))
      .useEffectOnMountBy((props, table, prefsLoadad, canSave) =>
        (props.stateStore.load() >>=
          (mod => table.modState(mod).to[DefaultA]))
          .guarantee( // This also forces a rerender, which react-table isn't doing by just changing the state.
            prefsLoadad.setStateAsync(PrefsLoaded(true))
          )
      )
      .useEffectWithDepsBy((_, table, _, _) => table.getState())((props, _, prefsLoadad, canSave) =>
        state =>
          // Don't save prefs while we are still attempting to load them or if we just loaded them.
          props.stateStore
            .save(state)
            .whenA(prefsLoadad.value.value && canSave.value.value)
            >> canSave
              .setAsync(CanSave(true))
              .whenA(prefsLoadad.value.value && !canSave.value.value)
      )
      .buildReturning((_, table, _, _) => table)

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

trait TableHooks:
  import TableHooks._

  given [Ctx, Step <: HooksApi.AbstractStep]
    : Conversion[HooksApi.Primary[Ctx, Step], Primary[Ctx, Step]] =
    api => new Primary(api)

  given [Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]]
    : Conversion[HooksApi.Secondary[Ctx, CtxFn, Step], Secondary[Ctx, CtxFn, Step]] =
    api => new Secondary(api)
