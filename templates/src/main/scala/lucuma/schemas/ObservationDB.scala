// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enums
import lucuma.core.model
import lucuma.core.model._
import lucuma.core.model.sequence._
import lucuma.core.math
import lucuma.core.math.dimensional._
import lucuma.core.math.BrightnessUnits._
// gql: import io.circe.refined._

@GraphQLSchema
trait ObservationDB {
  object Scalars {
    // Ids
    type AtomId           = Atom.Id
    type ExecutionEventId = ExecutionEvent.Id
    type ObservationId    = Observation.Id
    type ProgramId        = Program.Id
    type StepId           = Step.Id
    type TargetId         = Target.Id
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
    // Time
    type Instant          = java.time.Instant
  }

  object Enums {
    type Band                                = enums.Band
    type Breakpoint                          = enums.Breakpoint
    type BrightnessIntegratedUnits           = Units Of Brightness[Integrated]
    type BrightnessSurfaceUnits              = Units Of Brightness[Surface]
    type CatalogName                         = enums.CatalogName
    type CloudExtinction                     = enums.CloudExtinction
    type CoolStarTemperature                 = enums.CoolStarTemperature
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
    type ScienceRequirementMode              = enums.ScienceMode
    type SequenceCommand                     = enums.SequenceCommand
    type SkyBackground                       = enums.SkyBackground
    type SpectroscopyCapabilities            = enums.SpectroscopyCapabilities
    type StellarLibrarySpectrum              = enums.StellarLibrarySpectrum
    type StepStage                           = enums.StepStage
    type StepType                            = enums.StepType
    type TacCategory                         = enums.TacCategory
    type ToOActivation                       = enums.ToOActivation
    type WaterVapor                          = enums.WaterVapor
  }

  object Types {
    type BandNormalizedIntegrated       = model.SpectralDefinition.BandNormalized[Integrated]
    type BandNormalizedSurface          = model.SpectralDefinition.BandNormalized[Surface]
    type BrightnessIntegrated           = Measure[BigDecimal] Of Brightness[Integrated]
    type BrightnessSurface              = Measure[BigDecimal] Of Brightness[Surface]
    type BlackBody                      = model.UnnormalizedSED.BlackBody
    type CoolStarModel                  = model.UnnormalizedSED.CoolStarModel
    type Coordinates                    = math.Coordinates
    type Declination                    = math.Declination
    type Duration                       = java.time.Duration
    type EmissionLineIntegrated         = model.EmissionLine[Integrated]
    type EmissionLineSurface            = model.EmissionLine[Surface]
    type EmissionLinesIntegrated        = model.SpectralDefinition.EmissionLines[Integrated]
    type EmissionLinesSurface           = model.SpectralDefinition.EmissionLines[Surface]
    type FluxDensityContinuumIntegrated = Measure[BigDecimal] Of FluxDensityContinuum[Integrated]
    type FluxDensityContinuumSurface    = Measure[BigDecimal] Of FluxDensityContinuum[Surface]
    type Galaxy                         = model.UnnormalizedSED.Galaxy
    type GaussianSource                 = model.SourceProfile.Gaussian
    type HiiRegion                      = model.UnnormalizedSED.HIIRegion
    type LineFluxIntegrated             = Measure[BigDecimal] Of LineFlux[Integrated]
    type LineFluxSurface                = Measure[BigDecimal] Of LineFlux[Surface]
    type Parallax                       = math.Parallax
    type Planet                         = model.UnnormalizedSED.Planet
    type PlanetaryNebula                = model.UnnormalizedSED.PlanetaryNebula
    type PointSource                    = model.SourceProfile.Point
    type PosAngleConstraint             = model.PosAngleConstraint
    type PowerLaw                       = model.UnnormalizedSED.PowerLaw
    type ProperMotion                   = math.ProperMotion
    type ProperMotionDeclination        = math.ProperMotion.Dec
    type ProperMotionRA                 = math.ProperMotion.RA
    type Proposal                       = model.Proposal
    type Quasar                         = model.UnnormalizedSED.Quasar
    type RadialVelocity                 = math.RadialVelocity
    type RightAscension                 = math.RightAscension
    type Sidereal                       = model.SiderealTracking
    type StellarLibrary                 = model.UnnormalizedSED.StellarLibrary
    type UniformSource                  = model.SourceProfile.Uniform
    type UserDefined                    = model.UnnormalizedSED.UserDefined
    type Wavelength                     = math.Wavelength
  }
}
