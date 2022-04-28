// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import eu.timepit.refined.auto._
import lucuma.core.enum.CloudExtinction
import lucuma.core.enum.ImageQuality
import lucuma.core.enum.SkyBackground
import lucuma.core.enum.WaterVapor
import lucuma.core.model.ConstraintSet
import lucuma.core.model.ElevationRange

class ConstraintSetDecodersSuite extends InputStreamSuite {
  test("Constraint set decoder") {
    val expected =
      ConstraintSet(
        ImageQuality.PointEight,
        CloudExtinction.PointThree,
        SkyBackground.Bright,
        WaterVapor.Wet,
        ElevationRange.AirMass.fromDecimalValues.get((BigDecimal(1.0), BigDecimal(2.0)))
      )

    assertParsedStreamEquals("/cs1.json", expected)
  }
}
