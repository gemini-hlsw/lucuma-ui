// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

package object utils extends ReactUtils {

  def abbreviate(s: String, maxLength: Int): String =
    if (s.length > maxLength) s"${s.substring(0, maxLength)}\u2026" else s

  implicit class ListOps[A](val list: List[A]) extends AnyVal {
    def modFirstWhere(find: A => Boolean, mod: A => A): List[A] =
      list.indexWhere(find) match {
        case -1 => list
        case n  => (list.take(n) :+ mod(list(n))) ++ list.drop(n + 1)
      }

    def removeFirstWhere(find: A => Boolean): List[A] =
      list.indexWhere(find) match {
        case -1 => list
        case n  => list.take(n) ++ list.drop(n + 1)
      }
  }

  val versionDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.from(ZoneOffset.UTC))

  val versionDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.from(ZoneOffset.UTC))

}
