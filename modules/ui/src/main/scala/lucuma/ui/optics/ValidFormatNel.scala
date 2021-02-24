// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import cats.data.NonEmptyList
import cats.data.ValidatedNel

/**
 * Convenience version of `ValidFormat` when the error type is `NonEmptyList[E]`.
 */
object ValidFormatNel {

  /**
   * Build optic that's always valid and doesn't normalize or format
   */
  def id[E, A]: ValidFormatNel[E, A, A] = fromIso(Iso.id[A])

  /**
   * Build optic from getValidated and reverseGet functions.
   */
  def apply[E, T, A](
    getValidated: T => ValidatedNel[E, A],
    reverseGet:   A => T
  ): ValidFormatNel[E, T, A] =
    ValidFormat(getValidated, reverseGet)

  /**
   * Build optics from a Format
   */
  def fromFormat[E, T, A](
    format: Format[T, A],
    error:  E
  ): ValidFormatNel[E, T, A] =
    ValidFormat(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyList.of(error))),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[E, T, A](
    prism: Prism[T, A],
    error: E
  ): ValidFormatNel[E, T, A] =
    fromFormat(Format.fromPrism(prism), error)

  /**
   * Build optics from a Iso
   */
  def fromIso[E, T, A](iso: Iso[T, A]): ValidFormatNel[E, T, A] =
    ValidFormat(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )
}
