// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.effect.Async
import cats.implicits._
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.reuse.Reuse
import crystal.react.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.util.DefaultEffects.{ Sync => DefaultS }
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

  type RV[F[_], A]  = Reuse[ViewF[F, A]]
  type RVO[F[_], A] = Reuse[ViewOptF[F, A]]

  implicit def externalValueReuseViewF: ExternalValue[RV[DefaultS, *]] =
    new ExternalValue[RV[DefaultS, *]] {
      // override def get[A](ev: Reuse[ViewF[DefaultS, A]]): Option[A] = ev.get.some
      override def get[A](ev: RV[DefaultS, A]): Option[A] = ev.get.some

      // override def set[A](ev: Reuse[ViewF[DefaultS, A]]): A => Callback = a => ev.set(a)
      override def set[A](ev: RV[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueReuseViewOptF: ExternalValue[RVO[DefaultS, *]] =
    new ExternalValue[RVO[DefaultS, *]] {
      // override def get[A](ev: Reuse[ViewOptF[DefaultS, A]]): Option[A] = ev.get
      override def get[A](ev: RVO[DefaultS, A]): Option[A] = ev.get

      // override def set[A](ev: Reuse[ViewOptF[DefaultS, A]]): A => Callback = a => ev.set(a)
      override def set[A](ev: RVO[DefaultS, A]): A => Callback = a => ev.set(a)
    }

  implicit def externalValueAsyncReuseViewF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[RV[F, *]] =
    new ExternalValue[RV[F, *]] {
      // override def get[A](ev: Reuse[ViewF[F, A]]): Option[A] = ev.get.some
      override def get[A](ev: RV[F, A]): Option[A] = ev.get.some

      // override def set[A](ev: Reuse[ViewF[F, A]]): A => Callback =
      override def set[A](ev: RV[F, A]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit def externalValueAsyncReuseViewOptF[F[_]: Async: Effect.Dispatch: Logger]
    : ExternalValue[RVO[F, *]] =
    new ExternalValue[RVO[F, *]] {
      override def get[A](ev: Reuse[ViewOptF[F, A]]): Option[A] = ev.get

      override def set[A](ev: Reuse[ViewOptF[F, A]]): A => Callback =
        ev.set.andThen(_.runAsync)
    }

  implicit val externalValueStateSnapshot: ExternalValue[StateSnapshot] =
    new ExternalValue[StateSnapshot] {
      override def get[A](ev: StateSnapshot[A]): Option[A] = ev.value.some

      override def set[A](ev: StateSnapshot[A]): A => Callback = ev.setState
    }
}

object ExternalValue extends ExternalValueImplicits
