// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import io.circe.Decoder
import lucuma.core.enums.Band
import lucuma.core.math.BrightnessUnits.*
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.*
import lucuma.schemas.odb.BandBrightnessSubquery

import scala.collection.immutable.SortedMap
class BandBrightnessSubquery[T](
  rootType:                 String,
  override val dataDecoder: Decoder[(Band, BrightnessMeasure[T])]
) extends GraphQLSubquery[ObservationDB](rootType):
  override type Data = (Band, BrightnessMeasure[T])

  override val subquery: String = """
        {
          band
          value
          units
          error
        }
      """

object BandBrightnessIntegratedSubquery
    extends BandBrightnessSubquery[Integrated](
      "BandBrightnessIntegrated",
      brightnessEntryDecoder[Integrated]
    )

object BandBrightnessSurfaceSubquery
    extends BandBrightnessSubquery[Surface](
      "BandBrightnessSurface",
      brightnessEntryDecoder[Surface]
    )
