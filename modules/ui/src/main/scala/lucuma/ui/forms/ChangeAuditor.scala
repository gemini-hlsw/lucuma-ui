// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
import lucuma.core.math._
import mouse.all._

sealed trait AuditResult[A] extends Product with Serializable
object AuditResult {
  case class Reject[A]() extends AuditResult[A]
  case class Accept[A](newA: Option[A]) extends AuditResult[A]
  case class NewString[A](newS: String, newA: Option[A], cursorOffset: Int) extends AuditResult[A]

  def reject[A]: AuditResult[A] = Reject()
  def accept[A](newA:    Option[A]): AuditResult[A] = Accept(newA)
  def newString[A](newS: String, newA: Option[A], cursorOffset: Int = 0): AuditResult[A] =
    NewString(newS, newA, cursorOffset)
}

object ChangeAuditor {
  def fromFormat[A]: ChangeAuditor[A] = (s, f) =>
    f.getOption(s).fold(AuditResult.reject[A])(newA => AuditResult.accept(newA.some))

  def defaultForNonNegInts[A]: ChangeAuditor[A] = (s, f) => {
    val newS = if (s == "") "0" else s
    fromFormat(newS, f)
    // TODO: Strip leading zeros if there are non-zeros?
  }

  // can be used by regular and refined Ints.
  def defaultForInts[A]: ChangeAuditor[A] = (s, f) => {
    val newS = s match {
      case ""  => "0"
      case "-" => "-0"
      case _   => s
    }
    fromFormat(newS, f)
  }

  def int(min: Int = Int.MinValue, max: Int = Int.MaxValue): ChangeAuditor[Int] = (s, f) => {
    def inRange(i: Int) = i >= min && i <= max
    val vetted = defaultForInts(s, f)
    vetted match {
      case AuditResult.Accept(Some(i)) if inRange(i)          => vetted
      case AuditResult.NewString(_, Some(i), _) if inRange(i) => vetted
      case _                                                  => AuditResult.reject
    }
  }

  def nonNegativeInt(max: Int = Int.MaxValue): ChangeAuditor[Int] = (s, f) => {
    def inRange(i: Int) = i >= 0 && i <= max
    val vetted = defaultForNonNegInts(s, f)
    vetted match {
      case AuditResult.Accept(Some(i)) if inRange(i)          => vetted
      case AuditResult.NewString(_, Some(i), _) if inRange(i) => vetted
      case _                                                  => AuditResult.reject
    }
  }

  val useFormattedValue: ChangeAuditor[String] = (s, f) =>
    f.getOption(s)
      .fold(AuditResult.reject[String])(newS => AuditResult.newString(newS, newS.some))

  val rightAscension: ChangeAuditor[RightAscension] =
    (str, format) => {
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
        format
          .getOption(str)
          .fold(AuditResult.accept[RightAscension](None))(r => AuditResult.accept(r.some))
      else
        AuditResult.reject
    }
}
