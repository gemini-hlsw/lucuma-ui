// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import lucuma.core.math.SignalToNoise
import lucuma.core.math.Wavelength
import lucuma.core.model.ExposureTimeMode
import lucuma.core.refined.auto.*
import lucuma.core.syntax.timespan.*

class ExposureTimeModeDecodersSuite extends InputStreamSuite {
  test("SignalToNoise decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.SignalToNoiseMode(
        value = SignalToNoise.unsafeFromBigDecimalExact(BigDecimal(1.23)),
        at = Wavelength.fromIntNanometers(500).get
      )
    assertParsedStreamEquals("/signalToNoise.json", expected)
  }

  test("FixedExposure decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.TimeAndCountMode(count = 99.refined,
                                        time = 47.µsTimeSpan,
                                        at = Wavelength.fromIntNanometers(500).get
      )
    assertParsedStreamEquals("/fixedExposure.json", expected)
  }
}
