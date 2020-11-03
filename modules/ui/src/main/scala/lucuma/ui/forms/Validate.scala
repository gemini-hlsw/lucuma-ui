// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.data.Validated
import cats.syntax.all._
import eu.timepit.refined.refineV
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import cats.data.NonEmptyChain
import cats.data.ValidatedNec

abstract class Validate[T, A, E] extends Serializable { self =>
  val reverseGet: A => T

  val getValidated: T => Validated[E, A]
}

case class ValidateA[A](
  getValidated: A => ValidatedNec[String, A],
  reverseGet:   A => A = (a: A) => a
) extends Validate[A, A, NonEmptyChain[String]]

case class InputValidate[A](
  getValidated: String => ValidatedNec[String, A],
  reverseGet:   A => String
) extends Validate[String, A, NonEmptyChain[String]] {
  def compose[B](v: Validate[A, B, NonEmptyChain[String]]): InputValidate[B] =
    InputValidate(t => getValidated(t).andThen(a => v.getValidated(a)),
                  reverseGet.compose(v.reverseGet)
    )
}

object InputValidate {
  val id: InputValidate[String] = fromIso(Iso.id[String])

  val notEmpty: InputValidate[String] =
    InputValidate[String](s => if (s.isEmpty) "Can't be empty".invalidNec else s.valid,
                          identity[String]
    )

  val forInt: InputValidate[Int] =
    fromFormatWithTransform[Int](InputFormat.forInt, defaultStringForInts, "Must be an integer")

  val forNonNegInt: InputValidate[Int] =
    fromFormatWithTransform[Int](InputFormat.forInt,
                                 defaultStringForNonNegInts,
                                 "Must be an non-negative integer"
    )

  def forIntRange(min: Int = Int.MinValue, max: Int = Int.MaxValue): InputValidate[Int] =
    forInt.compose(intRangeA(min, max))

  def forNonNegIntRange(max: Int = Int.MaxValue): InputValidate[Int] =
    forNonNegInt.compose(intRangeA(0, max))

  def forRefinedInt[P](implicit v: RefinedValidate[Int, P]): InputValidate[RefinedInt[P]] =
    fromFormat(InputFormat.forInt).compose(refinedIntA)

  /**
   * Build optics from a Format
   */
  def fromFormat[A](format: Format[String, A], errorMessage: String = "Invalid format") =
    InputValidate(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyChain(errorMessage))),
      format.reverseGet
    )

  def fromFormatWithTransform[A](
    format:       Format[String, A],
    transform:    String => String,
    errorMessage: String
  ) =
    InputValidate(
      s => Validated.fromOption(format.getOption(transform(s)), NonEmptyChain(errorMessage)),
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

  def intRangeA(min: Int = Int.MinValue, max: Int = Int.MaxValue): ValidateA[Int] =
    ValidateA(i =>
      if (min <= i && i <= max) i.validNec
      else s"Must be between $min and $max".invalidNec
    )

  type RefinedInt[P] = Int Refined P
  def refinedIntA[P](implicit
    v: RefinedValidate[Int, P]
  ): Validate[Int, RefinedInt[P], NonEmptyChain[String]] =
    new Validate[Int, RefinedInt[P], NonEmptyChain[String]] {
      val reverseGet: RefinedInt[P] => Int = _.value

      val getValidated: Int => Validated[cats.data.NonEmptyChain[String], RefinedInt[P]] = i =>
        refineV[P](i)(v).toValidated.toValidatedNec

    }

  private def defaultStringForInts(s: String): String = s match {
    case ""  => "0"
    case "-" => "-0"
    case _   => s
  }

  private def defaultStringForNonNegInts(s: String): String = if (s == "") "0" else s
}
