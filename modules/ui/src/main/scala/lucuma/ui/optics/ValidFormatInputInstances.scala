// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.NonEmptyChain
import cats.data.Validated
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.math.Declination
import lucuma.core.math.RightAscension
import lucuma.ui.refined._
import mouse.all._

/**
 * Convenience ValidFormatInput instances.
 */
trait ValidFormatInputInstances {
  val nonEmptyValidFormat = ValidFormatInput[NonEmptyString](
    s => NonEmptyString.from(s).fold(_ => NonEmptyString("Can't be empty").invalidNec, _.validNec),
    _.toString
  )

  val upperNESValidFormat = ValidFormatInput[UpperNES](
    s =>
      UpperNES
        .from(s.toUpperCase)
        .fold(_ => NonEmptyString("Can't be empty").invalidNec, s => s.validNec),
    _.toString
  )

  def intValidFormat(errorMessage: NonEmptyString = "Must be an integer") = ValidFormatInput[Int](
    s => fixIntString(s).parseIntOption.fold(errorMessage.invalidNec[Int])(_.validNec),
    _.toString
  )

  // does not, and cannot, format to a particular number of decimal places. For that
  // you need a TruncatedBigDecimal.
  def bigDecimalValidFormat(errorMessage: NonEmptyString = "Must be a number") =
    ValidFormatInput[BigDecimal](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .fold(errorMessage.invalidNec[BigDecimal])(_.validNec),
      _.toString
    )

  def truncatedBigDecimalValidFormat(
    decimals:     TruncatedBigDecimal.IntDecimals,
    errorMessage: NonEmptyString = "Must be a number"
  ) =
    ValidFormatInput[TruncatedBigDecimal](
      s =>
        fixDecimalString(s).parseBigDecimalOption
          .fold(errorMessage.invalidNec[TruncatedBigDecimal])(
            TruncatedBigDecimal(_, decimals).validNec
          ),
      tbd => s"%.${decimals.value}f".format(tbd.value)
    )

  val truncatedRA = ValidFormatInput[TruncatedRA](
    s => {
      val ota = RightAscension.fromStringHMS
        .getOption(s)
        .map(TruncatedRA(_))
      Validated.fromOption(ota, NonEmptyChain("Invalid Right Ascension"))
    },
    tra => {
      val s = RightAscension.fromStringHMS.reverseGet(tra.ra)
      s.dropRight(3)
    }
  )

  val truncatedDec = ValidFormatInput[TruncatedDec](
    s => {
      val otd = Declination.fromStringSignedDMS.getOption(s).map(TruncatedDec(_))
      Validated.fromOption(otd, NonEmptyChain("Invalid Declination"))
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
