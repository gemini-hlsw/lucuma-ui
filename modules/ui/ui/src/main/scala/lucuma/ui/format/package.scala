// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.format

import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosInt
import lucuma.core.math.SignalToNoise
import lucuma.core.util.TimeSpan

import java.time.Duration
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

val DurationLongWithSecondsFormatter: Duration => String = d =>
  val days: Option[Long]   = d.toDays.some.filter(_ > 0)
  val hours: Option[Int]   = d.toHoursPart.some.filter(_ > 0)
  val minutes: Option[Int] = d.toMinutesPart.some.filter(_ > 0 || (days.isEmpty && hours.isEmpty))
  val seconds: Int         = d.toSecondsPart
  List(days, hours, minutes)
    .zip(List("day", "hour", "minute"))
    .map((nOpt, units) => nOpt.map(n => s"$n $units" + (if (n != 1) "s" else "")))
    .flattenOption
    .mkString(", ") + s", ${seconds}s"

val versionDateFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.from(ZoneOffset.UTC))

val versionDateTimeFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.from(ZoneOffset.UTC))

def formatDurationSeconds(ts: TimeSpan): String =
  val seconds = ts.toSeconds
  f"$seconds%.0f sec"

def formatDurationHours(ts: TimeSpan): String =
  val seconds = ts.toSeconds
  if (seconds < 60)
    f"$seconds%.0f sec"
  else if (seconds < 3600)
    f"${seconds / 60.0}%.2f min"
  else
    f"${seconds / 3600.0}%.2f hr"

extension (ts: TimeSpan)
  def formatSeconds: String = formatDurationSeconds(ts)
  def formatHours: String   = formatDurationHours(ts)

def format(time: TimeSpan, count: PosInt): String =
  s"$count × ${formatDurationSeconds(time)} = ${formatDurationHours(time *| count.value)}"

def formatSN(sn: SignalToNoise): String = f"${sn.toBigDecimal.toDouble}%.1f"

extension (sn: SignalToNoise) def format: String = formatSN(sn)

def abbreviate(s: String, maxLength: Int): String =
  if (s.length > maxLength) s"${s.substring(0, maxLength)}\u2026" else s
