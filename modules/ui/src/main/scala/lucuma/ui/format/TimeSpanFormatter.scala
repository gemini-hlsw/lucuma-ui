// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.format

import cats.syntax.all.*
import lucuma.core.util.TimeSpan

enum TimeSpanFormatter(hoursUnits: String, minutesUnits: String):
  case HoursMinutesLetter       extends TimeSpanFormatter("h", "m")
  case HoursMinutesAbbreviation extends TimeSpanFormatter("hrs", "mins")
  case DecimalHours             extends TimeSpanFormatter(" h", "")

  private def getHoursMinutes(timeSpan: TimeSpan): (Option[BigDecimal], Option[BigDecimal]) =
    this match
      case HoursMinutesLetter | HoursMinutesAbbreviation =>
        val hours: Option[BigDecimal]   =
          timeSpan.toHours.setScale(0, BigDecimal.RoundingMode.FLOOR).some.filterNot(_ === 0)
        val minutes: Option[BigDecimal] =
          (timeSpan.toMinutes.setScale(0, BigDecimal.RoundingMode.HALF_UP) % 60).some
            .filterNot(_ === 0 && hours.nonEmpty)
        (hours, minutes)
      case DecimalHours                                  =>
        (timeSpan.toHours.setScale(2, BigDecimal.RoundingMode.HALF_UP).some, none)

  def format(timeSpan: TimeSpan): String =
    val (hours, minutes)          = getHoursMinutes(timeSpan)
    val hourStr: Option[String]   = hours.map(h => s"$h$hoursUnits")
    val minuteStr: Option[String] = minutes.map(m => s"$m$minutesUnits")
    List(hourStr, minuteStr).flatten.mkString(" ")
