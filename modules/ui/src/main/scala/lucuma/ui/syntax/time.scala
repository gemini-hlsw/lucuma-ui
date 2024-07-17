// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.syntax.eq.*
import lucuma.core.util.TimeSpan
import lucuma.ui.TimeUnitsFormat

trait time:
  extension (timespan: TimeSpan)
    /**
     * Format a timespan in the format `${hh}hrs ${mm}mins`
     */
    def toHoursMinutes: String =
      toHoursMinutes(TimeUnitsFormat.Abbreviation)

    /**
     * Format a timespan in the format `${hh}hrs ${mm}mins` or `${hh}h ${mm}m`
     */
    def toHoursMinutes(unitsFormat: TimeUnitsFormat): String =
      val hours     = timespan.toHoursPart
      def hourStr   = s"$hours${unitsFormat.hours}"
      // Remaining minutes, rounded to the nearest minute
      val minutes   = timespan.toMinutes.setScale(0, BigDecimal.RoundingMode.HALF_UP) % 60
      def minuteStr = s"$minutes${unitsFormat.minutes}"

      if hours === 0 then minuteStr
      else if minutes === 0 then hourStr
      else s"$hourStr $minuteStr"

object time extends time
