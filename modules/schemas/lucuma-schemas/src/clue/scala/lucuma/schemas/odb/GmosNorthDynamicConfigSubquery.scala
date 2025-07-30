// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import clue.annotation.GraphQL
import lucuma.core.model.sequence.gmos
import lucuma.odb.json.gmos.given
import lucuma.schemas.ObservationDB

@GraphQL
object GmosNorthDynamicConfigSubquery
    extends GraphQLSubquery.Typed[ObservationDB, gmos.DynamicConfig.GmosNorth]("GmosNorthDynamic"):
  override val subquery: String = s"""
    {
      exposure $TimeSpanSubquery
      readout {
        xBin
        yBin
        ampCount
        ampGain
        ampReadMode
      }
      dtax
      roi
      gratingConfig {
        grating
        order
        wavelength $WavelengthSubquery
      }
      filter
      fpu {
        customMask {
          filename
          slitWidth
        }
        builtin
      }
    }
  """
