// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.Monad
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.reuse._
import lucuma.core.optics.SplitEpi
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.EnumValue

import scala.annotation.targetName

package object implicits {
  implicit class ViewFOpticOps[F[_], A](val self: ViewF[F, A]) extends AnyVal {
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)
  }

  implicit class ViewOptFOpticOps[F[_], A](val self: ViewOptF[F, A]) extends AnyVal {
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewOptF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)
  }

  implicit class ReuseViewFOpticOps[F[_], A](val self: Reuse[ViewF[F, A]]) extends AnyVal {
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)
  }

  implicit class ReuseViewOptFOpticOps[F[_], A](val self: Reuse[ViewOptF[F, A]]) extends AnyVal {
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewOptF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)
  }

  implicit def displayEnumByTag[A: Enumerated]: Display[A] =
    Display.byShortName(Enumerated[A].tag)

  extension [A](a: A | Unit)(using ev: EnumValue[A])
    @targetName("undefToJs")
    def undefToJs: Unit = a.map(ev.value)

}
