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

object ValidateInput {
  val id: ValidateInput[String] = fromIso(Iso.id[String])

  def apply[A](
    getValidated: String => ValidatedNec[String, A],
    reverseGet:   A => String
  ): ValidateInput[A] =
    Validate(getValidated, reverseGet)

  /**
   * Build optics from a Format
   */
  def fromFormat[A](
    format:       Format[String, A],
    errorMessage: String = "Invalid format"
  ): ValidateInput[A] =
    Validate(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage))),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[A](
    prism:        Prism[String, A],
    errorMessage: String = "Invalid value"
  ): ValidateInput[A] =
    fromFormat(Format.fromPrism(prism), errorMessage)

  /**
   * Build optics from a Iso
   */
  def fromIso[A](iso: Iso[String, A]): ValidateInput[A] =
    Validate(
      (iso.get _).andThen(_.valid),
      iso.reverseGet
    )

  /**
   * Build optic from a Format but allow empty values to become `None
   */
  def fromFormatOptional[A](
    format:       Format[String, A],
    errorMessage: String = "Invalid format"
  ): ValidateInput[Option[A]] =
    ValidateInput(
      (a: String) =>
        if (a.isEmpty) Validated.validNec(None)
        else
          format.getOption
            .andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage)))(a)
            .map(x => Some(x)),
      (a: Option[A]) => a.foldMap(format.reverseGet)
    )
}
