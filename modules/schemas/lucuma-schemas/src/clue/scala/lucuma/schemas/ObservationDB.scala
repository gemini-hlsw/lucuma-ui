// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enums
import lucuma.core.math.BrightnessUnits.*
import lucuma.core.math.dimensional.*
import lucuma.core.model.*
import lucuma.core.model.sequence.*
import lucuma.core.util
import lucuma.core.util.Of

// gql: import io.circe.refined.*
// gql: import lucuma.schemas.decoders.given
// gql: import lucuma.odb.json.sequence.given

@GraphQLSchema
trait ObservationDB {

  object Scalars {
    // Ids
    type AtomId                    = Atom.Id
    type AttachmentId              = Attachment.Id
    type CallForProposalsId        = CallForProposals.Id
    type ChronicleId               = scala.Long
    type ClientId                  = Client.Id        // TODO Remove when it's removed from the schema
    type ConfigurationRequestId    = ConfigurationRequest.Id
    type DatasetId                 = Dataset.Id
    type ExecutionEventId          = ExecutionEvent.Id
    type GroupId                   = Group.Id
    type IdempotencyKey            = util.IdempotencyKey
    type ObservationId             = Observation.Id
    type ProgramId                 = Program.Id
    type ProgramNoteId             = ProgramNote.Id
    type ProgramUserId             = ProgramUser.Id
    type StepId                    = Step.Id
    type TargetId                  = Target.Id
    type UserId                    = User.Id
    type VisitId                   = Visit.Id
    // Basic types
    type BigDecimal                = scala.BigDecimal
    type Long                      = scala.Long
    // Formatted strings
    type DmsString                 = String
    type EpochString               = String
    type HmsString                 = String
    type UserInvitationId          = String
    type UserInvitationKey         = String
    // Refined
    type Extinction                = NonNegBigDecimal // """Non-negative floating-point value."""
    type NonEmptyString            = eu.timepit.refined.types.string.NonEmptyString
    type NonNegBigDecimal          = eu.timepit.refined.types.numeric.NonNegBigDecimal
    type NonNegInt                 = eu.timepit.refined.types.numeric.NonNegInt
    type NonNegLong                = eu.timepit.refined.types.numeric.NonNegLong
    type NonNegShort               = eu.timepit.refined.types.numeric.NonNegShort
    type PosBigDecimal             = eu.timepit.refined.types.numeric.PosBigDecimal
    type PosInt                    = eu.timepit.refined.types.numeric.PosInt
    type PosLong                   = eu.timepit.refined.types.numeric.PosLong
    type PosShort                  = eu.timepit.refined.types.numeric.PosShort
    // Core Types
    type DatasetFilename           = lucuma.core.model.sequence.Dataset.Filename
    type EmailAddress              = lucuma.core.data.EmailAddress
    type ProposalReferenceLabel    = lucuma.core.model.ProposalReference
    type ProgramReferenceLabel     = lucuma.core.model.ProgramReference
    type ObservationReferenceLabel = lucuma.core.model.ObservationReference
    type DatasetReferenceLabel     = lucuma.core.model.sequence.DatasetReference
    type Semester                  = lucuma.core.model.Semester
    type SignalToNoise             = lucuma.core.math.SignalToNoise
    type Timestamp                 = lucuma.core.util.Timestamp
    // Enum Meta
    // These mappings cannot be used, because the decoder is for the
    // Enumerated instances, but this prevents a spurious codec from being generated.
    type ProposalStatusMeta        = lucuma.schemas.enums.ProposalStatus

    type Date = java.time.LocalDate
  }

  object Enums {
    type ArcType                             = enums.ArcType
    type AtomExecutionState                  = lucuma.schemas.model.enums.AtomExecutionState
    type AtomStage                           = enums.AtomStage
    type AttachmentType                      = enums.AttachmentType
    type Band                                = enums.Band
    type Breakpoint                          = enums.Breakpoint
    type BrightnessIntegratedUnits           = Units Of Brightness[Integrated]
    type BrightnessSurfaceUnits              = Units Of Brightness[Surface]
    type CalculationState                    = lucuma.core.util.CalculationState
    type CalibrationRole                     = enums.CalibrationRole
    type CallForProposalsType                = enums.CallForProposalsType
    type CatalogName                         = enums.CatalogName
    type ChargeClass                         = enums.ChargeClass
    type CloudExtinctionPreset               = CloudExtinction.Preset
    type ConfigurationRequestStatus          = enums.ConfigurationRequestStatus
    type CoolStarTemperature                 = enums.CoolStarTemperature
    type DatasetQaState                      = enums.DatasetQaState
    type DatasetStage                        = enums.DatasetStage
    type EducationalStatus                   = enums.EducationalStatus
    type EmailStatus                         = enums.EmailStatus
    type EphemerisKeyType                    = enums.EphemerisKeyType
    type FilterType                          = enums.FilterType
    type FluxDensityContinuumIntegratedUnits = Units Of FluxDensityContinuum[Integrated]
    type FluxDensityContinuumSurfaceUnits    = Units Of FluxDensityContinuum[Surface]
    type FocalPlane                          = enums.FocalPlane
    type GalaxySpectrum                      = enums.GalaxySpectrum
    type GcalArc                             = enums.GcalArc
    type GcalContinuum                       = enums.GcalContinuum
    type GcalDiffuser                        = enums.GcalDiffuser
    type GcalFilter                          = enums.GcalFilter
    type GcalShutter                         = enums.GcalShutter
    type Gender                              = enums.Gender
    type GuideProbe                          = enums.GuideProbe
    type Flamingos2CustomSlitWidth           = enums.Flamingos2CustomSlitWidth
    type Flamingos2Disperser                 = enums.Flamingos2Disperser
    type Flamingos2Filter                    = enums.Flamingos2Filter
    type Flamingos2Fpu                       = enums.Flamingos2Fpu
    type Flamingos2LyotWheel                 = enums.Flamingos2LyotWheel
    type Flamingos2ReadMode                  = enums.Flamingos2ReadMode
    type Flamingos2Reads                     = enums.Flamingos2Reads
    type Flamingos2Decker                    = enums.Flamingos2Decker
    type Flamingos2ReadoutMode               = enums.Flamingos2ReadoutMode
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
    type GmosBinning                         = enums.GmosBinning
    type GuideState                          = enums.StepGuideState
    type HiiRegionSpectrum                   = enums.HIIRegionSpectrum
    type ImageQualityPreset                  = ImageQuality.Preset
    type Instrument                          = enums.Instrument
    type LineFluxIntegratedUnits             = Units Of LineFlux[Integrated]
    type LineFluxSurfaceUnits                = Units Of LineFlux[Surface]
    type MosPreImaging                       = enums.MosPreImaging
    type MultipleFiltersMode                 = enums.MultipleFiltersMode
    type ObsActiveStatus                     = enums.ObsActiveStatus
    type ObservationWorkflowState            = enums.ObservationWorkflowState
    type ObsStatus                           = enums.ObsStatus
    type ObserveClass                        = enums.ObserveClass
    type ObservationValidationCode           = enums.ObservationValidationCode
    type Partner                             = enums.Partner
    type PlanetSpectrum                      = enums.PlanetSpectrum
    type PlanetaryNebulaSpectrum             = enums.PlanetaryNebulaSpectrum
    type ProgramType                         = enums.ProgramType
    type ProgramUserRole                     = enums.ProgramUserRole
    type ProposalStatus                      = lucuma.schemas.enums.ProposalStatus
    type QuasarSpectrum                      = enums.QuasarSpectrum
    type ScienceBand                         = enums.ScienceBand
    type ScienceMode                         = enums.ScienceMode
    type SequenceCommand                     = enums.SequenceCommand
    type SequenceType                        = enums.SequenceType
    type Site                                = enums.Site
    type SkyBackground                       = enums.SkyBackground
    type SlewStage                           = enums.SlewStage
    type SpectroscopyCapabilities            = enums.SpectroscopyCapabilities
    type SmartGcalType                       = enums.SmartGcalType
    type StellarLibrarySpectrum              = enums.StellarLibrarySpectrum
    type StepExecutionState                  = lucuma.schemas.model.enums.StepExecutionState
    type StepQaState                         = enums.StepQaState
    type StepStage                           = enums.StepStage
    type StepType                            = enums.StepType
    type TacCategory                         = enums.TacCategory
    type TimeAccountingCategory              = enums.TimeAccountingCategory
    type TimingWindowInclusion               = enums.TimingWindowInclusion
    type ToOActivation                       = enums.ToOActivation
    type WaterVapor                          = enums.WaterVapor
  }
}
