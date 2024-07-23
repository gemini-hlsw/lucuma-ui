// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import lucuma.core.util.TimeSpan
import lucuma.ui.format.TimeSpanFormatter

trait time:
  extension (timeSpan: TimeSpan)
    /**
     * Format a timespan in the format `${hh}hrs ${mm}mins`
     */
    @deprecated(
      "Use lucuma.ui.format.TimeSpanFormat.HoursMinutesAbbreviation.format() instead.",
      since = "0.113.0"
    )
    def toHoursMinutes: String =
      TimeSpanFormatter.HoursMinutesAbbreviation.format(timeSpan)

object time extends time
