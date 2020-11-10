// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.Validated._
import cats.syntax.all._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import eu.timepit.refined.numeric.Positive
import lucuma.core.math.Declination
import lucuma.core.math.RightAscension
import mouse.all._

sealed trait AuditResult extends Product with Serializable
object AuditResult {
  case object Reject extends AuditResult
  case object Accept extends AuditResult
  case class NewString(newS: String, cursorOffset: Int) extends AuditResult

  def reject: AuditResult = Reject
  def accept: AuditResult = Accept
  def newString(newS: String, cursorOffset: Int = 0): AuditResult =
    NewString(newS, cursorOffset)
}

sealed trait FilterMode extends Product with Serializable
object FilterMode {
  case object Lax    extends FilterMode
  case object Strict extends FilterMode
}

final case class ChangeAuditor[A](audit: (String, Int) => AuditResult) { self =>

  /**
   * Converts a ChangeAuditor[A] into a ChangeAuditor[Option[A]].
   * It unconditionally allows spaces. This is useful when using
   * a ChangeAuditor made from a Format, but the model field is optional.
   * Hint: If you're going to chain this together with another "modifier"
   * like 'int', you want this one last.
   */
  def optional: ChangeAuditor[Option[A]] = ChangeAuditor { (s, c) =>
    if (s == "") AuditResult.accept else self.audit(s, c)
  }

  /**
   * Unconditionally allows the field to be empty.
   * This is useful when using a ChangeAuditor made from a Format,
   * but you want the user to be able to empty the field while editing,
   * even if the Format won't accept it. This will often be used
   * after the ".int" or ".decimal" methods so that a user will be
   * able to make the field empty while editing, even if the Format
   * doesn't interpret "" as zero.
   */
  def allowEmpty: ChangeAuditor[A] = ChangeAuditor { (s, c) =>
    if (s == "") AuditResult.accept else self.audit(s, c)
  }

  /**
   * Validates the input against ChangeAuditor.int before passing
   * the result on to the "original" ChangeAuditor.
   * This is useful when using a ChangeAuditor made from a format
   * to get better behavior for entering values.
   */
  def int: ChangeAuditor[A] = ChangeAuditor { (s, c) =>
    self.checkAgainstSelf(s, ChangeAuditor.int.audit(s, c))
  }

  /**
   * Validates the input against ChangeAuditor.bigDecimal before passing
   * the result on to the "original" ChangeAuditor.
   * This is useful when using a ChangeAuditor made from a format
   * to get better behavior for entering values.
   */
  def decimal(decimals: Int Refined Positive): ChangeAuditor[A] = ChangeAuditor { (s, c) =>
    self.checkAgainstSelf(s, ChangeAuditor.bigDecimal(decimals).audit(s, c))
  }

  private def checkAgainstSelf(str: String, result: AuditResult): AuditResult = {
    def rejectOrPassOn(s: String) = audit(s, 0) match {
      case AuditResult.Reject => AuditResult.reject
      case _                  => result
    }
    result match {
      case AuditResult.Reject             => AuditResult.reject
      case AuditResult.Accept             => rejectOrPassOn(str)
      case AuditResult.NewString(newS, _) => rejectOrPassOn(newS)
    }
  }
}

object ChangeAuditor {
  import FilterMode._

  def accept[A]: ChangeAuditor[A] = ChangeAuditor((_, _) => AuditResult.accept)

  /**
   * For a plain integer. Only allows entry of numeric values.
   * ALlows the input to be empty or "-", etc. to make entry easier.
   * It also strips leading zeros.
   */
  def int: ChangeAuditor[Int] = ChangeAuditor { (str, cursorPos) =>
    val (formatStr, newStr, offset) = fixIntString(str, cursorPos)
    formatStr.parseIntOption match {
      case None                     => AuditResult.reject
      case Some(_) if newStr == str => AuditResult.accept
      case _                        => AuditResult.newString(newStr, offset)
    }
  }

  /**
   * For a big decimal.
   * ALlows the input to be empty or "-", etc. to make entry easier.
   * It also strips leading and trailing zeros (past the number of
   * allowed decimals).
   *
   * @param decimals - maximum number of allowed decimals.
   */
  def bigDecimal(decimals: Int Refined Positive): ChangeAuditor[BigDecimal] = ChangeAuditor {
    (str, cursorPos) =>
      val (formatStr, newStr, offset) = fixDecimalString(str, cursorPos, decimals.value)
      if (hasNDecimalsOrFewer(newStr, decimals.value))
        formatStr.parseBigDecimalOption match {
          case None                     => AuditResult.reject
          case Some(_) if newStr == str => AuditResult.accept
          case _                        => AuditResult.newString(newStr, offset)
        }
      else AuditResult.reject
  }

  /**
   * For Refined Ints. Only allows entry of numeric values.
   *
   * @param filterMode - If Strict, it validates against the ValidFormatInput
   *                     for the P. If Lax, it only validates that it is an
   *                     Int. This can be useful in instances where the
   *                     ValidFormatInstance makes it difficult to enter values,
   *                     such as for Odd integers.
   */
  def forRefinedInt[P](filterMode: FilterMode = FilterMode.Strict)(implicit
    v:                             RefinedValidate[Int, P]
  ): ChangeAuditor[Int Refined P] = ChangeAuditor { (str, cursorPos) =>
    val (formatStr, newStr, offset) = fixIntString(str, cursorPos)
    val validFormat                 = filterMode match {
      case Strict => ValidFormatInput.forRefinedInt[P]()
      case Lax    => ValidFormatInput.intValidFormat()
    }
    validFormat.getValidated(formatStr) match {
      case Invalid(_)                => AuditResult.reject
      case Valid(_) if newStr == str => AuditResult.accept
      case _                         => AuditResult.newString(newStr, offset)
    }
  }

  /**
   * For Refined Strings.
   *
   * @param filterMode - If Strict, it validates against the ValidFormatInput
   *                     for the P. If Lax, it allows any string.
   * @param formatFn - A formatting function, such as _.toUpperCase and forces
   *                   the input to that format. If the length of the string
   *                   is changed other than truncation, it could mean the
   *                   cursor position might be off.
   */
  def forRefinedString[P](
    filterMode: FilterMode = FilterMode.Strict,
    formatFn:   String => String = identity
  )(implicit
    v:          RefinedValidate[String, P]
  ): ChangeAuditor[String Refined P] = ChangeAuditor { (s, _) =>
    val newStr = formatFn(s)
    val valid  = filterMode match {
      case Strict => ValidFormatInput.forRefinedString[P]().getValidated(newStr)
      case Lax    => newStr.validNec[String]
    }
    valid match {
      case Invalid(_) => AuditResult.reject
      case Valid(_)   => if (newStr == s) AuditResult.accept else AuditResult.newString(newStr)
    }
  }

  /**
   * for RightAscension entry.
   */
  val rightAscension: ChangeAuditor[RightAscension] =
    ChangeAuditor { (str, _) =>
      val stripped = stripZerosPastNPlaces(str, 6)
      val isValid  =
        stripped.split(":").toList match {
          case Nil                                => true // it's just one or more ":"
          case hours :: Nil                       =>
            isValidNDigitInt(hours, 2, 23)
          case hours :: minutes :: Nil            =>
            isValidNDigitInt(hours, 2, 23) && isValidNDigitInt(minutes, 2, 59)
          case hours :: minutes :: seconds :: Nil =>
            isValidNDigitInt(hours, 2, 23) && isValidNDigitInt(minutes, 2, 59) &&
              isValidSeconds(seconds, 6)
          case _                                  => false
        }

      if (isValid)
        if (str == stripped) AuditResult.accept else AuditResult.newString(stripped)
      else
        AuditResult.reject
    }

  /**
   * for Declination entry.
   */
  val declination: ChangeAuditor[Declination] =
    ChangeAuditor { (str, _) =>
      val (sign, noSign) =
        if (str.startsWith("+") || str.startsWith("-")) (str.head, str.tail) else ("", str)
      val stripped       = stripZerosPastNPlaces(noSign, 6)

      val isValid =
        stripped.split(":").toList match {
          case Nil                                  => true // it's just one or more ":"
          case degrees :: Nil                       =>
            isValidNDigitInt(degrees, 2, 90)
          case degrees :: minutes :: Nil            =>
            isValidNDigitInt(degrees, 2, 90) && isValidNDigitInt(minutes, 2, 59)
          case degrees :: minutes :: seconds :: Nil =>
            isValidNDigitInt(degrees, 2, 90) && isValidNDigitInt(minutes, 2, 59) &&
              isValidSeconds(seconds, 6)
          case _                                    => false
        }

      if (isValid)
        if (noSign == stripped) AuditResult.accept else AuditResult.newString(s"$sign$stripped")
      else
        AuditResult.reject
    }

  /**
   * Build from an InputFormat instance.
   */
  def fromFormat[A](f: InputFormat[A]): ChangeAuditor[A] = ChangeAuditor { (s, _) =>
    f.getOption(s).fold(AuditResult.reject)(_ => AuditResult.accept)
  }

  /**
   * Build from a ValidFormatInput instance.
   */
  def fromValidFormatInput[A](v: ValidFormatInput[A]): ChangeAuditor[A] = ChangeAuditor { (s, _) =>
    v.getValidated(s).fold(_ => AuditResult.reject, _ => AuditResult.accept)
  }

  private def fixIntString(str: String, cursorPos: Int): (String, String, Int) =
    str match {
      case ""   => ("0", str, 0)
      case "-"  => ("-0", str, 0)
      case "0-" => ("0", "-", -1)
      case _    => stripZerosBeforeN(str, cursorPos)
    }

  private def fixDecimalString(
    str:       String,
    cursorPos: Int,
    decimals:  Int
  ): (String, String, Int) = {
    val postStripped = stripZerosPastNPlaces(str, decimals)
    postStripped match {
      case ""   => ("0", postStripped, 0)
      case "-"  => ("-0", postStripped, 0)
      case "0-" => ("0", "-", -1)
      case "."  => ("0.0", "0.", 1)
      case _    =>
        val dp = postStripped.indexOf(".")
        val n  = if (dp < 0) cursorPos else math.min(dp, cursorPos)
        stripZerosBeforeN(postStripped, n)
    }
  }

  private def stripZerosPastNPlaces(str: String, n: Int): String = {
    val regex = s"(.*\\.\\d{0,$n}[1-9]*)+0*".r
    str match {
      case regex(base) => base
      case _           => str
    }
  }

  private def stripZerosBeforeN(str: String, n: Int): (String, String, Int) = {
    val (minus, newStr, newPos) =
      if (str.startsWith("-")) ("-", str.substring(1), n - 1) else ("", str, n)
    if (newPos > 0) {
      // We actually only want to strip zeros if there is another digit to the right
      val regex    = s"0{0,$newPos}(\\d+)".r
      val stripped = newStr match {
        case regex(remainder) => remainder
        case _                => newStr
      }
      val s        = s"$minus$stripped"
      (s, s, s.length - str.length)
    } else {
      (str, str, 0)
    }
  }

  private def hasNDecimalsOrFewer(str: String, n: Int): Boolean = {
    val decimalPos = str.indexOf(".")
    decimalPos < 0 || decimalPos > str.length - n - 2
  }

  private def isValidNDigitInt(str: String, maxDigits: Int, maxValue: Int) =
    if (str == "") true
    else
      str.length <= maxDigits && str.parseIntOption
        .map(i => 0 <= i && i <= maxValue)
        .getOrElse(false)

  private def isValidSeconds(str: String, decimals: Int): Boolean =
    if (str == "") true
    else
      (str.indexOf(".") <= 2) && hasNDecimalsOrFewer(str, decimals) && str.parseDoubleOption
        .map(d => d >= 0.0 && d < 60.0)
        .getOrElse(false)
}
