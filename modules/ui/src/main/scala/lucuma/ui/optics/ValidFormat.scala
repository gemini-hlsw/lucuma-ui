// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import lucuma.core.optics.SplitEpi

/**
 * A validating and normalizing optic. Behaves similarly to `Format`, but the getter returns
 * a `Validated[E, A]` instead of an `Option[A]`.
 *
 * Laws are the same for `Format`, except that `coverage` allows no normalization to happen
 * as long as there are invalid inputs.
 *
 * Composition with `Format` or stronger optics (`Prism` and `Iso`) yields another `ValidFormat`,
 * and require providing an `E` instance for the invalid cases.
 */
abstract class ValidFormat[E, T, A] extends Serializable { self =>
  val getValidated: T => Validated[E, A]

  val reverseGet: A => T

  /**
   * getValidated and reverseGet, yielding a normalized formatted value if valid. Subsequent
   * getValidated/reverseGet cycles are idempotent.
   */
  def normalize(t: T): Validated[E, T] =
    getValidated(t).map(reverseGet)

  /** Like getValidated, but throws IllegalArgumentException when Invalid. */
  def unsafeGet(t: T): A =
    getValidated(t).getOrElse {
      throw new IllegalArgumentException(s"unsafeGet failed: $t")
    }

  /** Compose with another Validate. */
  def composeValidFormat[B](f: ValidFormat[E, A, B]): ValidFormat[E, T, B] =
    ValidFormat[E, T, B](
      getValidated(_).fold(_.invalid, f.getValidated),
      reverseGet.compose(f.reverseGet)
    )

  /** Compose with a Format. */
  def composeFormat[B](f: Format[A, B], error: E): ValidFormat[E, T, B] =
    composeValidFormat(ValidFormat.fromFormat(f, error))

  /** Compose with a Prism. */
  def composePrism[B](f: Prism[A, B], error: E): ValidFormat[E, T, B] =
    composeValidFormat(ValidFormat.fromPrism(f, error))

  /** Compose with an Iso. */
  def composeIso[B](f: Iso[A, B]): ValidFormat[E, T, B] =
    ValidFormat[E, T, B](
      getValidated(_).map(f.get),
      reverseGet.compose(f.reverseGet)
    )

  /** Compose with a SplitEpi. */
  def composeSplitEpi[B](f: SplitEpi[A, B], error: E): ValidFormat[E, T, B] =
    composeValidFormat(ValidFormat.fromFormat(f.asFormat, error))

  // /** Alias to composeFormat. */
  def ^<-*[B](f: Format[A, B], error: E): ValidFormat[E, T, B] =
    composeFormat(f, error)

  // /** Alias to composePrism. */
  def ^<-?[B](f: Prism[A, B], error: E): ValidFormat[E, T, B] =
    composePrism(f, error)

  // /** Alias to composeIso. */
  def ^<->[B](f: Iso[A, B]): ValidFormat[E, T, B] =
    composeIso(f)

  // /** Format is an invariant functor over A. */
  def imapA[B](f: B => A, g: A => B): ValidFormat[E, T, B] =
    ValidFormat(getValidated(_).map(g), f.andThen(reverseGet))

  // /** Format is an invariant functor over T. */
  def imapT[S](f: T => S, g: S => T): ValidFormat[E, S, A] =
    ValidFormat(g.andThen(getValidated), reverseGet.andThen(f))
}

object ValidFormat {

  /**
   * Build optic that's always valid and doesn't normalize or format
   */
  def id[E, A]: ValidFormat[E, A, A] = fromIso(Iso.id[A])

  /**
   * Build optic from getValidated and reverseGet functions.
   */
  def apply[E, T, A](
    _getValidated: T => Validated[E, A],
    _reverseGet:   A => T
  ): ValidFormat[E, T, A] =
    new ValidFormat[E, T, A] {
      val getValidated: T => Validated[E, A] = _getValidated
      val reverseGet: A => T                 = _reverseGet
    }

  /**
   * Build optic from a Format
   */
  def fromFormat[E, T, A](format: Format[T, A], error: E): ValidFormat[E, T, A] =
    ValidFormat(
      format.getOption.andThen(o => Validated.fromOption(o, error)),
      format.reverseGet
    )

  /**
   * Build optic from a Prism
   */
  def fromPrism[E, T, A](prism: Prism[T, A], error: E): ValidFormat[E, T, A] =
    fromFormat(Format.fromPrism(prism), error)

  /**
   * Build optic from a Iso
   */
  def fromIso[E, T, A](iso: Iso[T, A]): ValidFormat[E, T, A] =
    ValidFormat(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )
}
