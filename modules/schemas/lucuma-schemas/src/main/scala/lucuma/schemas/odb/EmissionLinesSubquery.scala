// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import io.circe.Decoder
import lucuma.core.math.BrightnessUnits.*
import lucuma.core.model.SpectralDefinition
import lucuma.odb.json.sourceprofile.given
import lucuma.schemas.ObservationDB

class EmissionLinesSubquery[T](rootType: String)(using
  Decoder[SpectralDefinition.EmissionLines[T]]
) extends GraphQLSubquery.Typed[ObservationDB, SpectralDefinition.EmissionLines[T]](rootType):
  override val subquery: String = s"""
        {
          lines {
            wavelength $WavelengthSubquery
            lineWidth
            lineFlux {
              value
              units
            }
          }
          fluxDensityContinuum {
            value
            units
          }
        }
      """

object EmissionLinesIntegratedSubquery
    extends EmissionLinesSubquery[Integrated]("EmissionLinesIntegrated")

object EmissionLinesSurfaceSubquery extends EmissionLinesSubquery[Surface]("EmissionLinesSurface")
