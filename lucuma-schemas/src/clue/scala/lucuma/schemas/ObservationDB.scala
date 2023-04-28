// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enums
import lucuma.core.math.SignalToNoise
import lucuma.core.math.dimensional._
import lucuma.core.model
import lucuma.core.model._
import lucuma.core.model.sequence._
import lucuma.core.util._
import lucuma.core.math.BrightnessUnits._

// gql: import clue.BigNumberEncoders._
// gql: import io.circe.refined._

@GraphQLSchema
trait ObservationDB {
  object Scalars {
    // Ids
    type AtomId           = Atom.Id
    type ExecutionEventId = ExecutionEvent.Id
    type ObsAttachmentId  = ObsAttachment.Id
    type ObservationId    = Observation.Id
    type ProgramId        = Program.Id
    type StepId           = Step.Id
    type TargetId         = Target.Id
    type UserId           = User.Id
    type VisitId          = Visit.Id
    // Basic types
    type BigDecimal       = scala.BigDecimal
    type Long             = scala.Long
    // Formatted strings
    type DatasetFilename  = String
    type DmsString        = String
    type EpochString      = String
    type HmsString        = String
    // Refined
    type NonEmptyString   = eu.timepit.refined.types.string.NonEmptyString
    type NonNegBigDecimal = eu.timepit.refined.types.numeric.NonNegBigDecimal
    type NonNegInt        = eu.timepit.refined.types.numeric.NonNegInt
    type NonNegLong       = eu.timepit.refined.types.numeric.NonNegLong
    type PosBigDecimal    = eu.timepit.refined.types.numeric.PosBigDecimal
    type PosInt           = eu.timepit.refined.types.numeric.PosInt
    type PosLong          = eu.timepit.refined.types.numeric.PosLong
    // Core Types
    type SignalToNoise    = lucuma.core.math.SignalToNoise
    type Timestamp        = lucuma.core.util.Timestamp
  }

  object Enums {
    type Band                                = enums.Band
    type Breakpoint                          = enums.Breakpoint
    type BrightnessIntegratedUnits           = Units Of Brightness[Integrated]
    type BrightnessSurfaceUnits              = Units Of Brightness[Surface]
    type CatalogName                         = enums.CatalogName
    type CloudExtinction                     = enums.CloudExtinction
    type CoolStarTemperature                 = enums.CoolStarTemperature
    type DatasetQaState                      = enums.DatasetQaState
    type DatasetStage                        = enums.DatasetStage
    type EphemerisKeyType                    = enums.EphemerisKeyType
    type FluxDensityContinuumIntegratedUnits = Units Of FluxDensityContinuum[Integrated]
    type FluxDensityContinuumSurfaceUnits    = Units Of FluxDensityContinuum[Surface]
    type FocalPlane                          = enums.FocalPlane
    type GalaxySpectrum                      = enums.GalaxySpectrum
    type GcalArc                             = enums.GcalArc
    type GcalContinuum                       = enums.GcalContinuum
    type GcalDiffuser                        = enums.GcalDiffuser
    type GcalFilter                          = enums.GcalFilter
    type GcalShutter                         = enums.GcalShutter
    type GmosAmpCount                        = enums.GmosAmpCount
    type GmosAmpGain                         = enums.GmosAmpGain
    type GmosAmpReadMode                     = enums.GmosAmpReadMode
    type GmosCustomSlitWidth                 = enums.GmosCustomSlitWidth
    type GmosGratingOrder                    = enums.GmosGratingOrder
    type GmosDtax                            = enums.GmosDtax
    type GmosEOffsetting                     = enums.GmosEOffsetting
    type GmosNorthBuiltinFpu                 = enums.GmosNorthFpu
    type GmosNorthDetector                   = enums.GmosNorthDetector
    type GmosNorthGrating                    = enums.GmosNorthGrating
    type GmosNorthFilter                     = enums.GmosNorthFilter
    type GmosNorthStageMode                  = enums.GmosNorthStageMode
    type GmosRoi                             = enums.GmosRoi
    type GmosSouthBuiltinFpu                 = enums.GmosSouthFpu
    type GmosSouthDetector                   = enums.GmosSouthDetector
    type GmosSouthGrating                    = enums.GmosSouthGrating
    type GmosSouthFilter                     = enums.GmosSouthFilter
    type GmosSouthStageMode                  = enums.GmosSouthStageMode
    type GmosXBinning                        = enums.GmosXBinning
    type GmosYBinning                        = enums.GmosYBinning
    type HiiRegionSpectrum                   = enums.HIIRegionSpectrum
    type ImageQuality                        = enums.ImageQuality
    type InstrumentType                      = enums.Instrument
    type LineFluxIntegratedUnits             = Units Of LineFlux[Integrated]
    type LineFluxSurfaceUnits                = Units Of LineFlux[Surface]
    type MosPreImaging                       = enums.MosPreImaging
    type ObsActiveStatus                     = enums.ObsActiveStatus
    type ObsStatus                           = enums.ObsStatus
    type Partner                             = model.Partner
    type PlanetSpectrum                      = enums.PlanetSpectrum
    type PlanetaryNebulaSpectrum             = enums.PlanetaryNebulaSpectrum
    type QuasarSpectrum                      = enums.QuasarSpectrum
    type ScienceMode                         = enums.ScienceMode
    type SequenceCommand                     = enums.SequenceCommand
    type SkyBackground                       = enums.SkyBackground
    type SpectroscopyCapabilities            = enums.SpectroscopyCapabilities
    type StellarLibrarySpectrum              = enums.StellarLibrarySpectrum
    type StepQaState                         = enums.StepQaState
    type StepStage                           = enums.StepStage
    type StepType                            = enums.StepType
    type TacCategory                         = enums.TacCategory
    type ToOActivation                       = enums.ToOActivation
    type WaterVapor                          = enums.WaterVapor
  }
}
