// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import clue.annotation.GraphQLSchema
import lucuma.core.enum
import lucuma.core.model
import lucuma.core.model._
import lucuma.core.math
import lucuma.core.math.dimensional._
import lucuma.core.math.BrightnessUnits._
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
    type Band                                = enum.Band
    type Breakpoint                          = enum.Breakpoint
    type BrightnessIntegratedUnits           = Units Of Brightness[Integrated]
    type BrightnessSurfaceUnits              = Units Of Brightness[Surface]
    type CatalogName                         = enum.CatalogName
    type CloudExtinction                     = enum.CloudExtinction
    type CoolStarTemperature                 = enum.CoolStarTemperature
    type DatasetStage                        = enum.DatasetStage
    type EphemerisKeyType                    = enum.EphemerisKeyType
    type FluxDensityContinuumIntegratedUnits = Units Of FluxDensityContinuum[Integrated]
    type FluxDensityContinuumSurfaceUnits    = Units Of FluxDensityContinuum[Surface]
    type FocalPlane                          = enum.FocalPlane
    type GalaxySpectrum                      = enum.GalaxySpectrum
    type GcalArc                             = enum.GcalArc
    type GcalContinuum                       = enum.GcalContinuum
    type GcalDiffuser                        = enum.GcalDiffuser
    type GcalFilter                          = enum.GcalFilter
    type GcalShutter                         = enum.GcalShutter
    type GmosAmpCount                        = enum.GmosAmpCount
    type GmosAmpGain                         = enum.GmosAmpGain
    type GmosAmpReadMode                     = enum.GmosAmpReadMode
    type GmosCustomSlitWidth                 = enum.GmosCustomSlitWidth
    type GmosDisperserOrder                  = enum.GmosDisperserOrder
    type GmosDtax                            = enum.GmosDtax
    type GmosEOffsetting                     = enum.GmosEOffsetting
    type GmosNorthDetector                   = enum.GmosNorthDetector
    type GmosNorthDisperser                  = enum.GmosNorthDisperser
    type GmosNorthFilter                     = enum.GmosNorthFilter
    type GmosNorthFpu                        = enum.GmosNorthFpu
    type GmosNorthStageMode                  = enum.GmosNorthStageMode
    type GmosRoi                             = enum.GmosRoi
    type GmosSouthDetector                   = enum.GmosSouthDetector
    type GmosSouthDisperser                  = enum.GmosSouthDisperser
    type GmosSouthFilter                     = enum.GmosSouthFilter
    type GmosSouthFpu                        = enum.GmosSouthFpu
    type GmosSouthStageMode                  = enum.GmosSouthStageMode
    type GmosXBinning                        = enum.GmosXBinning
    type GmosYBinning                        = enum.GmosYBinning
    type HiiRegionSpectrum                   = enum.HIIRegionSpectrum
    type ImageQuality                        = enum.ImageQuality
    type InstrumentType                      = enum.Instrument
    type LineFluxIntegratedUnits             = Units Of LineFlux[Integrated]
    type LineFluxSurfaceUnits                = Units Of LineFlux[Surface]
    type MosPreImaging                       = enum.MosPreImaging
    type ObsActiveStatus                     = enum.ObsActiveStatus
    type ObsStatus                           = enum.ObsStatus
    type PlanetSpectrum                      = enum.PlanetSpectrum
    type PlanetaryNebulaSpectrum             = enum.PlanetaryNebulaSpectrum
    type QuasarSpectrum                      = enum.QuasarSpectrum
    type ScienceMode                         = enum.ScienceMode
    type SequenceCommand                     = enum.SequenceCommand
    type SkyBackground                       = enum.SkyBackground
    type SpectroscopyCapabilities            = enum.SpectroscopyCapabilities
    type StellarLibrarySpectrum              = enum.StellarLibrarySpectrum
    type StepStage                           = enum.StepStage
    type StepType                            = enum.StepType
    type WaterVapor                          = enum.WaterVapor
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
    type PowerLaw                       = model.UnnormalizedSED.PowerLaw
    type ProperMotion                   = math.ProperMotion
    type ProperMotionDeclination        = math.ProperMotion.Dec
    type ProperMotionRA                 = math.ProperMotion.RA
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
