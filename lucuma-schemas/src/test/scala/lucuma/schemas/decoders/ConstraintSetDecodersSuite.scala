// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import coulomb._
import coulomb.policy.spire.standard.given
import coulomb.syntax._
import eu.timepit.refined._
import eu.timepit.refined.auto._
import io.circe.Decoder
import lucuma.core.enums.CloudExtinction
import lucuma.core.enums.ImageQuality
import lucuma.core.enums.SkyBackground
import lucuma.core.enums.WaterVapor
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
        ElevationRange.AirMass.fromDecimalValues.get(
          (
            refineV[ElevationRange.AirMass.Value](BigDecimal(1.0))
              .getOrElse(sys.error("Invalid refined value")),
            refineV[ElevationRange.AirMass.Value](BigDecimal(2.0))
              .getOrElse(sys.error("Invalid refined value"))
          )
        )
      )

    assertParsedStreamEquals("/cs1.json", expected)
  }
}
