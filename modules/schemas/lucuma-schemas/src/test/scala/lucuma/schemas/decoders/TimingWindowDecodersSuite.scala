// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import lucuma.core.enums.TimingWindowInclusion
import lucuma.core.model.TimingWindow
import lucuma.core.model.TimingWindowEnd
import lucuma.core.model.TimingWindowRepeat
import lucuma.core.util.TimeSpan
import lucuma.core.util.Timestamp

import java.time.LocalDateTime

class TimingWindowDecodersSuite extends InputStreamSuite {

  test("Timing window decoder") {
    val expected =
      List(
        TimingWindow(
          TimingWindowInclusion.Include,
          Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2023, 4, 1, 0, 0)),
          None
        ),
        TimingWindow(
          TimingWindowInclusion.Include,
          Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2023, 4, 1, 0, 0)),
          Some(
            TimingWindowEnd.At(
              Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2023, 5, 1, 0, 0))
            )
          )
        ),
        TimingWindow(
          TimingWindowInclusion.Exclude,
          Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2023, 4, 1, 0, 0)),
          Some(
            TimingWindowEnd.After(
              TimeSpan.unsafeFromMicroseconds(864000000000L),
              Some(
                TimingWindowRepeat(
                  TimeSpan.unsafeFromMicroseconds(1800000000000L),
                  Some(PosInt.unsafeFrom(50))
                )
              )
            )
          )
        )
      )

    assertParsedStreamEquals("/tw1.json", expected)
  }
}
