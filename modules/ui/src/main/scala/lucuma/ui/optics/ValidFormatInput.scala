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

/**
 * Convenience version of `ValidFormat` when the error type is `NonEmptyChain[String]` and `T = String`.
 */
object ValidFormatInput extends ValidFormatInputInstances {

  /**
   * Build optic that's always valid and doesn't normalize or format
   */
  val id: ValidFormatInput[String] = fromIso(Iso.id[String])

  /**
   * Build optic from getValidated and reverseGet functions.
   */
  def apply[A](
    getValidated: String => ValidatedNec[String, A],
    reverseGet:   A => String
  ): ValidFormatInput[A] =
    ValidFormat(getValidated, reverseGet)

  /**
   * Build optic from a Format
   */
  def fromFormat[A](
    format:       Format[String, A],
    errorMessage: String = "Invalid format"
  ): ValidFormatInput[A] =
    ValidFormat(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage))),
      format.reverseGet
    )

  /**
   * Build optic from a Prism
   */
  def fromPrism[A](
    prism:        Prism[String, A],
    errorMessage: String = "Invalid value"
  ): ValidFormatInput[A] =
    fromFormat(Format.fromPrism(prism), errorMessage)

  /**
   * Build optic from a Iso
   */
  def fromIso[A](iso: Iso[String, A]): ValidFormatInput[A] =
    ValidFormat(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )

  /**
   * Build optic from a Format but allow empty values to become `None`
   */
  def fromFormatOptional[A](
    format:       Format[String, A],
    errorMessage: String = "Invalid format"
  ): ValidFormatInput[Option[A]] =
    ValidFormatInput(
      (a: String) =>
        if (a.isEmpty) Validated.validNec(None)
        else
          format.getOption
            .andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage)))(a)
            .map(x => Some(x)),
      (a: Option[A]) => a.foldMap(format.reverseGet)
    )
}
