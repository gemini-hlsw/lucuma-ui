// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import lucuma.core.optics.SplitEpi

abstract class Validate[E, T, A] extends Serializable { self =>
  val getValidated: T => Validated[E, A]

  val reverseGet: A => T

  /**
   * getValidated and reverseGet, yielding a normalized formatted value if valid. Subsequent getValidated/reverseGet cycles are
   * idempotent.
   */
  def normalize(t: T): Validated[E, T] =
    getValidated(t).map(reverseGet)

  /** Like getValidated, but throws IllegalArgumentException on failure. */
  def unsafeGet(t: T): A =
    getValidated(t).getOrElse {
      throw new IllegalArgumentException(s"unsafeGet failed: $t")
    }

  /** Compose with another Validate. */
  def composeValidate[B](f: Validate[E, A, B]): Validate[E, T, B] =
    Validate[E, T, B](
      getValidated(_).fold(_.invalid, f.getValidated),
      reverseGet.compose(f.reverseGet)
    )

  /** Compose with a Format. */
  def composeFormat[B](f: Format[A, B], error: E): Validate[E, T, B] =
    composeValidate(Validate.fromFormat(f, error))

  /** Compose with a Prism. */
  def composePrism[B](f: Prism[A, B], error: E): Validate[E, T, B] =
    composeValidate(Validate.fromPrism(f, error))

  /** Compose with an Iso. */
  def composeIso[B](f: Iso[A, B]): Validate[E, T, B] =
    Validate[E, T, B](
      getValidated(_).map(f.get),
      reverseGet.compose(f.reverseGet)
    )

  /** Compose with a SplitEpi. */
  def composeSplitEpi[B](f: SplitEpi[A, B], error: E): Validate[E, T, B] =
    composeValidate(Validate.fromFormat(f.asFormat, error))

  // /** Alias to composeFormat. */
  def ^<-*[B](f: Format[A, B], error: E): Validate[E, T, B] =
    composeFormat(f, error)

  // /** Alias to composePrism. */
  def ^<-?[B](f: Prism[A, B], error: E): Validate[E, T, B] =
    composePrism(f, error)

  // /** Alias to composeIso. */
  def ^<->[B](f: Iso[A, B]): Validate[E, T, B] =
    composeIso(f)

  // /** Format is an invariant functor over A. */
  def imapA[B](f: B => A, g: A => B): Validate[E, T, B] =
    Validate(getValidated(_).map(g), f.andThen(reverseGet))

  // /** Format is an invariant functor over T. */
  def imapT[S](f: T => S, g: S => T): Validate[E, S, A] =
    Validate(g.andThen(getValidated), reverseGet.andThen(f))
}

object Validate {
  def apply[E, T, A](_getValidated: T => Validated[E, A], _reverseGet: A => T): Validate[E, T, A] =
    new Validate[E, T, A] {
      val getValidated: T => Validated[E, A] = _getValidated
      val reverseGet: A => T                 = _reverseGet
    }

  /**
   * Build optics from a Format
   */
  def fromFormat[E, T, A](format: Format[T, A], error: E): Validate[E, T, A] =
    Validate(
      format.getOption.andThen(o => Validated.fromOption(o, error)),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[E, T, A](prism: Prism[T, A], error: E): Validate[E, T, A] =
    fromFormat(Format.fromPrism(prism), error)

  /**
   * Build optics from a Iso
   */
  def fromIso[E, T, A](iso: Iso[T, A]): Validate[E, T, A] =
    Validate(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )
}
