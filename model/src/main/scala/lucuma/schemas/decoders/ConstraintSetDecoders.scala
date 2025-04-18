// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined.*
import lucuma.core.model.AirMassBound
import lucuma.core.model.ConstraintSet
import lucuma.core.model.ElevationRange
import lucuma.core.model.HourAngleBound
import lucuma.core.refined.given

trait ConstraintSetDecoders {
  given Decoder[ElevationRange.ByAirMass] = c =>
    for {
      min <- c.downField("min").as[AirMassBound]
      max <- c.downField("max").as[AirMassBound]
    } yield ElevationRange.ByAirMass.FromBounds.get((min, max))

  given Decoder[ElevationRange.ByHourAngle] = c =>
    for {
      min <- c.downField("minHours").as[HourAngleBound]
      max <- c.downField("maxHours").as[HourAngleBound]
    } yield ElevationRange.ByHourAngle.FromBounds.get((min, max))

  given Decoder[ElevationRange] = c =>
    c.downField("airMass")
      .as[ElevationRange.ByAirMass]
      .orElse(c.downField("hourAngle").as[ElevationRange.ByHourAngle])

  given Decoder[ConstraintSet] = semiauto.deriveDecoder
}
