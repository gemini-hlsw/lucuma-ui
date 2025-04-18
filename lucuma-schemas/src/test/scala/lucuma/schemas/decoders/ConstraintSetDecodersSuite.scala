// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import lucuma.core.enums.SkyBackground
import lucuma.core.enums.WaterVapor
import lucuma.core.model.AirMassBound
import lucuma.core.model.CloudExtinction
import lucuma.core.model.ConstraintSet
import lucuma.core.model.ElevationRange
import lucuma.core.model.ImageQuality

class ConstraintSetDecodersSuite extends InputStreamSuite {

  test("Constraint set decoder") {
    val expected =
      ConstraintSet(
        ImageQuality.Preset.PointEight,
        CloudExtinction.Preset.PointThree,
        SkyBackground.Bright,
        WaterVapor.Wet,
        ElevationRange.ByAirMass.FromBounds.get:
          (AirMassBound.unsafeFromBigDecimal(1.0), AirMassBound.unsafeFromBigDecimal(2.0))
      )

    assertParsedStreamEquals("/cs1.json", expected)
  }
}
