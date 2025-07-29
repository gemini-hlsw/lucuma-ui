// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.format

import lucuma.core.util.TimeSpan
import lucuma.ui.format.TimeSpanFormatter.*

import java.time.Duration

class TimeSpanFormatterSuite extends munit.FunSuite:

  test("TimeSpanFormat should format a TimeSpan"):
    assertEquals(HoursMinutesAbbreviation.format(TimeSpan.Zero), "0mins")
    assertEquals(
      HoursMinutesAbbreviation.format(TimeSpan.unsafeFromDuration(Duration.ofHours(1))),
      "1hrs"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(TimeSpan.unsafeFromDuration(Duration.ofMinutes(30))),
      "30mins"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(1).plusMinutes(30))
      ),
      "1hrs 30mins"
    )

    assertEquals(HoursMinutesLetter.format(TimeSpan.Zero), "0m")
    assertEquals(
      HoursMinutesLetter.format(TimeSpan.unsafeFromDuration(Duration.ofHours(1))),
      "1h"
    )
    assertEquals(
      HoursMinutesLetter.format(TimeSpan.unsafeFromDuration(Duration.ofHours(25).plusMinutes(30))),
      "25h 30m"
    )

    assertEquals(DecimalHours.format(TimeSpan.Zero), "0.00 h")
    assertEquals(
      DecimalHours.format(TimeSpan.unsafeFromDuration(Duration.ofHours(1))),
      "1.00 h"
    )
    assertEquals(
      DecimalHours.format(TimeSpan.unsafeFromDuration(Duration.ofHours(25).plusMinutes(30))),
      "25.50 h"
    )

  test("TimeSpanFormat should round to the nearest minute"):
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofMinutes(30).plusSeconds(30))
      ),
      "31mins"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofMinutes(30).plusSeconds(29))
      ),
      "30mins"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(25).plusMinutes(30).plusSeconds(30))
      ),
      "25hrs 31mins"
    )

    assertEquals(
      HoursMinutesLetter.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(1).plusMinutes(30).plusSeconds(30))
      ),
      "1h 31m"
    )

    assertEquals(
      DecimalHours.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(1).plusMinutes(30))
      ),
      "1.50 h"
    )
    assertEquals(
      DecimalHours.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(1).plusMinutes(30).minusSeconds(19))
      ),
      "1.49 h"
    )

  test("Shortcut 4850 - if seconds round up minutes to 0, hour should increase"):
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofMinutes(59).plusSeconds(39))
      ),
      "1hrs"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(2).plusMinutes(59).plusSeconds(30))
      ),
      "3hrs"
    )
    assertEquals(
      HoursMinutesAbbreviation.format(
        TimeSpan.unsafeFromDuration(Duration.ofHours(2).plusMinutes(59).plusSeconds(29))
      ),
      "2hrs 59mins"
    )
