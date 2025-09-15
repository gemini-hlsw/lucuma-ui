// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb.input

import cats.syntax.all.*
import clue.data.Input
import clue.data.syntax.*
import eu.timepit.refined.types.numeric.PosBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.syntax.*
import lucuma.core.enums.ArcType
import lucuma.core.enums.Band
import lucuma.core.enums.GmosNorthFpu
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.math.*
import lucuma.core.math.BrightnessUnits.*
import lucuma.core.math.dimensional.*
import lucuma.core.model.*
import lucuma.core.model.ExposureTimeMode.*
import lucuma.core.model.sequence.StepConfig
import lucuma.core.model.sequence.TelescopeConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2FpuMask
import lucuma.core.model.sequence.flamingos2.Flamingos2StaticConfig
import lucuma.core.model.sequence.gmos
import lucuma.core.model.sequence.gmos.GmosCcdMode
import lucuma.core.model.sequence.gmos.GmosFpuMask
import lucuma.core.model.sequence.gmos.GmosGratingConfig
import lucuma.core.model.sequence.gmos.GmosNodAndShuffle
import lucuma.core.util.*
import lucuma.schemas.ObservationDB.Enums.PartnerLinkType
import lucuma.schemas.ObservationDB.Enums.PosAngleConstraintMode
import lucuma.schemas.ObservationDB.Types.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.ObservingMode

import scala.annotation.targetName
import scala.collection.immutable.SortedMap

extension (id: Observation.Id)
  def toWhereObservation: WhereObservation         =
    WhereObservation(id = WhereOrderObservationId(EQ = id.assign).assign)
  def toObservationEditInput: ObservationEditInput =
    ObservationEditInput(observationId = id.assign)

extension (ids: List[Observation.Id])
  @targetName("ObservationId_toWhereObservation")
  def toWhereObservation: WhereObservation =
    WhereObservation(id = WhereOrderObservationId(IN = ids.assign).assign)

extension (id: Program.Id)
  def toWhereProgram: WhereProgram                 =
    WhereProgram(id = WhereOrderProgramId(EQ = id.assign).assign)
  def toProgramEditInput: ProgramEditInput         =
    ProgramEditInput(programId = id.assign)
  @targetName("ProgramId_toWhereObservation")
  def toWhereObservation: WhereObservation         =
    WhereObservation(program = toWhereProgram.assign)
  @targetName("ProgramId_toObservationEditInput")
  def toObservationEditInput: ObservationEditInput =
    ObservationEditInput(programId = id.assign)
  @targetName("ProgramId_ToWhereTarget")
  def toWhereTarget: WhereTarget                   =
    WhereTarget(program = toWhereProgram.assign)
  def toTargetEditInput: TargetEditInput           =
    TargetEditInput(programId = id.assign)

extension (id: ProgramNote.Id)
  def toWhereProgramNote: WhereProgramNote =
    WhereProgramNote(id = WhereOrderProgramNoteId(EQ = id.assign).assign)

extension (id: ProgramUser.Id)
  def toWhereProgramUser: WhereProgramUser  =
    WhereProgramUser(id = WhereOrderProgramUserId(EQ = id.assign).assign)
  def toDeleteInput: DeleteProgramUserInput =
    DeleteProgramUserInput(programUserId = id)

extension (id: Target.Id)
  def toWhereTarget: WhereTarget =
    WhereTarget(id = WhereOrderTargetId(EQ = id.assign).assign)

extension (ids: List[Target.Id])
  def toWhereTargets: WhereTarget =
    WhereTarget(OR = ids.map(_.toWhereTarget).assign)

extension (id: ConfigurationRequest.Id)
  def toWhereConfigurationRequest: WhereConfigurationRequest =
    WhereConfigurationRequest(id = WhereOrderConfigurationRequestId(EQ = id.assign).assign)

extension (ids: List[ConfigurationRequest.Id])
  def toWhereConfigurationRequest: WhereConfigurationRequest =
    WhereConfigurationRequest(id = WhereOrderConfigurationRequestId(IN = ids.assign).assign)

extension (a: Angle)
  def toInput: AngleInput =
    AngleInput(microarcseconds = a.toMicroarcseconds.assign)

extension (w: Wavelength)
  def toInput: WavelengthInput =
    WavelengthInput(picometers = w.toPicometers.value.assign)

extension (info: CatalogInfo)
  def toInput: CatalogInfoInput =
    CatalogInfoInput(info.catalog.assign, info.id.assign, info.objectType.orIgnore)

extension (ra: RightAscension)
  def toInput: RightAscensionInput =
    RightAscensionInput(microseconds = ra.toHourAngle.toMicroseconds.assign)

extension (dec: Declination)
  def toInput: DeclinationInput =
    DeclinationInput(microarcseconds = dec.toAngle.toMicroarcseconds.assign)

extension (pm: ProperMotion)
  def toInput: ProperMotionInput =
    ProperMotionInput(
      ra = ProperMotionComponentInput(microarcsecondsPerYear = pm.ra.μasy.value.assign),
      dec = ProperMotionComponentInput(microarcsecondsPerYear = pm.dec.μasy.value.assign)
    )

extension (rv: RadialVelocity)
  def toInput: RadialVelocityInput =
    RadialVelocityInput(metersPerSecond = rv.rv.value.assign)

extension (p: Parallax)
  def toInput: ParallaxInput =
    ParallaxInput(microarcseconds = p.μas.value.value.assign)

extension (u: UnnormalizedSED)
  def toInput: UnnormalizedSedInput =
    u match {
      case UnnormalizedSED.StellarLibrary(librarySpectrum)          =>
        UnnormalizedSedInput(stellarLibrary = librarySpectrum.assign)
      case UnnormalizedSED.CoolStarModel(temperature)               =>
        UnnormalizedSedInput(coolStar = temperature.assign)
      case UnnormalizedSED.Galaxy(galaxySpectrum)                   =>
        UnnormalizedSedInput(galaxy = galaxySpectrum.assign)
      case UnnormalizedSED.Planet(planetSpectrum)                   =>
        UnnormalizedSedInput(planet = planetSpectrum.assign)
      case UnnormalizedSED.Quasar(quasarSpectrum)                   =>
        UnnormalizedSedInput(quasar = quasarSpectrum.assign)
      case UnnormalizedSED.HIIRegion(hiiRegionSpectrum)             =>
        UnnormalizedSedInput(hiiRegion = hiiRegionSpectrum.assign)
      case UnnormalizedSED.PlanetaryNebula(planetaryNebulaSpectrum) =>
        UnnormalizedSedInput(planetaryNebula = planetaryNebulaSpectrum.assign)
      case UnnormalizedSED.PowerLaw(index)                          =>
        UnnormalizedSedInput(powerLaw = index.assign)
      case UnnormalizedSED.BlackBody(temperature)                   =>
        UnnormalizedSedInput(blackBodyTempK = temperature.value.assign)
      case UnnormalizedSED.UserDefined(fluxDensities)               =>
        UnnormalizedSedInput(fluxDensities =
          fluxDensities.toSortedMap.toList
            .map { case (wavelength, value) =>
              (wavelength, PosBigDecimal.from(value).toOption)
            }
            .collect { case (wavelength, Some(value)) =>
              FluxDensity(wavelength.toInput, value.value)
            }
            .assign
        )
      case UnnormalizedSED.UserDefinedAttachment(attachmentId)      =>
        UnnormalizedSedInput(fluxDensitiesAttachment = attachmentId.assign)
    }

extension (bs: SortedMap[Band, BrightnessMeasure[Integrated]])
  @targetName("IntegratedBrightnessMap_toInput")
  def toInput: List[BandBrightnessIntegratedInput] =
    bs.toList.map { case (band, measure) =>
      BandBrightnessIntegratedInput(
        band = band,
        value = measure.value.value.value.assign,
        units = Measure.unitsTagged.get(measure).assign,
        error = measure.error.map(_.value.value).orIgnore
      )
    }

extension (bs: SortedMap[Band, BrightnessMeasure[Surface]])
  @targetName("SurfaceBrightnessMap_toInput")
  def toInput: List[BandBrightnessSurfaceInput] =
    bs.toList.map { case (band, measure) =>
      BandBrightnessSurfaceInput(
        band = band,
        value = measure.value.value.value.assign,
        units = Measure.unitsTagged.get(measure).assign,
        error = measure.error.map(_.value.value).orIgnore
      )
    }

extension (b: SpectralDefinition.BandNormalized[Integrated])
  def toInput: BandNormalizedIntegratedInput =
    BandNormalizedIntegratedInput(
      sed = b.sed.map(_.toInput).orUnassign,
      brightnesses = b.brightnesses.toInput.assign
    )

extension (b: SpectralDefinition.BandNormalized[Surface])
  def toInput: BandNormalizedSurfaceInput =
    BandNormalizedSurfaceInput(
      sed = b.sed.map(_.toInput).orUnassign,
      brightnesses = b.brightnesses.toInput.assign
    )

extension (lines: SortedMap[Wavelength, EmissionLine[Integrated]])
  @targetName("IntegratedEmissionLineMap_toInput")
  def toInput: List[EmissionLineIntegratedInput] =
    lines.toList.map { case (wavelength, line) =>
      EmissionLineIntegratedInput(
        wavelength = wavelength.toInput,
        lineWidth = PosBigDecimal.unsafeFrom(line.lineWidth.value.value.value).assign,
        lineFlux = LineFluxIntegratedInput(
          PosBigDecimal.unsafeFrom(line.lineFlux.value.value.value),
          Measure.unitsTagged.get(line.lineFlux)
        ).assign
      )
    }

extension (lines: SortedMap[Wavelength, EmissionLine[Surface]])
  @targetName("SurfaceEmissionLineMap_toInput")
  def toInput: List[EmissionLineSurfaceInput] =
    lines.toList.map { case (wavelength, line) =>
      EmissionLineSurfaceInput(
        wavelength = wavelength.toInput,
        lineWidth = PosBigDecimal.unsafeFrom(line.lineWidth.value.value.value).assign,
        lineFlux = LineFluxSurfaceInput(
          PosBigDecimal.unsafeFrom(line.lineFlux.value.value.value),
          Measure.unitsTagged.get(line.lineFlux)
        ).assign
      )
    }

extension (fdc: FluxDensityContinuumMeasure[Integrated])
  def toInput: FluxDensityContinuumIntegratedInput = FluxDensityContinuumIntegratedInput(
    value = fdc.value.value.value,
    units = Measure.unitsTagged.get(fdc)
  )

extension (fdc: FluxDensityContinuumMeasure[Surface])
  def toInput: FluxDensityContinuumSurfaceInput = FluxDensityContinuumSurfaceInput(
    value = fdc.value.value.value,
    units = Measure.unitsTagged.get(fdc)
  )

extension (e: SpectralDefinition.EmissionLines[Integrated])
  def toInput: EmissionLinesIntegratedInput =
    EmissionLinesIntegratedInput(
      lines = e.lines.toInput.assign,
      fluxDensityContinuum = e.fluxDensityContinuum.toInput.assign
    )

extension (e: SpectralDefinition.EmissionLines[Surface])
  def toInput: EmissionLinesSurfaceInput =
    EmissionLinesSurfaceInput(
      lines = e.lines.toInput.assign,
      fluxDensityContinuum = e.fluxDensityContinuum.toInput.assign
    )

extension (s: SpectralDefinition[Integrated])
  def toInput: SpectralDefinitionIntegratedInput =
    s match
      case b @ SpectralDefinition.BandNormalized(_, _) =>
        SpectralDefinitionIntegratedInput(bandNormalized = b.toInput.assign)
      case e @ SpectralDefinition.EmissionLines(_, _)  =>
        SpectralDefinitionIntegratedInput(emissionLines = e.toInput.assign)

extension (s: SpectralDefinition[Surface])
  def toInput: SpectralDefinitionSurfaceInput =
    s match
      case b @ SpectralDefinition.BandNormalized(_, _) =>
        SpectralDefinitionSurfaceInput(bandNormalized = b.toInput.assign)
      case e @ SpectralDefinition.EmissionLines(_, _)  =>
        SpectralDefinitionSurfaceInput(emissionLines = e.toInput.assign)

extension (s: SourceProfile)
  def toInput: SourceProfileInput =
    s match
      case SourceProfile.Point(definition)          =>
        SourceProfileInput(point = definition.toInput.assign)
      case SourceProfile.Uniform(definition)        =>
        SourceProfileInput(uniform = definition.toInput.assign)
      case SourceProfile.Gaussian(fwhm, definition) =>
        SourceProfileInput(
          gaussian = GaussianInput(fwhm.toInput.assign, definition.toInput.assign).assign
        )

extension (p: PosAngleConstraint)
  def toInput: PosAngleConstraintInput =
    p match
      case PosAngleConstraint.Fixed(angle)               =>
        PosAngleConstraintInput(
          mode = PosAngleConstraintMode.Fixed.assign,
          angle = angle.toInput.assign
        )
      case PosAngleConstraint.AllowFlip(angle)           =>
        PosAngleConstraintInput(
          mode = PosAngleConstraintMode.AllowFlip.assign,
          angle = angle.toInput.assign
        )
      case PosAngleConstraint.ParallacticOverride(angle) =>
        PosAngleConstraintInput(
          mode = PosAngleConstraintMode.ParallacticOverride.assign,
          angle = angle.toInput.assign
        )
      case PosAngleConstraint.AverageParallactic         =>
        PosAngleConstraintInput(
          mode = PosAngleConstraintMode.AverageParallactic.assign
        )
      case PosAngleConstraint.Unbounded                  =>
        PosAngleConstraintInput(mode = PosAngleConstraintMode.Unbounded.assign)

extension (ts: TimeSpan)
  def toInput: TimeSpanInput = TimeSpanInput(microseconds = ts.toMicroseconds.assign)

extension (etm: ExposureTimeMode)
  def toInput: ExposureTimeModeInput = etm match
    case TimeAndCountMode(time, count, at) =>
      ExposureTimeModeInput(timeAndCount =
        TimeAndCountExposureTimeModeInput(
          count = count,
          time = time.toInput,
          at = at.toInput
        ).assign
      )
    case SignalToNoiseMode(value, at)      =>
      ExposureTimeModeInput(signalToNoise =
        SignalToNoiseExposureTimeModeInput(value = value, at = at.toInput).assign
      )

extension (pl: PartnerLink)
  def toInput: PartnerLinkInput =
    pl match
      case PartnerLink.HasPartner(p)         =>
        PartnerLinkInput(PartnerLinkType.HasPartner.assign, p.assign)
      case PartnerLink.HasNonPartner         =>
        PartnerLinkInput(PartnerLinkType.HasNonPartner.assign, none.orUnassign)
      case PartnerLink.HasUnspecifiedPartner =>
        PartnerLinkInput(PartnerLinkType.HasUnspecifiedPartner.assign, none.orUnassign)

extension [A](arc: Arc[A])
  def arcType: ArcType = arc match
    case Arc.Empty()       => ArcType.Empty
    case Arc.Full()        => ArcType.Full
    case Arc.Partial(_, _) => ArcType.Partial

extension (raArc: Arc[RightAscension])
  def toInput: RightAscensionArcInput = RightAscensionArcInput(
    `type` = raArc.arcType,
    start = Arc.start.getOption(raArc).map(_.toInput).orUnassign,
    end = Arc.end.getOption(raArc).map(_.toInput).orUnassign
  )

extension (decArc: Arc[Declination])
  def toInput: DeclinationArcInput = DeclinationArcInput(
    `type` = decArc.arcType,
    start = Arc.start.getOption(decArc).map(_.toInput).orUnassign,
    end = Arc.end.getOption(decArc).map(_.toInput).orUnassign
  )

extension (region: Region)
  def toInput: RegionInput = RegionInput(
    rightAscensionArc = region.raArc.toInput,
    declinationArc = region.decArc.toInput
  )

extension (sidereal: Target.Sidereal)
  def toInput: SiderealInput = SiderealInput(
    ra = sidereal.tracking.baseCoordinates.ra.toInput.assign,
    dec = sidereal.tracking.baseCoordinates.dec.toInput.assign,
    epoch = Epoch.fromString.reverseGet(sidereal.tracking.epoch).assign,
    properMotion = sidereal.tracking.properMotion.map(_.toInput).orIgnore,
    radialVelocity = sidereal.tracking.radialVelocity.map(_.toInput).orIgnore,
    parallax = sidereal.tracking.parallax.map(_.toInput).orIgnore,
    catalogInfo = sidereal.catalogInfo.map(_.toInput).orIgnore
  )

  def toCreateTargetInput(programId: Program.Id): CreateTargetInput =
    CreateTargetInput(
      programId = programId.assign,
      SET = TargetPropertiesInput(
        name = sidereal.name.assign,
        sidereal = toInput.assign,
        sourceProfile = sidereal.sourceProfile.toInput.assign
      )
    )

extension (nonsidereal: Target.Nonsidereal)
  def toInput: NonsiderealInput = NonsiderealInput(
    key = NonEmptyString.unsafeFrom(nonsidereal.ephemerisKey.asJson.toString).assign
  )

  def toCreateTargetInput(programId: Program.Id): CreateTargetInput =
    CreateTargetInput(
      programId = programId.assign,
      SET = TargetPropertiesInput(
        name = nonsidereal.name.assign,
        nonsidereal = toInput.assign,
        sourceProfile = nonsidereal.sourceProfile.toInput.assign
      )
    )

extension (too: Target.Opportunity)
  def toInput: OpportunityInput =
    OpportunityInput(
      region = too.region.toInput
    )

  def toCreateTargetInput(programId: Program.Id): CreateTargetInput =
    CreateTargetInput(
      programId = programId.assign,
      SET = TargetPropertiesInput(
        name = too.name.assign,
        opportunity = toInput.assign,
        sourceProfile = too.sourceProfile.toInput.assign
      )
    )

extension (t: Target)
  def toCreateTargetInput(programId: Program.Id): CreateTargetInput =
    t match
      case s: Target.Sidereal    => s.toCreateTargetInput(programId)
      case n: Target.Nonsidereal => n.toCreateTargetInput(programId)
      case o: Target.Opportunity => o.toCreateTargetInput(programId)

extension (d: WavelengthDither)
  def toInput: WavelengthDitherInput =
    WavelengthDitherInput(picometers = d.toPicometers.value.assign)

extension [A](o: Offset.Component[A])
  def toInput: OffsetComponentInput =
    OffsetComponentInput(microarcseconds = o.toAngle.toMicroarcseconds.assign)

extension (o: Offset) def toInput: OffsetInput = OffsetInput(o.p.toInput, o.q.toInput)

extension (o: ObservingMode.GmosNorthLongSlit)
  def toInput: GmosNorthLongSlitInput = GmosNorthLongSlitInput(
    grating = o.grating.assign,
    filter = o.filter.orUnassign,
    fpu = o.fpu.assign,
    centralWavelength = o.centralWavelength.value.toInput.assign,
    explicitXBin = o.explicitXBin.map(_.value).orUnassign,
    explicitYBin = o.explicitYBin.map(_.value).orUnassign,
    explicitAmpReadMode = o.explicitAmpReadMode.orUnassign,
    explicitAmpGain = o.explicitAmpGain.orUnassign,
    explicitRoi = o.explicitRoi.orUnassign,
    explicitWavelengthDithers = o.explicitWavelengthDithers.map(_.toList.map(_.toInput)).orUnassign,
    explicitSpatialOffsets = o.explicitSpatialOffsets.map(_.toList.map(_.toInput)).orUnassign
  )

extension (o: ObservingMode.GmosSouthLongSlit)
  def toInput: GmosSouthLongSlitInput = GmosSouthLongSlitInput(
    grating = o.grating.assign,
    filter = o.filter.orUnassign,
    fpu = o.fpu.assign,
    centralWavelength = o.centralWavelength.value.toInput.assign,
    explicitXBin = o.explicitXBin.map(_.value).orUnassign,
    explicitYBin = o.explicitYBin.map(_.value).orUnassign,
    explicitAmpReadMode = o.explicitAmpReadMode.orUnassign,
    explicitAmpGain = o.explicitAmpGain.orUnassign,
    explicitRoi = o.explicitRoi.orUnassign,
    explicitWavelengthDithers = o.explicitWavelengthDithers.map(_.toList.map(_.toInput)).orUnassign,
    explicitSpatialOffsets = o.explicitSpatialOffsets.map(_.toList.map(_.toInput)).orUnassign
  )

extension (o: ObservingMode.GmosNorthImaging)
  def toInput: GmosNorthImagingInput = GmosNorthImagingInput(
    filters = o.filters.toList.assign,
    offsets = o.offsets.map(_.toInput).assign,
    explicitMultipleFiltersMode = o.explicitMultipleFiltersMode.orUnassign,
    explicitBin = o.explicitBin.orUnassign,
    explicitAmpReadMode = o.explicitAmpReadMode.orUnassign,
    explicitAmpGain = o.explicitAmpGain.orUnassign,
    explicitRoi = o.explicitRoi.orUnassign
  )

extension (o: ObservingMode.GmosSouthImaging)
  def toInput: GmosSouthImagingInput = GmosSouthImagingInput(
    filters = o.filters.toList.assign,
    offsets = o.offsets.map(_.toInput).assign,
    explicitMultipleFiltersMode = o.explicitMultipleFiltersMode.orUnassign,
    explicitBin = o.explicitBin.orUnassign,
    explicitAmpReadMode = o.explicitAmpReadMode.orUnassign,
    explicitAmpGain = o.explicitAmpGain.orUnassign,
    explicitRoi = o.explicitRoi.orUnassign
  )

extension (o: ObservingMode.Flamingos2LongSlit)
  def toInput: Flamingos2LongSlitInput = Flamingos2LongSlitInput(
    disperser = o.disperser.assign,
    filter = o.filter.assign,
    fpu = o.fpu.assign,
    explicitReadMode = o.explicitReadMode.orUnassign,
    explicitReads = o.explicitReads.orUnassign,
    explicitDecker = o.explicitDecker.orUnassign,
    explicitReadoutMode = o.explicitReadoutMode.orUnassign,
    explicitOffsets = o.explicitOffsets.map(_.toList.map(_.toInput)).orUnassign
  )

extension (b: ObservingMode)
  def toInput: ObservingModeInput = b match
    case o: ObservingMode.GmosNorthLongSlit  =>
      ObservingModeInput(gmosNorthLongSlit = o.toInput.assign)
    case o: ObservingMode.GmosSouthLongSlit  =>
      ObservingModeInput(gmosSouthLongSlit = o.toInput.assign)
    case o: ObservingMode.GmosNorthImaging   =>
      ObservingModeInput(gmosNorthImaging = o.toInput.assign)
    case o: ObservingMode.GmosSouthImaging   =>
      ObservingModeInput(gmosSouthImaging = o.toInput.assign)
    case o: ObservingMode.Flamingos2LongSlit =>
      ObservingModeInput(flamingos2LongSlit = o.toInput.assign)

extension (i: BasicConfiguration)
  def toInput: ObservingModeInput = i match
    case o: BasicConfiguration.GmosNorthLongSlit  =>
      ObservingModeInput(
        gmosNorthLongSlit = GmosNorthLongSlitInput(
          grating = o.grating.assign,
          filter = o.filter.orUnassign,
          fpu = o.fpu.assign,
          centralWavelength = o.centralWavelength.value.toInput.assign
        ).assign
      )
    case o: BasicConfiguration.GmosSouthLongSlit  =>
      ObservingModeInput(
        gmosSouthLongSlit = GmosSouthLongSlitInput(
          grating = o.grating.assign,
          filter = o.filter.orUnassign,
          fpu = o.fpu.assign,
          centralWavelength = o.centralWavelength.value.toInput.assign
        ).assign
      )
    case o: BasicConfiguration.GmosNorthImaging   =>
      ObservingModeInput(
        gmosNorthImaging = GmosNorthImagingInput(
          filters = o.filter.toList.assign
        ).assign
      )
    case o: BasicConfiguration.GmosSouthImaging   =>
      ObservingModeInput(
        gmosSouthImaging = GmosSouthImagingInput(
          filters = o.filter.toList.assign
        ).assign
      )
    case o: BasicConfiguration.Flamingos2LongSlit =>
      ObservingModeInput(
        flamingos2LongSlit = Flamingos2LongSlitInput(
          disperser = o.disperser.assign,
          filter = o.filter.assign,
          fpu = o.fpu.assign
        ).assign
      )

extension (er: ElevationRange)
  def toInput: ElevationRangeInput =
    er match
      case ElevationRange.ByAirMass(min, max)   =>
        ElevationRangeInput(airMass =
          // These are actually safe, because min and max in the model are refined [1.0 - 3.0]
          AirMassRangeInput(
            min = PosBigDecimal.unsafeFrom(min.toBigDecimal).assign,
            max = PosBigDecimal.unsafeFrom(max.toBigDecimal).assign
          ).assign
        )
      case ElevationRange.ByHourAngle(min, max) =>
        ElevationRangeInput(hourAngle =
          HourAngleRangeInput(
            minHours = min.toBigDecimal.assign,
            maxHours = max.toBigDecimal.assign
          ).assign
        )

extension (cs: ConstraintSet)
  def toInput: ConstraintSetInput =
    ConstraintSetInput(
      imageQuality = cs.imageQuality.assign,
      cloudExtinction = cs.cloudExtinction.assign,
      skyBackground = cs.skyBackground.assign,
      waterVapor = cs.waterVapor.assign,
      elevationRange = cs.elevationRange.toInput.assign
    )

extension (twea: TimingWindowRepeat)
  def toInput: TimingWindowRepeatInput =
    TimingWindowRepeatInput(
      period = twea.period.toInput,
      times = twea.times.orIgnore
    )

extension (twe: TimingWindowEnd)
  def toInput: TimingWindowEndInput =
    TimingWindowEndInput(
      atUtc = TimingWindowEnd.at.andThen(TimingWindowEnd.At.instant).getOption(twe).orIgnore,
      after = TimingWindowEnd.after
        .andThen(TimingWindowEnd.After.duration)
        .getOption(twe)
        .map(_.toInput)
        .orIgnore,
      repeat = TimingWindowEnd.after
        .andThen(TimingWindowEnd.After.repeat)
        .getOption(twe)
        .flatten
        .map(_.toInput)
        .orIgnore
    )

extension (tw: TimingWindow)
  def toInput: TimingWindowInput =
    TimingWindowInput(
      inclusion = tw.inclusion,
      startUtc = tw.start,
      end = tw.end.map(_.toInput).orIgnore
    )

extension (ccd: GmosCcdMode)
  def toInput: GmosCcdModeInput =
    GmosCcdModeInput(xBin = ccd.xBin.value.assign,
                     yBin = ccd.yBin.value.assign,
                     ampCount = ccd.ampCount.assign,
                     ampGain = ccd.ampGain.assign,
                     ampReadMode = ccd.ampReadMode.assign
    )

extension (g: GmosGratingConfig.South)
  def toInput: GmosSouthGratingConfigInput =
    GmosSouthGratingConfigInput(
      grating = g.grating,
      order = g.order,
      wavelength = g.wavelength.toInput
    )

extension (g: GmosGratingConfig.North)
  def toInput: GmosNorthGratingConfigInput =
    GmosNorthGratingConfigInput(
      grating = g.grating,
      order = g.order,
      wavelength = g.wavelength.toInput
    )

extension (g: GmosFpuMask.Custom)
  def toInput: GmosCustomMaskInput =
    GmosCustomMaskInput(
      filename = g.filename.value,
      slitWidth = g.slitWidth
    )

extension (customMask: Flamingos2FpuMask.Custom)
  def toInput: Flamingos2CustomMaskInput =
    Flamingos2CustomMaskInput(
      filename = customMask.filename.value,
      slitWidth = customMask.slitWidth
    )

extension (g: GmosFpuMask[GmosSouthFpu])
  def toInput: GmosSouthFpuInput =
    GmosSouthFpuInput(
      customMask = g.custom.map(_.toInput).orUnassign,
      builtin = g.builtinFpu.orUnassign
    )

extension (g: GmosFpuMask[GmosNorthFpu])
  def toInput: GmosNorthFpuInput =
    GmosNorthFpuInput(
      customMask = g.custom.map(_.toInput).orUnassign,
      builtin = g.builtinFpu.orUnassign
    )

extension (mask: Flamingos2FpuMask)
  def toInput: Input[Flamingos2FpuMaskInput] =
    if mask.isImaging then Input.unassign
    else
      Flamingos2FpuMaskInput(
        customMask = mask.custom.map(_.toInput).orUnassign,
        builtin = mask.builtinFpu.orUnassign
      ).assign

extension (ns: GmosNodAndShuffle)
  def toInput: GmosNodAndShuffleInput = GmosNodAndShuffleInput(
    ns.posA.toInput,
    ns.posB.toInput,
    ns.eOffset,
    ns.shuffleOffset,
    ns.shuffleCycles
  )

extension (gmosNStatic: gmos.StaticConfig.GmosNorth)
  def toInput: GmosNorthStaticInput = GmosNorthStaticInput(
    gmosNStatic.stageMode.assign,
    gmosNStatic.detector.assign,
    gmosNStatic.mosPreImaging.assign,
    gmosNStatic.nodAndShuffle.map(_.toInput).orUnassign
  )

extension (gmosSStatic: gmos.StaticConfig.GmosSouth)
  def toInput: GmosSouthStaticInput = GmosSouthStaticInput(
    gmosSStatic.stageMode.assign,
    gmosSStatic.detector.assign,
    gmosSStatic.mosPreImaging.assign,
    gmosSStatic.nodAndShuffle.map(_.toInput).orUnassign
  )

extension (flamingos2Static: Flamingos2StaticConfig)
  def toInput: Flamingos2StaticInput = Flamingos2StaticInput(
    flamingos2Static.mosPreImaging.assign,
    flamingos2Static.useElectronicOffsetting.assign
  )

extension (gmosSDynamic: gmos.DynamicConfig.GmosSouth)
  def toInput: GmosSouthDynamicInput = GmosSouthDynamicInput(
    gmosSDynamic.exposure.toInput,
    gmosSDynamic.readout.toInput,
    gmosSDynamic.dtax,
    gmosSDynamic.roi,
    gmosSDynamic.gratingConfig.map(_.toInput).orUnassign,
    gmosSDynamic.filter.orUnassign,
    gmosSDynamic.fpu.map(_.toInput).orUnassign
  )

extension (gmosNDynamic: gmos.DynamicConfig.GmosNorth)
  def toInput: GmosNorthDynamicInput = GmosNorthDynamicInput(
    gmosNDynamic.exposure.toInput,
    gmosNDynamic.readout.toInput,
    gmosNDynamic.dtax,
    gmosNDynamic.roi,
    gmosNDynamic.gratingConfig.map(_.toInput).orUnassign,
    gmosNDynamic.filter.orUnassign,
    gmosNDynamic.fpu.map(_.toInput).orUnassign
  )

extension (f2Dynamic: Flamingos2DynamicConfig)
  def toInput: Flamingos2DynamicInput = Flamingos2DynamicInput(
    f2Dynamic.exposure.toInput,
    f2Dynamic.disperser.orUnassign,
    f2Dynamic.filter,
    f2Dynamic.readMode,
    f2Dynamic.lyotWheel,
    f2Dynamic.fpu.toInput,
    f2Dynamic.decker,
    f2Dynamic.readoutMode,
    f2Dynamic.reads
  )

extension (sc: StepConfig)
  def toInput: StepConfigInput = sc match
    case StepConfig.Bias =>
      StepConfigInput(bias = true.assign)

    case StepConfig.Dark =>
      StepConfigInput(dark = true.assign)

    case StepConfig.Gcal(lamp, filter, diffuser, shutter) =>
      val gcal = StepConfigGcalInput(
        arcs = lamp.arcs.map(_.toNonEmptyList.toList).orIgnore,
        continuum = lamp.continuum.orIgnore,
        filter = filter,
        diffuser = diffuser,
        shutter = shutter
      )
      StepConfigInput(gcal = gcal.assign)

    case StepConfig.Science =>
      StepConfigInput(science = true.assign)

    case StepConfig.SmartGcal(smartGcalType) =>
      val smartGcal = StepConfigSmartGcalInput(smartGcalType = smartGcalType)
      StepConfigInput(smartGcal = smartGcal.assign)

extension (tc: TelescopeConfig)
  def toInput: TelescopeConfigInput =
    TelescopeConfigInput(offset = tc.offset.toInput.assign, guiding = tc.guiding.assign)
