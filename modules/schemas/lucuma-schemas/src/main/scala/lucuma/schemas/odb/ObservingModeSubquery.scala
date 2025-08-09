// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.model.ObservingMode

object ObservingModeSubquery
    extends GraphQLSubquery.Typed[ObservationDB, ObservingMode]("ObservingMode"):
  override val subquery: String = s"""
        {
          gmosNorthLongSlit {
            initialGrating
            initialFilter
            initialFpu
            initialCentralWavelength $WavelengthSubquery
            grating
            filter
            fpu
            centralWavelength $WavelengthSubquery
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
            defaultWavelengthDithers $WavelengthDitherSubquery
            explicitWavelengthDithers $WavelengthDitherSubquery
            defaultSpatialOffsets $AngleSubquery
            explicitSpatialOffsets $AngleSubquery
          }
          gmosSouthLongSlit {
            initialGrating
            initialFilter
            initialFpu
            initialCentralWavelength $WavelengthSubquery
            grating
            filter
            fpu
            centralWavelength $WavelengthSubquery
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
            defaultWavelengthDithers $WavelengthDitherSubquery
            explicitWavelengthDithers $WavelengthDitherSubquery
            defaultSpatialOffsets $AngleSubquery
            explicitSpatialOffsets $AngleSubquery
          }
          gmosNorthImaging {
            initialFilters
            filters
            defaultMultipleFiltersMode
            explicitMultipleFiltersMode
            defaultBin
            explicitBin
            defaultAmpReadMode
            explicitAmpReadMode
            defaultAmpGain
            explicitAmpGain
            defaultRoi
            explicitRoi
            offsets $OffsetSubquery
          }
          gmosSouthImaging {
            initialFilters
            filters
            defaultMultipleFiltersMode
            explicitMultipleFiltersMode
            defaultBin
            explicitBin
            defaultAmpReadMode
            explicitAmpReadMode
            defaultAmpGain
            explicitAmpGain
            defaultRoi
            explicitRoi
            offsets $OffsetSubquery
          }
          flamingos2LongSlit {
            initialDisperser
            initialFilter
            initialFpu
            disperser
            filter
            fpu
            explicitReadMode
            explicitReads
            defaultDecker
            explicitDecker
            defaultReadoutMode
            explicitReadoutMode
            defaultOffsets $OffsetSubquery
            explicitOffsets $OffsetSubquery
          }
        }
      """
