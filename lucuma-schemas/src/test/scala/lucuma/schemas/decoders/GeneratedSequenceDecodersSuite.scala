// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order.given
import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.data.Zipper
import lucuma.core.enums.*
import lucuma.core.math.Angle
import lucuma.core.math.Offset
import lucuma.core.math.Wavelength
import lucuma.core.model.sequence.StepConfig.Gcal
import lucuma.core.model.sequence.*
import lucuma.core.model.sequence.gmos.*
import lucuma.core.syntax.timespan.*
import lucuma.core.util.TimeSpan
import lucuma.odb.json.plannedtime
import lucuma.odb.json.sequence.given

import java.util.UUID
import scala.collection.immutable.SortedSet

class GeneratedSequenceDecodersSuite extends InputStreamSuite {
  test("Generated sequence decoder") {
    val expected: InstrumentExecutionConfig.GmosNorth =
      InstrumentExecutionConfig.GmosNorth(
        ExecutionConfig(
          static = StaticConfig.GmosNorth(
            GmosNorthStageMode.FollowXy,
            GmosNorthDetector.Hamamatsu,
            MosPreImaging.IsNotMosPreImaging,
            none
          ),
          acquisition = ExecutionSequence(
            digest = SequenceDigest(
              observeClass = ObserveClass.Acquisition,
              plannedTime = PlannedTime(
                ChargeClass.NonCharged -> TimeSpan.Zero,
                ChargeClass.Partner    -> TimeSpan.Zero,
                ChargeClass.Program    -> 185162500.microsecondTimeSpan
              ),
              offsets = SortedSet(
                Offset.Zero,
                Offset(Offset.P(Angle.fromMicroarcseconds(10000000)), Offset.Q.Zero)
              )
            ),
            nextAtom = Atom(
              id = Atom.Id.fromUuid(UUID.fromString("42c0507a-fd17-313a-a105-733181208926")),
              description = NonEmptyString.from("Acquisition - Initial").toOption,
              steps = NonEmptyList.of(
                Step(
                  id = Step.Id.fromUuid(UUID.fromString("367730f9-29a6-3acc-885e-8cfdbf7d8fdf")),
                  instrumentConfig = DynamicConfig.GmosNorth(
                    exposure = 10.secTimeSpan,
                    readout = GmosCcdMode(
                      xBin = GmosXBinning.Two,
                      yBin = GmosYBinning.Two,
                      ampCount = GmosAmpCount.Twelve,
                      ampGain = GmosAmpGain.Low,
                      ampReadMode = GmosAmpReadMode.Fast
                    ),
                    dtax = GmosDtax.Zero,
                    roi = GmosRoi.Ccd2,
                    gratingConfig = none,
                    filter = GmosNorthFilter.RPrime.some,
                    fpu = none
                  ),
                  stepConfig = StepConfig.Science(Offset.Zero, GuideState.Enabled),
                  estimate = StepEstimate(
                    configChange = none,
                    detector = Zipper
                      .of(
                        DetectorEstimate(
                          name = "GMOS North",
                          description = "GMOS North Hamamatsu Detector Array",
                          dataset = DatasetEstimate(
                            exposure = 10.secTimeSpan,
                            readout = 9700.msTimeSpan,
                            write = 10.secTimeSpan
                          ),
                          count = NonNegInt.unsafeFrom(1)
                        )
                      )
                      .focusIndex(0)
                  ),
                  observeClass = ObserveClass.Acquisition,
                  breakpoint = Breakpoint.Disabled
                ),
                Step(
                  id = Step.Id.fromUuid(UUID.fromString("5f5d696b-63f5-3411-8a69-ee02f3d16bc6")),
                  instrumentConfig = DynamicConfig.GmosNorth(
                    exposure = 20.secTimeSpan,
                    readout = GmosCcdMode(
                      xBin = GmosXBinning.One,
                      yBin = GmosYBinning.One,
                      ampCount = GmosAmpCount.Twelve,
                      ampGain = GmosAmpGain.Low,
                      ampReadMode = GmosAmpReadMode.Fast
                    ),
                    dtax = GmosDtax.Zero,
                    roi = GmosRoi.CentralStamp,
                    gratingConfig = none,
                    filter = GmosNorthFilter.RPrime.some,
                    fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_0_25).some
                  ),
                  stepConfig = StepConfig.Science(
                    Offset(Offset.P(Angle.fromMicroarcseconds(10000000)), Offset.Q.Zero),
                    GuideState.Enabled
                  ),
                  estimate = StepEstimate(
                    configChange = Zipper
                      .of(
                        ConfigChangeEstimate(
                          name = "GMOS North FPU",
                          description = "GMOS North FPU change cost",
                          estimate = 60.secTimeSpan
                        ),
                        ConfigChangeEstimate(
                          name = "Offset",
                          description = "Offset cost, 7 (constant) + 0.0625 (distance)",
                          estimate = 7062500.microsecondTimeSpan
                        )
                      )
                      .focusIndex(0),
                    detector = Zipper
                      .of(
                        DetectorEstimate(
                          name = "GMOS North",
                          description = "GMOS North Hamamatsu Detector Array",
                          dataset = DatasetEstimate(
                            exposure = 20.secTimeSpan,
                            readout = 4200.millisecondTimeSpan,
                            write = 10.secTimeSpan
                          ),
                          count = NonNegInt.unsafeFrom(1)
                        )
                      )
                      .focusIndex(0)
                  ),
                  observeClass = ObserveClass.Acquisition,
                  breakpoint = Breakpoint.Disabled
                ),
                Step(
                  id = Step.Id.fromUuid(UUID.fromString("b6623be3-b10f-38dd-8082-79d2f07b0765")),
                  instrumentConfig = DynamicConfig.GmosNorth(
                    exposure = 40.secTimeSpan,
                    readout = GmosCcdMode(
                      xBin = GmosXBinning.One,
                      yBin = GmosYBinning.One,
                      ampCount = GmosAmpCount.Twelve,
                      ampGain = GmosAmpGain.Low,
                      ampReadMode = GmosAmpReadMode.Fast
                    ),
                    dtax = GmosDtax.Zero,
                    roi = GmosRoi.CentralStamp,
                    gratingConfig = none,
                    filter = GmosNorthFilter.RPrime.some,
                    fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_0_25).some
                  ),
                  stepConfig = StepConfig.Science(Offset.Zero, GuideState.Enabled),
                  estimate = StepEstimate(
                    configChange = Zipper
                      .of(
                        ConfigChangeEstimate(
                          name = "Offset",
                          description = "Offset cost, 7 (constant) + 0.0625 (distance)",
                          estimate = 7062500.microsecondTimeSpan
                        )
                      )
                      .focusIndex(0),
                    detector = Zipper
                      .of(
                        DetectorEstimate(
                          name = "GMOS North",
                          description = "GMOS North Hamamatsu Detector Array",
                          dataset = DatasetEstimate(
                            exposure = 40.secTimeSpan,
                            readout = 4200.millisecondTimeSpan,
                            write = 10.secTimeSpan
                          ),
                          count = NonNegInt.unsafeFrom(1)
                        )
                      )
                      .focusIndex(0)
                  ),
                  observeClass = ObserveClass.Acquisition,
                  breakpoint = Breakpoint.Disabled
                )
              )
            ),
            possibleFuture = List.empty,
            hasMore = false,
            atomCount = PosInt.unsafeFrom(1)
          ).some,
          science = ExecutionSequence(
            digest = SequenceDigest(
              observeClass = ObserveClass.Science,
              plannedTime = PlannedTime(
                ChargeClass.NonCharged -> TimeSpan.Zero,
                ChargeClass.Partner    -> 90100.msTimeSpan,
                ChargeClass.Program    -> 52100.msTimeSpan
              ),
              offsets = SortedSet(Offset.Zero)
            ),
            nextAtom = Atom(
              id = Atom.Id.fromUuid(UUID.fromString("88eb8a7f-f0b4-38cd-a85b-c1640b2c4528")),
              description = NonEmptyString.from("q 0.0″, λ 650.0 nm").toOption,
              steps = NonEmptyList.of(
                Step(
                  id = Step.Id.fromUuid(UUID.fromString("db4f32ed-fabd-32b9-88e8-d123ac71e9b3")),
                  instrumentConfig = DynamicConfig.GmosNorth(
                    exposure = 1.secTimeSpan,
                    readout = GmosCcdMode(
                      xBin = GmosXBinning.One,
                      yBin = GmosYBinning.Two,
                      ampCount = GmosAmpCount.Twelve,
                      ampGain = GmosAmpGain.Low,
                      ampReadMode = GmosAmpReadMode.Slow
                    ),
                    dtax = GmosDtax.Zero,
                    roi = GmosRoi.FullFrame,
                    gratingConfig = GmosGratingConfig
                      .North(
                        grating = GmosNorthGrating.B600_G5307,
                        order = GmosGratingOrder.One,
                        wavelength = Wavelength.unsafeFromIntPicometers(650000)
                      )
                      .some,
                    filter = none,
                    fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_0_25).some
                  ),
                  stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((0, 0)),
                                                  GuideState.Enabled
                  ),
                  estimate = StepEstimate(
                    configChange = none,
                    detector = Zipper
                      .of(
                        DetectorEstimate(
                          name = "GMOS North",
                          description = "GMOS North Hamamatsu Detector Array",
                          dataset = DatasetEstimate(
                            exposure = 1.secTimeSpan,
                            readout = 41100.msTimeSpan,
                            write = 10.secTimeSpan
                          ),
                          count = NonNegInt.unsafeFrom(1)
                        )
                      )
                      .focusIndex(0)
                  ),
                  observeClass = ObserveClass.Science,
                  breakpoint = Breakpoint.Disabled
                ),
                Step(
                  id = Step.Id.fromUuid(UUID.fromString("5434235b-4403-321c-8f45-16cd8771ccf7")),
                  instrumentConfig = DynamicConfig.GmosNorth(
                    exposure = 24.secTimeSpan,
                    readout = GmosCcdMode(
                      xBin = GmosXBinning.One,
                      yBin = GmosYBinning.Two,
                      ampCount = GmosAmpCount.Twelve,
                      ampGain = GmosAmpGain.Low,
                      ampReadMode = GmosAmpReadMode.Slow
                    ),
                    dtax = GmosDtax.Zero,
                    roi = GmosRoi.FullFrame,
                    gratingConfig = GmosGratingConfig
                      .North(
                        grating = GmosNorthGrating.B600_G5307,
                        order = GmosGratingOrder.One,
                        wavelength = Wavelength.unsafeFromIntPicometers(650000)
                      )
                      .some,
                    filter = none,
                    fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_0_25).some
                  ),
                  stepConfig = StepConfig.Gcal(
                    lamp = Gcal.Lamp.fromContinuum(GcalContinuum.QuartzHalogen5W),
                    filter = GcalFilter.Gmos,
                    diffuser = GcalDiffuser.Visible,
                    shutter = GcalShutter.Closed
                  ),
                  estimate = StepEstimate(
                    configChange = Zipper
                      .of(
                        ConfigChangeEstimate(
                          name = "Science Fold",
                          description = "Cost for moving the science fold in or out",
                          estimate = 15.secTimeSpan
                        )
                      )
                      .focusIndex(0),
                    detector = Zipper
                      .of(
                        DetectorEstimate(
                          name = "GMOS North",
                          description = "GMOS North Hamamatsu Detector Array",
                          dataset = DatasetEstimate(
                            exposure = 24.secTimeSpan,
                            readout = 41100.msTimeSpan,
                            write = 10.secTimeSpan
                          ),
                          count = NonNegInt.unsafeFrom(1)
                        )
                      )
                      .focusIndex(0)
                  ),
                  observeClass = ObserveClass.PartnerCal,
                  breakpoint = Breakpoint.Disabled
                )
              )
            ),
            possibleFuture = List.empty,
            hasMore = false,
            atomCount = PosInt.unsafeFrom(1)
          ).some,
          setup = SetupTime(
            full = 960.secTimeSpan,
            reacquisition = 300.secTimeSpan
          )
        )
      )

    assertParsedStreamEquals("/generatedSequence1.json", expected)
  }
}
