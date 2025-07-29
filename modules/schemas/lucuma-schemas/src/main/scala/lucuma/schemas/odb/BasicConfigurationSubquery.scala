// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.model.BasicConfiguration

object BasicConfigurationSubquery
    extends GraphQLSubquery.Typed[ObservationDB, BasicConfiguration]("ObservingMode"):
  override val subquery: String = s"""
        {
          gmosNorthLongSlit {
            grating
            filter
            fpu
            centralWavelength $WavelengthSubquery
          }
          gmosSouthLongSlit {
            grating
            filter
            fpu
            centralWavelength $WavelengthSubquery
          }
          gmosNorthImaging {
            filters
          }
          gmosSouthImaging {
            filters
          }
          flamingos2LongSlit {
            disperser
            filter
            fpu
          }
        }
      """
