// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.input

import cats.syntax.all.*
import eu.timepit.refined.api.Validate as RefinedValidate
import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV
import eu.timepit.refined.types.numeric.PosInt
import lucuma.core.optics.*
import lucuma.core.syntax.string.*
import lucuma.core.validation.*
import lucuma.refined.*
import lucuma.ui.input.FormatUtils.*

sealed trait AuditResult extends Product with Serializable
object AuditResult {
  case object Reject                                    extends AuditResult
  case object Accept                                    extends AuditResult
  case class NewString(newS: String, cursorOffset: Int) extends AuditResult

  def reject: AuditResult                                         = Reject
  def accept: AuditResult                                         = Accept
  def newString(newS: String, cursorOffset: Int = 0): AuditResult =
    NewString(newS, cursorOffset)
}

sealed trait FilterMode extends Product with Serializable
object FilterMode {
  case object Lax    extends FilterMode
  case object Strict extends FilterMode
}

final case class ChangeAuditor(audit: (String, Int) => AuditResult) { self =>

  /**
   * Accept if the string meets the condition.
   *
   * @param cond
   *   - Condition to check for.
   */
  def allow(cond: String => Boolean): ChangeAuditor = ChangeAuditor { (s, c) =>
    if (cond(s)) AuditResult.accept else self.audit(s, c)
  }

  /**
   * Reject if the string meets the condition.
   *
   * @param cond
   *   - Condition to check for.
   */
  def deny(cond: String => Boolean): ChangeAuditor = ChangeAuditor { (s, c) =>
    if (cond(s)) AuditResult.reject else self.audit(s, c)
  }

  /**
   * Unconditionally allows the field to be empty. This is useful when using a ChangeAuditor made
   * from a Format, but you want the user to be able to empty the field while editing, even if the
   * Format won't accept it. This will often be used after the ".int" or ".decimal" methods so that
   * a user will be able to make the field empty while editing, even if the Format doesn't interpret
   * "" as zero. Hint: If you're going to chain this together with another "modifier" like 'int',
   * you probably want this one last.
   */
  def allowEmpty: ChangeAuditor = allow(_.isEmpty)

  /**
   * Converts a ChangeAuditor[A] into a ChangeAuditor[Option[A]]. It unconditionally allows spaces.
   * This is useful when using a ChangeAuditor made from a Format, but the model field is optional.
   * Hint: If you're going to chain this together with another "modifier" like 'int', you want this
   * one last.
   */
  def optional: ChangeAuditor = allowEmpty

  /**
   * Unconditionally allows the field to start with minus sign. This is used by the `int` and
   * `decimal` modifiers below, but could possibly be useful elsewhere.
   */
  def allowNeg: ChangeAuditor = allow(_.startsWith("-"))

  /**
   * Unconditionally prevents the field from starting with minus sign. This is used by the `int` and
   * `decimal` modifiers below, but could possibly be useful elsewhere.
   */
  def denyNeg: ChangeAuditor = deny(_.startsWith("-"))

  /**
   * Allows a numeric field to have an exponential part (ie.: e10, e+10 or e-10). Numeric fields
   * don't allow this unless explicitly enabled with this method.
   */
  def allowExp(digits: PosInt = 2.refined): ChangeAuditor = {
    val SplitExp = s"^([^e]*)((?:e[\\+-]?)?)((?:[1-9]\\d{0,${digits.value - 1}})?)$$".r

    ChangeAuditor { (s, c) =>
      s match {
        case SplitExp(base, e, exp) =>
          val expAudit = ChangeAuditor.int.audit(exp, c - base.length - e.length)
          self.audit(base, c.max(base.length)) match {
            case AuditResult.Accept                       => expAudit
            case AuditResult.NewString(newString, cursor) =>
              expAudit match {
                case AuditResult.Accept                     => AuditResult.NewString(newString + e + exp, cursor)
                case AuditResult.NewString(newExpString, _) =>
                  AuditResult.NewString(newString + e + newExpString, cursor)
                case _                                      => AuditResult.reject
              }
            case _                                        => AuditResult.reject
          }
        case _                      =>
          AuditResult.reject
      }
    }
  }

  /**
   * Validates the input against ChangeAuditor.int before passing the result on to the "original"
   * ChangeAuditor. This is useful when using a ChangeAuditor made from a format to get better
   * behavior for entering values.
   */
  def int: ChangeAuditor = {
    // Check to see if negative values are allowed. If this causes
    // problems, we may need to make the check optional.
    val allowNeg = isAllowed("-1")

    val auditor = ChangeAuditor { (s, c) =>
      val (result, newS, newC) = ChangeAuditor.processIntString(s, c)
      self.checkAgainstSelf(newS, newC, result)
    }
    if (allowNeg) auditor.allowNeg else auditor.denyNeg
  }

  /**
   * Validates the input against ChangeAuditor.bigDecimal before passing the result on to the
   * "original" ChangeAuditor. This is useful when using a ChangeAuditor made from a format to get
   * better behavior for entering values.
   */
  def decimal(decimals: PosInt): ChangeAuditor = {
    // Check to see if negative values are allowed. If this causes
    // problems, we may need to make the check optional.
    val negStr   = "-0." + "0" * (decimals.value - 1) + "1"
    val allowNeg = isAllowed(negStr)

    val auditor = ChangeAuditor { (s, c) =>
      val (result, newS, newC) = ChangeAuditor.processDecimalString(s, c, decimals)
      self.checkAgainstSelf(newS, newC, result)
    }
    if (allowNeg) auditor.allowNeg else auditor.denyNeg
  }

  // Separators longer than a single Char add the complexity that the separator itself may be being edited, not sure it's worth it.
  def toSequence(separator: Char = ','): ChangeAuditor =
    ChangeAuditor { (str, cursorPos) =>
      val startIndex = str.lastIndexOf(separator.toString, cursorPos - 1) + 1
      val endIndex_  = str.indexOf(separator.toString, cursorPos)
      val endIndex   = if (endIndex_ == -1) str.length else endIndex_

      this.audit(str.substring(startIndex, endIndex), cursorPos - startIndex) match {
        case AuditResult.NewString(newS, cursorOffset) =>
          AuditResult.NewString(
            str.substring(0, startIndex) + newS + str.substring(endIndex),
            cursorOffset + startIndex
          )
        case other                                     => other
      }
    }

  private def checkAgainstSelf(str: String, cursor: Int, result: AuditResult): AuditResult = {
    def rejectOrPassOn(s: String, c: Int) = audit(s, c) match {
      case AuditResult.Reject => AuditResult.reject
      case _                  => result
    }

    result match {
      case AuditResult.Reject                => AuditResult.reject
      case AuditResult.Accept                => rejectOrPassOn(str, cursor)
      case AuditResult.NewString(newS, newC) => rejectOrPassOn(newS, newC)
    }
  }

  private def isAllowed(s: String): Boolean = audit(s, 0) match {
    case AuditResult.Reject => false
    case _                  => true
  }
}

object ChangeAuditor {
  import FilterMode.*

  def accept[A]: ChangeAuditor = ChangeAuditor((_, _) => AuditResult.accept)

  /**
   * For a string. Simply limits the length of the input string.
   */
  def maxLength(max: PosInt): ChangeAuditor = ChangeAuditor { (str, _) =>
    if (str.length > max.value) AuditResult.reject else AuditResult.accept
  }

  /**
   * For a plain integer. Only allows entry of numeric values. ALlows the input to be empty or "-",
   * etc. to make entry easier. It also strips leading zeros.
   */
  val int: ChangeAuditor = ChangeAuditor { (str, cursorPos) =>
    processIntString(str, cursorPos)._1
  }

  /**
   * For a plain positive integer. Only allows entry of numeric values. ALlows the input to be
   * empty, etc. to make entry easier. It also strips leading zeros.
   */
  val posInt: ChangeAuditor = int.denyNeg

  /**
   * For a big decimal. Allows the input to be empty or "-", etc. to make entry easier. It also
   * strips leading and trailing zeros (past the number of allowed decimals).
   *
   * @param integers
   *   - maximum number of allowed integer digits, or None for unbounded
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  protected def bigDecimal(integers: Option[PosInt], decimals: PosInt): ChangeAuditor =
    ChangeAuditor { (str, cursorPos) =>
      val (result, formatStr, _) = processDecimalString(str, cursorPos, decimals)
      result match {
        case AuditResult.Accept                           =>
          checkIntegerDigits(formatStr, integers)
        case newString @ AuditResult.NewString(newStr, _) =>
          checkIntegerDigits(newStr, integers) match {
            case AuditResult.Accept => newString
            case other              => other
          }
        case reject @ AuditResult.Reject                  => reject
      }
    }

  /**
   * For a big decimal. Allows the input to be empty or "-", etc. to make entry easier. It also
   * strips leading and trailing zeros (past the number of allowed decimals). Allows an unbounded
   * number of integer digits.
   *
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  @inline
  def bigDecimal(decimals: PosInt = 3.refined): ChangeAuditor =
    bigDecimal(none, decimals)

  /**
   * For a big decimal. Allows the input to be empty or "-", etc. to make entry easier. It also
   * strips leading and trailing zeros (past the number of allowed decimals).
   *
   * @param integers
   *   - maximum number of allowed integer digits
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  @inline
  def bigDecimal(integers: PosInt, decimals: PosInt): ChangeAuditor =
    bigDecimal(integers.some, decimals)

  /**
   * For a big decimal. Allows the input to be empty, etc. to make entry easier. It also strips
   * leading and trailing zeros (past the number of allowed decimals).
   *
   * @param integers
   *   - maximum number of allowed integer digits, or None for unbounded
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  @inline
  def posBigDecimal(integers: Option[PosInt], decimals: PosInt): ChangeAuditor =
    bigDecimal(integers, decimals).denyNeg

  /**
   * For a big decimal. Allows the input to be empty, etc. to make entry easier. It also strips
   * leading and trailing zeros (past the number of allowed decimals). Allows an unbounded number of
   * integer digits.
   *
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  @inline
  def posBigDecimal(decimals: PosInt = 3.refined): ChangeAuditor =
    posBigDecimal(none, decimals)

  /**
   * For a positive big decimal. Allows the input to be empty, etc. to make entry easier. It also
   * strips leading and trailing zeros (past the number of allowed decimals).
   *
   * @param integers
   *   - maximum number of allowed integer digits, or None for unbounded
   * @param decimals
   *   - maximum number of allowed decimals.
   */
  @inline
  def posBigDecimal(integers: PosInt, decimals: PosInt): ChangeAuditor =
    posBigDecimal(integers.some, decimals)

  def scientificNotation(
    decimals:       PosInt = 3.refined,
    exponentDigits: PosInt = 2.refined
  ): ChangeAuditor =
    bigDecimal(1.refined[Positive], decimals).allowExp(exponentDigits)

  @inline
  def posScientificNotation(
    decimals:       PosInt = 3.refined,
    exponentDigits: PosInt = 2.refined
  ): ChangeAuditor =
    scientificNotation(decimals, exponentDigits).denyNeg

  /**
   * For Refined Ints. Only allows entry of numeric values.
   *
   * @param filterMode
   *   - If Strict, it validates against the InputValidWedge for the P. If Lax, it only validates
   *     that it is an Int. This can be useful in instances where the ValidFormatInstance makes it
   *     difficult to enter values, such as for Odd integers or other discontinuous ranges. NOTE: If
   *     the filter mode is Strict, the refined Int is tested to see if it allows a value of -1. If
   *     it does not, minus signs are not permitted to be entered. This WOULD cause a problem with
   *     discontinuous ranges that exclude -1 but allow other negative numbers, EXCEPT that, as
   *     noted above, Lax filter mode should be used for this type of refined Int. This is required
   *     because scala, and refined, treat a -0 as a 0.
   */
  def refinedInt[P](filterMode: FilterMode = FilterMode.Strict)(implicit
    v: RefinedValidate[Int, P]
  ): ChangeAuditor = {

    val auditor: ChangeAuditor = ChangeAuditor { (str, cursorPos) =>
      val (formatStr, newStr, offset) = fixIntString(str, cursorPos)
      val validFormat                 = filterMode match {
        case Strict => InputValidSplitEpi.refinedInt[P]
        case Lax    => InputValidSplitEpi.int
      }
      validFormat.getValid(formatStr) match {
        case Left(_)                   => AuditResult.reject
        case Right(_) if newStr == str => AuditResult.accept
        case _                         => AuditResult.newString(newStr, offset)
      }
    }

    if (filterMode == Lax || refinedPrism[Int, P].getOption(-1).isDefined) auditor
    else auditor.denyNeg
  }

  /**
   * For Refined Strings.
   *
   * @param filterMode
   *   - If Strict, it validates against the InputValidWedge for the P. If Lax, it allows any
   *     string.
   * @param formatFn
   *   - A formatting function, such as _.toUpperCase and forces the input to that format. If the
   *     length of the string is changed other than truncation, it could mean the cursor position
   *     might be off.
   */
  def refinedString[P](
    filterMode: FilterMode = FilterMode.Strict,
    formatFn:   String => String = identity
  )(implicit
    v:          RefinedValidate[String, P]
  ): ChangeAuditor = ChangeAuditor { (s, _) =>
    val newStr = formatFn(s)
    val valid  = filterMode match {
      case Strict => InputValidSplitEpi.refinedString[P].getValid(newStr)
      case Lax    => newStr.asRight
    }
    valid match {
      case Left(_)  => AuditResult.reject
      case Right(_) =>
        if (newStr == s) AuditResult.accept else AuditResult.newString(newStr)
    }
  }

  /**
   * for RightAscension entry.
   */
  val truncatedRA: ChangeAuditor =
    ChangeAuditor { (str, _) =>
      val stripped = stripZerosPastNPlaces(str, 3.refined)
      val isValid  =
        stripped.split(":").toList match {
          case Nil                                => true // it's just one or more ":"
          case hours :: Nil                       =>
            isValidNDigitInt(hours, 2, 23)
          case hours :: minutes :: Nil            =>
            isValidNDigitInt(hours, 2, 23) && isValidNDigitInt(minutes, 2, 59)
          case hours :: minutes :: seconds :: Nil =>
            isValidNDigitInt(hours, 2, 23) && isValidNDigitInt(minutes, 2, 59) &&
            isValidSeconds(seconds, 3)
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
  val truncatedDec: ChangeAuditor =
    ChangeAuditor { (str, _) =>
      val (sign, noSign) =
        if (str.startsWith("+") || str.startsWith("-")) (str.head, str.tail) else ("", str)
      val stripped       = stripZerosPastNPlaces(noSign, 2.refined)

      val isValid =
        stripped.split(":").toList match {
          case Nil                                  => true // it's just one or more ":"
          case degrees :: Nil                       =>
            isValidNDigitInt(degrees, 2, 90)
          case degrees :: minutes :: Nil            =>
            isValidNDigitInt(degrees, 2, 90) && isValidNDigitInt(minutes, 2, 59)
          case degrees :: minutes :: seconds :: Nil =>
            isValidNDigitInt(degrees, 2, 90) && isValidNDigitInt(minutes, 2, 59) &&
            isValidSeconds(seconds, 2)
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
  def fromFormat[A](f: InputFormat[A]): ChangeAuditor = ChangeAuditor { (s, _) =>
    f.getOption(s).fold(AuditResult.reject)(_ => AuditResult.accept)
  }

  /**
   * Build from a InputValidSplitEpi instance.
   */
  def fromInputValidSplitEpi[A](v: InputValidSplitEpi[A]): ChangeAuditor = ChangeAuditor { (s, _) =>
    v.getValid(s).fold(_ => AuditResult.reject, _ => AuditResult.accept)
  }

  /**
   * Build from a InputValidWedge instance.
   */
  def fromInputValidWedge[A](v: InputValidWedge[A]): ChangeAuditor = ChangeAuditor { (s, _) =>
    v.getValid(s).fold(_ => AuditResult.reject, _ => AuditResult.accept)
  }

  private def processIntString(str: String, cursorPos: Int): (AuditResult, String, Int) = {
    val (formatStr, newStr, offset) = fixIntString(str, cursorPos)

    val result = formatStr.parseIntOption match {
      case None                     => AuditResult.reject
      case Some(_) if newStr == str => AuditResult.accept
      case _                        => AuditResult.newString(newStr, offset)
    }
    (result, formatStr, offset)
  }

  private def fixIntString(str: String, cursorPos: Int): (String, String, Int) =
    str match {
      case ""   => ("0", str, 0)
      case "-"  => ("-0", str, 0)
      case "0-" => ("0", "-", -1)
      case _    => stripZerosBeforeN(str, cursorPos)
    }

  private def processDecimalString(
    str:       String,
    cursorPos: Int,
    decimals:  PosInt
  ): (AuditResult, String, Int) = {
    val (formatStr, newStr, offset) = fixDecimalString(str, cursorPos, decimals)

    val result =
      if (hasNDecimalsOrFewer(newStr, decimals.value))
        formatStr.parseBigDecimalOption match {
          case None                     => AuditResult.reject
          case Some(_) if newStr == str => AuditResult.accept
          case _                        => AuditResult.newString(newStr, offset)
        }
      else AuditResult.reject

    (result, formatStr, offset)
  }

  private def fixDecimalString(
    str:       String,
    cursorPos: Int,
    decimals:  PosInt
  ): (String, String, Int) = {
    val postStripped = stripZerosPastNPlaces(
      str,
      refineV[NonNegative](decimals.value).getOrElse(sys.error("Should not happen"))
    )
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
        .exists(i => 0 <= i && i <= maxValue)

  private def integerDigits(str: String): Int = {
    val Integers = "-?(\\d*).*".r
    str match {
      case Integers(digits) => digits.length
      case _                => 0
    }
  }

  private def checkIntegerDigits(str: String, digitsOpt: Option[PosInt]): AuditResult =
    digitsOpt.fold(AuditResult.accept)(digits =>
      if (integerDigits(str) <= digits.value) AuditResult.accept
      else AuditResult.reject
    )

  private def isValidSeconds(str: String, decimals: Int): Boolean =
    if (str == "") true
    else
      (str.indexOf(".") <= 2) && hasNDecimalsOrFewer(str, decimals) && str.parseDoubleOption
        .map(d => d >= 0.0 && d < 60.0)
        .getOrElse(false)
}
