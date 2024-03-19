// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import eu.timepit.refined.*
import lucuma.core.math.SignalToNoise
import lucuma.core.model.ExposureTimeMode
import lucuma.core.syntax.timespan.*
import lucuma.refined.*

class ExposureTimeModeDecodersSuite extends InputStreamSuite {
  test("SignalToNoise decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.SignalToNoiseMode(value =
        SignalToNoise.unsafeFromBigDecimalExact(BigDecimal(1.23))
      )
    assertParsedStreamEquals("/signalToNoise.json", expected)
  }

  test("FixedExposure decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.FixedExposureMode(count = 99.refined, 47.µsTimeSpan)
    assertParsedStreamEquals("/fixedExposure.json", expected)
  }
}
