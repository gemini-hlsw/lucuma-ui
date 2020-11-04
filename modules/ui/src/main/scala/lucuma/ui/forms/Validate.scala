// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.implicits._
import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import cats.data.NonEmptyChain
import cats.data.ValidatedNec

abstract class Validate[T, A, E] extends Serializable { self =>
  val reverseGet: A => T

  val getValidated: T => Validated[E, A]
}

case class InputValidate[A](
  getValidated: String => ValidatedNec[String, A],
  reverseGet:   A => String
) extends Validate[String, A, NonEmptyChain[String]]

object InputValidate {
  val id: InputValidate[String] = fromIso(Iso.id[String])

  /**
   * Build optics from a Format
   */
  def fromFormat[A](format: Format[String, A], errorMessage: String = "Invalid format") =
    InputValidate(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage))),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[A](prism: Prism[String, A]): InputValidate[A] =
    fromFormat(Format.fromPrism(prism))

  /**
   * Build optics from a Iso
   */
  def fromIso[A](iso: Iso[String, A]): InputValidate[A] =
    fromFormat(Format.fromIso(iso))

  /**
   * Build optic from a Format but allow empty values to become `None
   */
  def fromFormatOptional[A](
    format:       Format[String, A],
    errorMessage: String = "Invalid format"
  ): InputValidate[Option[A]] =
    InputValidate(
      (a: String) =>
        if (a.isEmpty) Validated.validNec(None)
        else
          format.getOption
            .andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage)))(a)
            .map(x => Some(x)),
      (a: Option[A]) => a.foldMap(format.reverseGet)
    )
}
