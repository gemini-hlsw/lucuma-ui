// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.format

import cats.syntax.all.*

import java.time.Duration
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val GppDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd")

val GppTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

val GppTimeTZFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC)

val GppTimeTZFormatterWithZone: DateTimeFormatter =
  DateTimeFormatter.ofPattern("HH:mm 'UTC'").withZone(ZoneOffset.UTC)

val IsoUTCFormatter: DateTimeFormatter =
  DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)

val UtcFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)

val DurationFormatter: Duration => String = d =>
  val hours: Option[Long]  = d.toHours.some.filter(_ > 0)
  val minutes: Option[Int] = d.toMinutesPart.some.filter(_ > 0 || hours.isDefined)
  val seconds: Int         = d.toSecondsPart
  hours.map(h => s"${h}h").orEmpty + minutes.map(m => s"${m}m").orEmpty + s"${seconds}s"

val DurationLongFormatter: Duration => String = d =>
  val days: Option[Long]   = d.toDays.some.filter(_ > 0)
  val hours: Option[Int]   = d.toHoursPart.some.filter(_ > 0)
  val minutes: Option[Int] = d.toMinutesPart.some.filter(_ > 0 || (days.isEmpty && hours.isEmpty))
  List(days, hours, minutes)
    .zip(List("day", "hour", "minute"))
    .map((nOpt, units) => nOpt.map(n => s"$n $units" + (if (n != 1) "s" else "")))
    .flattenOption
    .mkString(", ")
