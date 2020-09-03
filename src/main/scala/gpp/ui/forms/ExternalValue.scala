// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.effect.Effect
import crystal.ViewF
import crystal.react.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.extra.StateSnapshot

trait ExternalValue[EV[_]] {
  def get[A](ev: EV[A]): A
  def set[A](ev: EV[A]): A => Callback
}

object ExternalValue {
  implicit def externalValueViewF[F[_]: Effect]: ExternalValue[ViewF[F, *]] =
    new ExternalValue[ViewF[F, *]] {
      override def get[A](ev: ViewF[F, A]): A = ev.get

      override def set[A](ev: ViewF[F, A]): A => Callback =
        ev.set.andThen(_.runInCB)
    }

  implicit val externalValueStateSnapshot: ExternalValue[StateSnapshot] =
    new ExternalValue[StateSnapshot] {
      override def get[A](ev: StateSnapshot[A]): A = ev.value

      override def set[A](ev: StateSnapshot[A]): A => Callback = ev.setState
    }
}
