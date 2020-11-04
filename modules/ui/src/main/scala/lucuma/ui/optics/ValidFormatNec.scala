// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import cats.data.NonEmptyChain
import cats.data.ValidatedNec

object ValidFormatNec {
  def id[E, A]: ValidFormatNec[E, A, A] = fromIso(Iso.id[A])

  def apply[E, T, A](
    getValidated: T => ValidatedNec[E, A],
    reverseGet:   A => T
  ): ValidFormatNec[E, T, A] =
    ValidFormat(getValidated, reverseGet)

  /**
   * Build optics from a Format
   */
  def fromFormat[E, T, A](
    format: Format[T, A],
    error:  E
  ): ValidFormatNec[E, T, A] =
    ValidFormat(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyChain(error))),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[E, T, A](
    prism: Prism[T, A],
    error: E
  ): ValidFormatNec[E, T, A] =
    fromFormat(Format.fromPrism(prism), error)

  /**
   * Build optics from a Iso
   */
  def fromIso[E, T, A](iso: Iso[T, A]): ValidFormatNec[E, T, A] =
    ValidFormat(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )
}
