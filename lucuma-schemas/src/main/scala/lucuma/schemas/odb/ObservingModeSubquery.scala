// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.ObservingMode

object ObservingModeSubquery
    extends GraphQLSubquery.Typed[ObservationDB, ObservingMode]("ObservingMode"):
  override val subquery: String = """
        {
          gmosNorthLongSlit {
            initialGrating
            initialFilter
            initialFpu
            initialCentralWavelength {
              picometers
            }
            grating
            filter
            fpu
            centralWavelength {
              picometers
            }
            defaultXBin
            explicitXBin
            defaultYBin
            explicitYBin
            defaultAmpReadMode
            explicitAmpReadMode
            defaultAmpGain
            explicitAmpGain
            defaultRoi
            explicitRoi
            defaultWavelengthDithers {
              picometers
            }
            explicitWavelengthDithers {
              picometers
            }
            defaultSpatialOffsets {
              microarcseconds
            }
            explicitSpatialOffsets {
              microarcseconds
            }
          }
          gmosSouthLongSlit {
            initialGrating
            initialFilter
            initialFpu
            initialCentralWavelength {
              picometers
            }
            grating
            filter
            fpu
            centralWavelength {
              picometers
            }
            defaultXBin
            explicitXBin
            defaultYBin
            explicitYBin
            defaultAmpReadMode
            explicitAmpReadMode
            defaultAmpGain
            explicitAmpGain
            defaultRoi
            explicitRoi
            defaultWavelengthDithers {
              picometers
            }
            explicitWavelengthDithers {
              picometers
            }
            defaultSpatialOffsets {
              microarcseconds
            }
            explicitSpatialOffsets {
              microarcseconds
            }
          }
        }
      """
