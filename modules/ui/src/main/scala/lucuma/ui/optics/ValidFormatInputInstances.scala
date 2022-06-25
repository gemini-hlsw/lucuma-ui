// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.NonEmptyChain
import cats.data.Validated
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.collection.NonEmpty
import lucuma.core.math.Declination
import lucuma.core.math.RightAscension
import lucuma.core.syntax.string._
import lucuma.refined._
import lucuma.ui.refined._

/**
 * Convenience ValidFormatInput instances.
 */
trait ValidFormatInputInstances {
  val nonEmptyValidFormat = ValidFormatInput[NonEmptyString](
    s =>
      NonEmptyString.from(s).fold(_ => "Can't be empty".refined[NonEmpty].invalidNec, _.validNec),
    _.toString
  )

  val upperNESValidFormat = ValidFormatInput[UpperNES](
    s =>
      UpperNES
        .from(s.toUpperCase)
        .fold(_ => "Can't be empty".refined[NonEmpty].invalidNec, s => s.validNec),
    _.toString
  )

  def intValidFormat(errorMessage: NonEmptyString = "Must be an integer".refined[NonEmpty]) =
    ValidFormatInput[Int](
      s => fixIntString(s).parseIntOption.fold(errorMessage.invalidNec[Int])(_.validNec),
      _.toString
    )

  // Does not, and cannot, format to a particular number of decimal places. For that
  // you need a TruncatedBigDecimal.
  def bigDecimalValidFormat(errorMessage: NonEmptyString = "Must be a number".refined[NonEmpty]) =
    ValidFormatInput[BigDecimal](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .fold(errorMessage.invalidNec[BigDecimal])(_.validNec),
      _.toString.toLowerCase.replace("+", "") // Strip + sign from exponent.
    )

  def truncatedBigDecimalValidFormat[Dec <: XInt](
    errorMessage: NonEmptyString = "Must be a number".refined
  )(implicit req: Require[&&[Dec > 0, Dec < 10]], vo: ValueOf[Dec]) =
    ValidFormatInput[TruncatedBigDecimal[Dec]](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .fold(errorMessage.invalidNec[TruncatedBigDecimal[Dec]])(
            TruncatedBigDecimal[Dec](_).validNec
          ),
      tbd => s"%.${vo.value}f".format(tbd.value)
    )

  val truncatedRA = ValidFormatInput[TruncatedRA](
    s => {
      val ota = RightAscension.fromStringHMS
        .getOption(s)
        .map(TruncatedRA(_))
      Validated.fromOption(ota, NonEmptyChain("Invalid Right Ascension".refined[NonEmpty]))
    },
    tra => {
      val s = RightAscension.fromStringHMS.reverseGet(tra.ra)
      s.dropRight(3)
    }
  )

  val truncatedDec = ValidFormatInput[TruncatedDec](
    s => {
      val otd = Declination.fromStringSignedDMS.getOption(s).map(TruncatedDec(_))
      Validated.fromOption(otd, NonEmptyChain("Invalid Declination".refined[NonEmpty]))
    },
    tdec => {
      val s = Declination.fromStringSignedDMS.reverseGet(tdec.dec)
      s.dropRight(4)
    }
  )

  private def fixIntString(str: String): String = str match {
    case ""  => "0"
    case "-" => "-0"
    case _   => str
  }

  protected def fixDecimalString(str: String): String = str match {
    case ""  => "0"
    case "-" => "-0"
    case "." => "0."
    case _   => str
  }
}
