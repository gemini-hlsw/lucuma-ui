// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.NonEmptyChain
import cats.data.Validated
import cats.data.ValidatedNec
import cats.syntax.all._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import mouse.all._
import singleton.ops._

/**
 * Convenience version of `ValidFormat` when the error type is `NonEmptyChain[String]` and `T =
 * String`.
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
    getValidated: String => ValidatedNec[NonEmptyString, A],
    reverseGet:   A => String
  ): ValidFormatInput[A] =
    ValidFormat(getValidated, reverseGet)

  /**
   * Build optic from a Format
   */
  def fromFormat[A](
    format:       Format[String, A],
    errorMessage: NonEmptyString = "Invalid format"
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
    errorMessage: NonEmptyString = "Invalid value"
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
    errorMessage: NonEmptyString = "Invalid format"
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

  def forRefinedString[P](
    error:      NonEmptyString = "Invalid format"
  )(implicit v: RefinedValidate[String, P]): ValidFormatInput[String Refined P] =
    ValidFormat.forRefined[NonEmptyChain[NonEmptyString], String, P](NonEmptyChain(error))

  def forRefinedInt[P](
    error:      NonEmptyString = "Invalid format"
  )(implicit v: RefinedValidate[Int, P]): ValidFormatInput[Int Refined P] =
    intValidFormat(error).andThen(
      ValidFormat.forRefined[NonEmptyChain[NonEmptyString], Int, P](NonEmptyChain(error))
    )

  def forRefinedBigDecimal[P](
    error:      NonEmptyString = "Invalid format"
  )(implicit v: RefinedValidate[BigDecimal, P]): ValidFormatInput[BigDecimal Refined P] =
    bigDecimalValidFormat(error).andThen(
      ValidFormat.forRefined[NonEmptyChain[NonEmptyString], BigDecimal, P](NonEmptyChain(error))
    )

  def forRefinedTruncatedBigDecimal[P, Dec <: XInt](
    error: NonEmptyString = "Invalid format"
  )(implicit
    v:   RefinedValidate[BigDecimal, P],
    req: Require[&&[Dec > 0, Dec < 10]],
    vo:  ValueOf[Dec]
  ): ValidFormatInput[TruncatedRefinedBigDecimal[P, Dec]] = {
    val prism = ValidFormat.refinedPrism[BigDecimal, P]
    ValidFormatInput[TruncatedRefinedBigDecimal[P, Dec]](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .flatMap(prism.getOption(_))
          .flatMap(TruncatedRefinedBigDecimal.apply[P, Dec](_))
          .fold(error.invalidNec[TruncatedRefinedBigDecimal[P, Dec]])(_.validNec),
      trbd => s"%.${vo.value}f".format(trbd.value.value)
    )
  }
}
