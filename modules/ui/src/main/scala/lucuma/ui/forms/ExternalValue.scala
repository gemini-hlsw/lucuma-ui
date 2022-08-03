// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.effect.Async
import cats.implicits._
import crystal._
import crystal.react._
import crystal.react.implicits._
import crystal.react.reuse._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.util.DefaultEffects.{Sync => DefaultS}
import japgolly.scalajs.react.util.Effect
import org.typelevel.log4cats.Logger

trait ExternalValue[EV[_]] {
  def get[A](ev: EV[A]): Option[A]
  def set[A](ev: EV[A]): A => Callback
}

trait ExternalValueImplicits {
  implicit def externalValueViewF: ExternalValue[ViewF[DefaultS, *]] =
    new ExternalValue[ViewF[DefaultS, *]] {
      override def get[A](ev: ViewF[DefaultS, A]): Option[A] = ev.get.some

      override def set[A](ev: ViewF[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueViewOptF: ExternalValue[ViewOptF[DefaultS, *]] =
    new ExternalValue[ViewOptF[DefaultS, *]] {
      override def get[A](ev: ViewOptF[DefaultS, A]): Option[A] = ev.get

      override def set[A](ev: ViewOptF[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueAsyncViewF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[ViewF[F, *]] =
    new ExternalValue[ViewF[F, *]] {
      override def get[A](ev: ViewF[F, A]): Option[A] = ev.get.some

      override def set[A](ev: ViewF[F, A]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit def externalValueAsyncViewOptF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[ViewOptF[F, *]] =
    new ExternalValue[ViewOptF[F, *]] {
      override def get[A](ev: ViewOptF[F, A]): Option[A] = ev.get

      override def set[A](ev: ViewOptF[F, A]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit def externalValueReuseViewF: ExternalValue[ReuseViewF[DefaultS, *]] =
    new ExternalValue[ReuseViewF[DefaultS, *]] {
      override def get[A](ev: ReuseViewF[DefaultS, A]): Option[A] = ev.get.some

      override def set[A](ev: ReuseViewF[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueReuseViewOptF: ExternalValue[ReuseViewOptF[DefaultS, *]] =
    new ExternalValue[ReuseViewOptF[DefaultS, *]] {
      override def get[A](ev: ReuseViewOptF[DefaultS, A]): Option[A] = ev.get

      override def set[A](ev: ReuseViewOptF[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueAsyncReuseViewF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[ReuseViewF[F, *]] =
    new ExternalValue[ReuseViewF[F, *]] {
      override def get[A](ev: ReuseViewF[F, A]): Option[A] = ev.get.some

      override def set[A](ev: ReuseViewF[F, A]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit def externalValueAsyncReuseViewOptF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[ReuseViewOptF[F, *]] =
    new ExternalValue[ReuseViewOptF[F, *]] {
      override def get[A](ev: ReuseViewOptF[F, A]): Option[A] = ev.get

      override def set[A](ev: ReuseViewOptF[F, A]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit val externalValueStateSnapshot: ExternalValue[StateSnapshot] =
    new ExternalValue[StateSnapshot] {
      override def get[A](ev: StateSnapshot[A]): Option[A] = ev.value.some

      override def set[A](ev: StateSnapshot[A]): A => Callback = ev.setState
    }
}

object ExternalValue extends ExternalValueImplicits
