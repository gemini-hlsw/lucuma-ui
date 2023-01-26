// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import lucuma.core.enums._
import lucuma.core.math.Offset
import lucuma.core.model.sequence._
import lucuma.core.syntax.timespan._

import java.util.UUID

class GeneratedSequenceDecodersSuite extends InputStreamSuite {
  test("Generated sequence decoder") {
    val expected: FutureExecutionConfig.GmosNorth =
      FutureExecutionConfig.GmosNorth(
        static = StaticConfig.GmosNorth(
          GmosNorthStageMode.FollowXy,
          GmosNorthDetector.Hamamatsu,
          MosPreImaging.IsNotMosPreImaging,
          none
        ),
        acquisition = ExecutionSequence.GmosNorth(
          nextAtom = Atom.GmosNorth(
            id = Atom.Id.fromUuid(UUID.fromString("a0a4771b-9348-4ee8-8ef6-dffbfec35496")),
            steps = List(
              Step.GmosNorth(
                id = Step.Id.fromUuid(UUID.fromString("1ece00d1-03f6-4823-ab74-9069fef089ae")),
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
                  filter = GmosNorthFilter.GPrime.some,
                  fpu = none
                ),
                stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((0, 0))),
                time = StepTime(
                  configChange = 7.secTimeSpan,
                  exposure = 10.secTimeSpan,
                  readout = 71400.msTimeSpan,
                  write = 10.secTimeSpan,
                  total = 98400.msTimeSpan
                ),
                breakpoint = Breakpoint.Disabled
              )
            )
          ),
          possibleFuture = List.empty
        ),
        science = ExecutionSequence.GmosNorth(
          nextAtom = Atom.GmosNorth(
            id = Atom.Id.fromUuid(UUID.fromString("33cbcd88-843c-402b-bb1e-991741cf6f63")),
            steps = List(
              Step.GmosNorth(
                id = Step.Id.fromUuid(UUID.fromString("f3769281-55cd-4d87-bcac-6dc18dbf512a")),
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
                  filter = GmosNorthFilter.GPrime.some,
                  fpu = none
                ),
                stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((0, 0))),
                time = StepTime(
                  configChange = 7.secTimeSpan,
                  exposure = 10.secTimeSpan,
                  readout = 71400.msTimeSpan,
                  write = 10.secTimeSpan,
                  total = 98400.msTimeSpan
                ),
                breakpoint = Breakpoint.Disabled
              ),
              Step.GmosNorth(
                id = Step.Id.fromUuid(UUID.fromString("6bf2856b-abeb-417d-aff8-2125c99f25fe")),
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
                  filter = GmosNorthFilter.GPrime.some,
                  fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_1_00).some
                ),
                stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((10000000, 0))),
                time = StepTime(
                  configChange = 7.secTimeSpan,
                  exposure = 20.secTimeSpan,
                  readout = 71400.msTimeSpan,
                  write = 10.secTimeSpan,
                  total = 108400.msTimeSpan
                ),
                breakpoint = Breakpoint.Disabled
              ),
              Step.GmosNorth(
                id = Step.Id.fromUuid(UUID.fromString("f7442252-6eac-4c6e-bfea-ee4ab824fd78")),
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
                  filter = GmosNorthFilter.GPrime.some,
                  fpu = GmosFpuMask.Builtin(GmosNorthFpu.LongSlit_1_00).some
                ),
                stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((0, 0))),
                time = StepTime(
                  configChange = 7.secTimeSpan,
                  exposure = 40.secTimeSpan,
                  readout = 71400.msTimeSpan,
                  write = 10.secTimeSpan,
                  total = 128400.msTimeSpan
                ),
                breakpoint = Breakpoint.Disabled
              )
            )
          ),
          possibleFuture = List.empty
        )
      )

    assertParsedStreamEquals("/generatedSequence1.json", expected)
  }
}
