// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enum
import lucuma.core.model
import lucuma.core.model._
// gql: import io.circe.refined._

@GraphQLSchema
trait ObservationDB {
  object Scalars {
    // Ids
    type AsterismId          = Asterism.Id
    type AtomId              = Atom.Id
    type ExecutionEventId    = ExecutionEvent.Id
    type ObservationId       = Observation.Id
    type ProgramId           = String
    type StepId              = Step.Id
    type TargetId            = Target.Id
    type TargetEnvironmentId = TargetEnvironment.Id
    // Basic types
    type BigDecimal          = scala.BigDecimal
    type Long                = scala.Long
    // Formatted strings
    type DatasetFilename     = String
    type DmsString           = String
    type EphemerisKey        = model.EphemerisKey
    type EpochString         = String
    type HmsString           = String
    // Refined
    type NonEmptyString      = eu.timepit.refined.types.string.NonEmptyString
    type PosBigDecimal       = eu.timepit.refined.types.numeric.PosBigDecimal
    type PosInt              = eu.timepit.refined.types.numeric.PosInt
    // Time
    type Instant             = java.time.Instant
  }

  object Enums {
    type CatalogName              = enum.CatalogName
    type CloudExtinction          = enum.CloudExtinction
    type EphemerisKeyType         = enum.EphemerisKeyType
    type GcalArc                  = enum.GcalArc
    type GcalContinuum            = enum.GcalContinuum
    type GcalDiffuser             = enum.GcalDiffuser
    type GcalFilter               = enum.GcalFilter
    type GcalShutter              = enum.GcalShutter
    type GmosAmpCount             = enum.GmosAmpCount
    type GmosAmpReadMode          = enum.GmosAmpReadMode
    type GmosCustomSlitWidth      = enum.GmosCustomSlitWidth
    type GmosDetector             = enum.GmosNorthDetector // FIXME when API reflects new model
    type GmosDtax                 = enum.GmosDtax
    type GmosEOffsetting          = enum.GmosEOffsetting
    type GmosNorthDisperser       = enum.GmosNorthDisperser
    type GmosNorthFilter          = enum.GmosNorthFilter
    type GmosNorthFpu             = enum.GmosNorthFpu
    type GmosNorthStageMode       = enum.GmosNorthStageMode
    type GmosRoi                  = enum.GmosRoi
    type GmosSouthDisperser       = enum.GmosSouthDisperser
    type GmosSouthFilter          = enum.GmosSouthFilter
    type GmosSouthFpu             = enum.GmosSouthFpu
    type GmosSouthStageMode       = enum.GmosSouthStageMode
    type GmosXBinning             = enum.GmosXBinning
    type GmosYBinning             = enum.GmosYBinning
    type ImageQuality             = enum.ImageQuality
    type InstrumentType           = enum.Instrument
    type MagnitudeBand            = enum.MagnitudeBand
    type MagnitudeSystem          = enum.MagnitudeSystem
    type MosPreImaging            = enum.MosPreImaging
    type ObsStatus                = enum.ObsStatus
    type ObsActiveStatus          = enum.ObsActiveStatus
    type SkyBackground            = enum.SkyBackground
    type StepType                 = enum.StepType
    type WaterVapor               = enum.WaterVapor
    type ScienceMode              = enum.ScienceMode
    type FocalPlane               = enum.FocalPlane
    type SpectroscopyCapabilities = enum.SpectroscopyCapabilities
  }

  object Types {
    type Duration = java.time.Duration
  }
}
