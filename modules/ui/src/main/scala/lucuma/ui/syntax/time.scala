// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.syntax.eq.*
import lucuma.core.util.TimeSpan

trait time:
  extension (timespan: TimeSpan)
    /**
     * Format a timespan in the format `${hh}hrs ${mm}mins`
     */
    def toHoursMinutes: String =
      val hours   = timespan.toHoursPart
      // Remaining minutes, rounded to the nearest minute
      val minutes = timespan.toMinutes.setScale(0, BigDecimal.RoundingMode.HALF_UP) % 60

      if hours === 0 then s"${minutes}mins"
      else if minutes === 0 then s"${hours}hrs"
      else s"${hours}hrs ${minutes}mins"

object time extends time
