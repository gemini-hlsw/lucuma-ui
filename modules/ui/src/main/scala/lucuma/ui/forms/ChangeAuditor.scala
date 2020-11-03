// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
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

object ChangeAuditor {
  def accept[A]: ChangeAuditor[A] = _ => AuditResult.accept

  def forInt(min: Int = Int.MinValue, max: Int = Int.MaxValue): ChangeAuditor[Int] =
    fromInputValidate(InputValidate.forIntRange(min, max))

  def forNonNegInt(max: Int = Int.MaxValue): ChangeAuditor[Int] =
    fromInputValidate(InputValidate.forNonNegIntRange(max))

  val upperCase: ChangeAuditor[String] = s => AuditResult.newString(s.toUpperCase)

  val rightAscension: ChangeAuditor[RightAscension] =
    str => {
      def validateHoursOrMins(hours: String): Option[Unit] =
        if (hours == "") ().some
        else
          """^\d{1,2}""".r.matches(hours).option(()) *>
            hours.parseIntOption.flatMap(i => if (i >= 0 && i <= 60) ().some else None)

      def validateSeconds(seconds: String): Option[Unit] =
        // TODO: Validate number of decimal places, strip trailing zeros
        if (seconds == "") ().some
        else
          seconds.parseDoubleOption.flatMap(d => if (d >= 0.0 && d <= 60.0) ().some else None)

      val isValid = {
        str.split(":").toList match {
          case Nil                                => ().some // should never get
          case hours :: Nil                       =>
            validateHoursOrMins(hours)
          case hours :: minutes :: Nil            =>
            validateHoursOrMins(hours) *> validateHoursOrMins(minutes)
          case hours :: minutes :: seconds :: Nil =>
            validateHoursOrMins(hours) *> validateHoursOrMins(minutes) *> validateSeconds(seconds)
          case _                                  => None
        }
      }.fold(false)(_ => true)

      if (isValid)
        AuditResult.accept
      else
        AuditResult.reject
    }

  def fromFormat[A](f: InputFormat[A]): ChangeAuditor[A] = s =>
    f.getOption(s).fold(AuditResult.reject[A])(_ => AuditResult.accept)

  def fromInputValidate[A](v: InputValidate[A]): ChangeAuditor[A] = s =>
    v.getValidated(s).fold(_ => AuditResult.reject, _ => AuditResult.accept)

}
