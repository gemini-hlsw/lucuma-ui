// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb.input

import clue.data.Input
import clue.data.syntax.*
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.types.numeric.PosBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.syntax.*
import lucuma.core.enums.Band
import lucuma.core.enums.GmosNorthFpu
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.math.*
import lucuma.core.math.BrightnessUnits.*
import lucuma.core.math.dimensional.*
import lucuma.core.model.*
import lucuma.core.model.ExposureTimeMode.*
import lucuma.core.model.ProposalClass.*
import lucuma.core.model.sequence.StepConfig
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.GmosCcdMode
import lucuma.core.model.sequence.gmos.GmosFpuMask
import lucuma.core.model.sequence.gmos.GmosGratingConfig
import lucuma.core.model.sequence.gmos.GmosNodAndShuffle
import lucuma.core.model.sequence.gmos.StaticConfig
import lucuma.core.util.*
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

extension (id: Target.Id)
  def toWhereTarget: WhereTarget =
    WhereTarget(id = WhereOrderTargetId(EQ = id.assign).assign)

extension (ids: List[Target.Id])
  def toWhereTargets: WhereTarget =
    WhereTarget(OR = ids.map(_.toWhereTarget).assign)

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
    RightAscensionInput(microarcseconds = ra.toAngle.toMicroarcseconds.assign)

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
        UnnormalizedSedInput(fluxDensities = fluxDensities.toSortedMap.toList.map {
          case (wavelength, value) => FluxDensity(wavelength.toInput, value)
        }.assign)
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
    value = PosBigDecimal.unsafeFrom(fdc.value.value.value),
    units = Measure.unitsTagged.get(fdc)
  )

extension (fdc: FluxDensityContinuumMeasure[Surface])
  def toInput: FluxDensityContinuumSurfaceInput = FluxDensityContinuumSurfaceInput(
    value = PosBigDecimal.unsafeFrom(fdc.value.value.value),
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
    case FixedExposureMode(count, time) =>
      ExposureTimeModeInput(fixedExposure =
        FixedExposureModeInput(
          count = NonNegInt.unsafeFrom(count.value),
          time = time.toInput
        ).assign
      )
    case SignalToNoiseMode(value)       =>
      ExposureTimeModeInput(signalToNoise = SignalToNoiseModeInput(value = value).assign)

extension (p: ProposalClass)
  def toInput: ProposalClassInput = p match
    case DemoScience(minPercentTime)                                  =>
      ProposalClassInput(demoScience =
        DemoScienceInput(minPercentTime = minPercentTime.assign).assign
      )
    case Exchange(minPercentTime)                                     =>
      ProposalClassInput(exchange = ExchangeInput(minPercentTime = minPercentTime.assign).assign)
    case LargeProgram(minPercentTime, minPercentTotalTime, totalTime) =>
      ProposalClassInput(largeProgram =
        LargeProgramInput(
          minPercentTime = minPercentTime.assign,
          minPercentTotalTime = minPercentTotalTime.assign,
          totalTime = totalTime.toInput.assign
        ).assign
      )
    case Queue(minPercentTime)                                        =>
      ProposalClassInput(queue = QueueInput(minPercentTime = minPercentTime.assign).assign)
    case FastTurnaround(minPercentTime)                               =>
      ProposalClassInput(fastTurnaround =
        FastTurnaroundInput(minPercentTime = minPercentTime.assign).assign
      )
    case DirectorsTime(minPercentTime)                                =>
      ProposalClassInput(directorsTime =
        DirectorsTimeInput(minPercentTime = minPercentTime.assign).assign
      )
    case Intensive(minPercentTime, minPercentTotalTime, totalTime)    =>
      ProposalClassInput(intensive =
        IntensiveInput(
          minPercentTime = minPercentTime.assign,
          minPercentTotalTime = minPercentTotalTime.assign,
          totalTime = totalTime.toInput.assign
        ).assign
      )
    case SystemVerification(minPercentTime)                           =>
      ProposalClassInput(systemVerification =
        SystemVerificationInput(minPercentTime = minPercentTime.assign).assign
      )
    case Classical(minPercentTime)                                    =>
      ProposalClassInput(classical = ClassicalInput(minPercentTime = minPercentTime.assign).assign)
    case PoorWeather(minPercentTime)                                  =>
      ProposalClassInput(poorWeather =
        PoorWeatherInput(minPercentTime = minPercentTime.assign).assign
      )

extension (proposal: Proposal)
  def toInput: ProposalInput = ProposalInput(
    title = proposal.title.orUnassign,
    proposalClass = proposal.proposalClass.toInput.assign,
    category = proposal.category.orUnassign,
    toOActivation = proposal.toOActivation.assign,
    `abstract` = proposal.abstrakt.orUnassign,
    // Temporary fix until the changes in this issue are complete: https://github.com/gemini-hlsw/lucuma-odb/issues/253
    // partnerSplits = proposal.partnerSplits.toList.map { case (par, pct) =>
    //   PartnerSplitInput(par, pct)
    partnerSplits =
      if (proposal.partnerSplits.isEmpty) Input.unassign
      else
        proposal.partnerSplits.toList.map { case (par, pct) =>
          PartnerSplitInput(par, pct)
        }.assign
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
    explicitXBin = o.explicitXBin.orUnassign,
    explicitYBin = o.explicitYBin.orUnassign,
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
    explicitXBin = o.explicitXBin.orUnassign,
    explicitYBin = o.explicitYBin.orUnassign,
    explicitAmpReadMode = o.explicitAmpReadMode.orUnassign,
    explicitAmpGain = o.explicitAmpGain.orUnassign,
    explicitRoi = o.explicitRoi.orUnassign,
    explicitWavelengthDithers = o.explicitWavelengthDithers.map(_.toList.map(_.toInput)).orUnassign,
    explicitSpatialOffsets = o.explicitSpatialOffsets.map(_.toList.map(_.toInput)).orUnassign
  )

extension (b: ObservingMode)
  def toInput: ObservingModeInput = b match
    case o: ObservingMode.GmosNorthLongSlit =>
      ObservingModeInput(gmosNorthLongSlit = o.toInput.assign)
    case o: ObservingMode.GmosSouthLongSlit =>
      ObservingModeInput(gmosSouthLongSlit = o.toInput.assign)

extension (i: BasicConfiguration)
  def toInput: ObservingModeInput = i match
    case o: BasicConfiguration.GmosNorthLongSlit =>
      ObservingModeInput(
        gmosNorthLongSlit = GmosNorthLongSlitInput(
          grating = o.grating.assign,
          filter = o.filter.orUnassign,
          fpu = o.fpu.assign,
          centralWavelength = o.centralWavelength.value.toInput.assign
        ).assign
      )
    case o: BasicConfiguration.GmosSouthLongSlit =>
      ObservingModeInput(
        gmosSouthLongSlit = GmosSouthLongSlitInput(
          grating = o.grating.assign,
          filter = o.filter.orUnassign,
          fpu = o.fpu.assign,
          centralWavelength = o.centralWavelength.value.toInput.assign
        ).assign
      )

extension (er: ElevationRange)
  def toInput: ElevationRangeInput =
    er match
      case ElevationRange.AirMass(min, max)   =>
        ElevationRangeInput(airMass =
          // These are actually safe, because min and max in the model are refined [1.0 - 3.0]
          AirMassRangeInput(
            min = PosBigDecimal.unsafeFrom(min.value).assign,
            max = PosBigDecimal.unsafeFrom(max.value).assign
          ).assign
        )
      case ElevationRange.HourAngle(min, max) =>
        ElevationRangeInput(hourAngle =
          HourAngleRangeInput(minHours = min.value.assign, maxHours = max.value.assign).assign
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
    GmosCcdModeInput(xBin = ccd.xBin.assign,
                     yBin = ccd.yBin.assign,
                     ampCount = ccd.ampCount.assign,
                     ampGain = ccd.ampGain.assign,
                     ampReadMode = ccd.ampReadMode.assign
    )

extension (g: GmosGratingConfig.South)
  def toInput: GmosSouthGratingConfigInput =
    GmosSouthGratingConfigInput(grating = g.grating,
                                order = g.order,
                                wavelength = g.wavelength.toInput
    )

extension (g: GmosGratingConfig.North)
  def toInput: GmosNorthGratingConfigInput =
    GmosNorthGratingConfigInput(grating = g.grating,
                                order = g.order,
                                wavelength = g.wavelength.toInput
    )

extension (g: GmosFpuMask.Custom)
  def toInput: GmosCustomMaskInput =
    GmosCustomMaskInput(filename = g.filename.value, slitWidth = g.slitWidth)

extension (g: GmosFpuMask[GmosSouthFpu])
  def toInput: GmosSouthFpuInput =
    GmosSouthFpuInput(customMask = g.custom.map(_.toInput).orUnassign,
                      builtin = g.builtinFpu.orUnassign
    )

extension (g: GmosFpuMask[GmosNorthFpu])
  def toInput: GmosNorthFpuInput =
    GmosNorthFpuInput(customMask = g.custom.map(_.toInput).orUnassign,
                      builtin = g.builtinFpu.orUnassign
    )
extension (ns: GmosNodAndShuffle)
  def toInput: GmosNodAndShuffleInput = GmosNodAndShuffleInput(
    ns.posA.toInput,
    ns.posB.toInput,
    ns.eOffset,
    ns.shuffleOffset,
    ns.shuffleCycles
  )

extension (gmosNStatic: StaticConfig.GmosNorth)
  def toInput: GmosNorthStaticInput = GmosNorthStaticInput(
    gmosNStatic.stageMode.assign,
    gmosNStatic.detector.assign,
    gmosNStatic.mosPreImaging.assign,
    gmosNStatic.nodAndShuffle.map(_.toInput).orUnassign
  )

extension (gmosSStatic: StaticConfig.GmosSouth)
  def toInput: GmosSouthStaticInput = GmosSouthStaticInput(
    gmosSStatic.stageMode.assign,
    gmosSStatic.detector.assign,
    gmosSStatic.mosPreImaging.assign,
    gmosSStatic.nodAndShuffle.map(_.toInput).orUnassign
  )

extension (gmosSDynamic: DynamicConfig.GmosSouth)
  def toInput: GmosSouthDynamicInput = GmosSouthDynamicInput(
    gmosSDynamic.exposure.toInput,
    gmosSDynamic.readout.toInput,
    gmosSDynamic.dtax,
    gmosSDynamic.roi,
    gmosSDynamic.gratingConfig.map(_.toInput).orUnassign,
    gmosSDynamic.filter.orUnassign,
    gmosSDynamic.fpu.map(_.toInput).orUnassign
  )

extension (gmosNDynamic: DynamicConfig.GmosNorth)
  def toInput: GmosNorthDynamicInput = GmosNorthDynamicInput(
    gmosNDynamic.exposure.toInput,
    gmosNDynamic.readout.toInput,
    gmosNDynamic.dtax,
    gmosNDynamic.roi,
    gmosNDynamic.gratingConfig.map(_.toInput).orUnassign,
    gmosNDynamic.filter.orUnassign,
    gmosNDynamic.fpu.map(_.toInput).orUnassign
  )

extension (sc: StepConfig)
  def toInput: StepConfigInput = sc match {
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

    case StepConfig.Science(offset, guiding) =>
      val science = StepConfigScienceInput(
        offset = offset.toInput,
        guiding = guiding.assign
      )
      StepConfigInput(science = science.assign)

    case StepConfig.SmartGcal(smartGcalType) =>
      val smartGcal = StepConfigSmartGcalInput(
        smartGcalType = smartGcalType
      )
      StepConfigInput(smartGcal = smartGcal.assign)
  }
