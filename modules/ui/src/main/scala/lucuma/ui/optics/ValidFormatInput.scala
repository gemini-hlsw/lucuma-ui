// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.NonEmptyChain
import cats.data.Validated
import cats.data.ValidatedNec
import cats.syntax.all._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{Validate => RefinedValidate}
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.types.numeric.PosBigDecimal
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.syntax.string._
import lucuma.core.optics._
import monocle.Iso
import monocle.Prism
import lucuma.refined._
import scala.compiletime.ops.int.*
import scala.compiletime.ops.boolean.*

import java.math.RoundingMode
import java.text.DecimalFormat
import scala.util.Try

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
    errorMessage: NonEmptyString = "Invalid format".refined
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
    errorMessage: NonEmptyString = "Invalid value".refined
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

  def forRefinedString[P](
    error:      NonEmptyString = "Invalid format".refined
  )(implicit v: RefinedValidate[String, P]): ValidFormatInput[String Refined P] =
    ValidFormat.forRefined[NonEmptyChain[NonEmptyString], String, P](NonEmptyChain(error))

  def forRefinedInt[P](
    error:      NonEmptyString = "Invalid format".refined
  )(implicit v: RefinedValidate[Int, P]): ValidFormatInput[Int Refined P] =
    intValidFormat(error).andThen(
      ValidFormat.forRefined[NonEmptyChain[NonEmptyString], Int, P](NonEmptyChain(error))
    )

  def forPosInt(
    error: NonEmptyString = "Invalid format".refined
  ): ValidFormatInput[PosInt] =
    forRefinedInt[Positive](error)

  def forRefinedBigDecimal[P](
    error:      NonEmptyString = "Invalid format".refined
  )(implicit v: RefinedValidate[BigDecimal, P]): ValidFormatInput[BigDecimal Refined P] =
    bigDecimalValidFormat(error).andThen(
      ValidFormat.forRefined[NonEmptyChain[NonEmptyString], BigDecimal, P](NonEmptyChain(error))
    )

  def forPosBigDecimal(
    error: NonEmptyString = "Invalid format".refined
  ): ValidFormatInput[PosBigDecimal] =
    forRefinedBigDecimal[Positive](error)

  def forRefinedTruncatedBigDecimal[P, Dec <: Int](
    error: NonEmptyString = "Invalid format".refined
  )(implicit
    v:     RefinedValidate[BigDecimal, P],
    req:   Dec > 0 && Dec < 10,
    vo:    ValueOf[Dec]
  ): ValidFormatInput[TruncatedRefinedBigDecimal[P, Dec]] = {
    val prism = refinedPrism[BigDecimal, P]
    ValidFormatInput[TruncatedRefinedBigDecimal[P, Dec]](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .flatMap(prism.getOption(_))
          .flatMap(TruncatedRefinedBigDecimal.apply[P, Dec](_))
          .fold(error.invalidNec[TruncatedRefinedBigDecimal[P, Dec]])(_.validNec),
      trbd => s"%.${vo.value}f".format(trbd.value.value)
    )
  }

  private def scientificNotationFormat(x: BigDecimal): String = {
    val formatter = new DecimalFormat("0.0E0")
    formatter.setRoundingMode(RoundingMode.HALF_UP)
    formatter.setMinimumFractionDigits(if (x.scale > 0) x.precision - 1 else x.scale)
    formatter.format(x.bigDecimal).stripSuffix("E0").toLowerCase
  }

  def forScientificNotationBigDecimal(
    error: NonEmptyString = "Invalid format".refined
  ): ValidFormatInput[BigDecimal] =
    ValidFormatInput(bigDecimalValidFormat(error).getValidated,
                     n => Try(scientificNotationFormat(n)).toOption.orEmpty
    ) // We shouldn't need to catch errors here, but https://github.com/scala-js/scala-js/issues/4655

  def forScientificNotationPosBigDecimal(
    error: NonEmptyString = "Invalid format".refined
  ): ValidFormatInput[PosBigDecimal] =
    ValidFormatInput(forPosBigDecimal(error).getValidated,
                     pbd => scientificNotationFormat(pbd.value)
    )
}
