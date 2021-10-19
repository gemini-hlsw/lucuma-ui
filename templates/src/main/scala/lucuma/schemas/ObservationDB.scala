// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enum
import lucuma.core.model
import lucuma.core.model._
import lucuma.core.math
// gql: import io.circe.refined._

@GraphQLSchema
trait ObservationDB {
  object Scalars {
    // Ids
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
    type Breakpoint               = enum.Breakpoint
    type CatalogName              = enum.CatalogName
    type CloudExtinction          = enum.CloudExtinction
    type DatasetStage             = enum.DatasetStage
    type EphemerisKeyType         = enum.EphemerisKeyType
    type FocalPlane               = enum.FocalPlane
    type GcalArc                  = enum.GcalArc
    type GcalContinuum            = enum.GcalContinuum
    type GcalDiffuser             = enum.GcalDiffuser
    type GcalFilter               = enum.GcalFilter
    type GcalShutter              = enum.GcalShutter
    type GmosAmpCount             = enum.GmosAmpCount
    type GmosAmpReadMode          = enum.GmosAmpReadMode
    type GmosCustomSlitWidth      = enum.GmosCustomSlitWidth
    type GmosDisperserOrder       = enum.GmosDisperserOrder
    type GmosDtax                 = enum.GmosDtax
    type GmosEOffsetting          = enum.GmosEOffsetting
    type GmosNorthDetector        = enum.GmosNorthDetector
    type GmosNorthDisperser       = enum.GmosNorthDisperser
    type GmosNorthFilter          = enum.GmosNorthFilter
    type GmosNorthFpu             = enum.GmosNorthFpu
    type GmosNorthStageMode       = enum.GmosNorthStageMode
    type GmosRoi                  = enum.GmosRoi
    type GmosSouthDetector        = enum.GmosSouthDetector
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
    type ObsActiveStatus          = enum.ObsActiveStatus
    type ObsStatus                = enum.ObsStatus
    type ScienceMode              = enum.ScienceMode
    type SequenceCommand          = enum.SequenceCommand
    type SkyBackground            = enum.SkyBackground
    type SpectroscopyCapabilities = enum.SpectroscopyCapabilities
    type StepStage                = enum.StepStage
    type StepType                 = enum.StepType
    type WaterVapor               = enum.WaterVapor
  }

  object Types {
    type Coordinates             = math.Coordinates
    type Declination             = math.Declination
    type Duration                = java.time.Duration
    type Magnitude               = model.Magnitude
    type Parallax                = math.Parallax
    type ProperMotion            = math.ProperMotion
    type ProperMotionDeclination = math.ProperMotion.Dec
    type ProperMotionRA          = math.ProperMotion.RA
    type RadialVelocity          = math.RadialVelocity
    type RightAscension          = math.RightAscension
    type Sidereal                = model.SiderealTracking
    type Wavelength              = math.Wavelength

  }
}
