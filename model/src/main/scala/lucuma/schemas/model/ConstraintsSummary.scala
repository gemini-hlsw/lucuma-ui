// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.CloudExtinction
import lucuma.core.enums.ImageQuality
import lucuma.core.enums.SkyBackground
import lucuma.core.enums.WaterVapor
import lucuma.core.model.ConstraintSet
import lucuma.core.model.ElevationRange

case class ConstraintsSummary(
  imageQuality:    ImageQuality,
  cloudExtinction: CloudExtinction,
  skyBackground:   SkyBackground,
  waterVapor:      WaterVapor
) derives Eq:
  def summaryString: String =
    s"${imageQuality.label} ${cloudExtinction.label} ${skyBackground.label} ${waterVapor.label}"

object ConstraintsSummary:
  // Defaults here should match server defaults for a smooth UI experience.
  val Default: ConstraintsSummary =
    ConstraintsSummary(
      ImageQuality.PointEight,
      CloudExtinction.PointThree,
      SkyBackground.Bright,
      WaterVapor.Wet
    )

  extension (cs: ConstraintsSummary)
    // Create a ConstraintSet which is not safe as the elevation range is not set
    def withDefaultElevationRange: ConstraintSet =
      ConstraintSet(
        cs.imageQuality,
        cs.cloudExtinction,
        cs.skyBackground,
        cs.waterVapor,
        ElevationRange.AirMass.Default
      )
