// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.data.Validated._
import cats.syntax.all._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import lucuma.core.math._
import mouse.all._

sealed trait AuditResult[A] extends Product with Serializable
object AuditResult {
  case class Reject[A]() extends AuditResult[A]
  case class Accept[A]() extends AuditResult[A]
  case class NewString[A](newS: String, cursorOffset: Int) extends AuditResult[A]

  def reject[A]: AuditResult[A] = Reject()
  def accept[A]: AuditResult[A] = Accept()
  def newString[A](newS: String, cursorOffset: Int = 0): AuditResult[A] =
    NewString(newS, cursorOffset)
}

sealed trait FilterMode extends Product with Serializable
object FilterMode {
  case object Lax    extends FilterMode
  case object Strict extends FilterMode
}

object ChangeAuditor {
  import FilterMode._

  def accept[A]: ChangeAuditor[A] = (_, _) => AuditResult.accept

  /**
   * For a plain integer. Only allows entry of numeric values.
   */
  def forInt: ChangeAuditor[Int] = (str, cursorPos) => {
    val (formatStr, newStr, offset) = fixIntString(str, cursorPos)
    formatStr.parseIntOption match {
      case None                     => AuditResult.reject
      case Some(_) if newStr == str => AuditResult.accept
      case _                        => AuditResult.newString(newStr, offset)
    }
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
  ): ChangeAuditor[Int Refined P] = (str, cursorPos) => {
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
   * Takes a string formatting function, such as _.toUpperCase and forces
   * input to that format. If the length of the string is changed other than
   * at the end, it could mean the cursor position will be off.
   */
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
  ): ChangeAuditor[String Refined P] = (s, _) => {
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
    (str, _) => {
      def stripped = stripZerosPastNPlaces(str, 6)
      def validateHoursOrMins(hoursOrMins: String, max: Int): Option[Unit] =
        if (hoursOrMins == "") ().some
        else
          """\d{1,2}""".r.matches(hoursOrMins).option(()) *>
            hoursOrMins.parseIntOption.flatMap(i => if (i >= 0 && i <= max) ().some else None)

      def validateSeconds(seconds: String): Option[Unit] =
        if (seconds == "") ().some
        else
          """\d{0,2}(\.\d{0,6})?""".r.matches(seconds).option(()) *>
            seconds.parseDoubleOption.flatMap(d => if (d >= 0.0 && d < 60.0) ().some else None)

      val isValid = {
        stripped.split(":").toList match {
          case Nil                                => ().some // it's just one or more ":"
          case hours :: Nil                       =>
            validateHoursOrMins(hours, 23)
          case hours :: minutes :: Nil            =>
            validateHoursOrMins(hours, 23) *> validateHoursOrMins(minutes, 59)
          case hours :: minutes :: seconds :: Nil =>
            validateHoursOrMins(hours, 23) *> validateHoursOrMins(minutes, 59) *>
              validateSeconds(seconds)
          case _                                  => None
        }
      }.fold(false)(_ => true)

      if (isValid)
        if (str == stripped) AuditResult.accept else AuditResult.newString(stripped)
      else
        AuditResult.reject
    }

  /**
   * Build from an InputFormat instance.
   */
  def fromFormat[A](f: InputFormat[A]): ChangeAuditor[A] = (s, _) =>
    f.getOption(s).fold(AuditResult.reject[A])(_ => AuditResult.accept)

  /**
   * Build from a ValidFormatInput instance.
   */
  def fromValidFormatInput[A](v: ValidFormatInput[A]): ChangeAuditor[A] = (s, _) =>
    v.getValidated(s).fold(_ => AuditResult.reject, _ => AuditResult.accept)

  private def fixIntString(str: String, cursorPos: Int): (String, String, Int) = {
    def stripZerosBeforeCursor: (String, String, Int) = {
      val (minus, newStr, newPos) =
        if (str.startsWith("-")) ("-", str.substring(1), cursorPos - 1) else ("", str, cursorPos)
      if (newPos > 0) {
        val regex    = s"^(0{0,$newPos})".r
        val stripped = regex.replaceAllIn(newStr, "")
        val s        = s"$minus$stripped"
        (s, s, newStr.length - stripped.length)
      } else {
        (str, str, 0)
      }
    }
    str match {
      case ""   => ("0", str, 0)
      case "-"  => ("-0", str, 0)
      case "0-" => ("0", "-", -1)
      case _    => stripZerosBeforeCursor
    }
  }

  private def stripZerosPastNPlaces(str: String, n: Int): String = {
    val regex = s"(.*\\.\\d{0,$n}[1-9]*)+0*".r
    str match {
      case regex(base) => base
      case _           => str
    }
  }
}
