// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.ui.forms

import japgolly.scalajs.react.Callback
import crystal.ViewF
import crystal.react.implicits._
import cats.effect.Effect
import japgolly.scalajs.react.extra.StateSnapshot

trait ExternalValue[W[_]] {
  def get[A](w: W[A]): A
  def set[A](w: W[A]): A => Callback
}

object ExternalValue {
  implicit def externalValueViewF[F[_]: Effect]: ExternalValue[ViewF[F, *]] =
    new ExternalValue[ViewF[F, *]] {
      override def get[A](w: ViewF[F, A]): A = w.get

      override def set[A](w: ViewF[F, A]): A => Callback =
        w.set.andThen(_.runInCB)
    }

  implicit val externalValueStateSnapshot: ExternalValue[StateSnapshot] =
    new ExternalValue[StateSnapshot] {
      override def get[A](w: StateSnapshot[A]): A = w.value

      override def set[A](w: StateSnapshot[A]): A => Callback = w.setState
    }
}
