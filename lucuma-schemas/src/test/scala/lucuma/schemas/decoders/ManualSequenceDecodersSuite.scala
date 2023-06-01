// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

// package lucuma.schemas.decoders

// import cats.syntax.all.*
// import eu.timepit.refined.numeric.Positive
// import lucuma.core.enums.*
// import lucuma.core.math.Offset
// import lucuma.core.math.Wavelength
// import lucuma.core.model.sequence.*
// import lucuma.core.syntax.timespan._
// import lucuma.refined.*

// import java.util.UUID

// class ManualSequenceDecodersSuite extends InputStreamSuite {
//   test("Manual sequence decoder") {
//     val expected: ManualConfig =
//       ManualConfig.GmosSouth(
//         static = StaticConfig.GmosSouth(
//           GmosSouthStageMode.FollowXy,
//           GmosSouthDetector.Hamamatsu,
//           MosPreImaging.IsNotMosPreImaging,
//           none
//         ),
//         acquisition = List(
//           Atom.GmosSouth(
//             id = Atom.Id.fromUuid(UUID.fromString("76601293-283f-42c2-bd92-04662dac99b7")),
//             steps = List(
//               Step.GmosSouth(
//                 id = Step.Id.fromUuid(UUID.fromString("fd1419b1-2de4-4718-b508-a42923e4b696")),
//                 instrumentConfig = DynamicConfig.GmosSouth(
//                   exposure = 10.secTimeSpan,
//                   readout = GmosCcdMode(
//                     xBin = GmosXBinning.Two,
//                     yBin = GmosYBinning.Two,
//                     ampCount = GmosAmpCount.Twelve,
//                     ampGain = GmosAmpGain.Low,
//                     ampReadMode = GmosAmpReadMode.Fast
//                   ),
//                   dtax = GmosDtax.Zero,
//                   roi = GmosRoi.Ccd2,
//                   gratingConfig = GmosGratingConfig
//                     .South(
//                       grating = GmosSouthGrating.R600_G5324,
//                       order = GmosGratingOrder.Zero,
//                       wavelength = Wavelength(520000.refined[Positive])
//                     )
//                     .some,
//                   filter = GmosSouthFilter.RPrime.some,
//                   fpu = none
//                 ),
//                 stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((0, 0))),
//                 time = StepTime(
//                   configChange = 7.secTimeSpan,
//                   exposure = 10.secTimeSpan,
//                   readout = 71400.msTimeSpan,
//                   write = 10.secTimeSpan,
//                   total = 98400.msTimeSpan
//                 ),
//                 breakpoint = Breakpoint.Disabled
//               )
//             )
//           ),
//           Atom.GmosSouth(
//             id = Atom.Id.fromUuid(UUID.fromString("3d18caeb-142d-4bd5-98f7-9a330ddd6f4b")),
//             steps = List(
//               Step.GmosSouth(
//                 id = Step.Id.fromUuid(UUID.fromString("a190bf63-493f-4689-886d-effb29983de7")),
//                 instrumentConfig = DynamicConfig.GmosSouth(
//                   exposure = 20.secTimeSpan,
//                   readout = GmosCcdMode(
//                     xBin = GmosXBinning.One,
//                     yBin = GmosYBinning.One,
//                     ampCount = GmosAmpCount.Twelve,
//                     ampGain = GmosAmpGain.Low,
//                     ampReadMode = GmosAmpReadMode.Fast
//                   ),
//                   dtax = GmosDtax.Zero,
//                   roi = GmosRoi.CentralStamp,
//                   gratingConfig = none,
//                   filter = GmosSouthFilter.RPrime.some,
//                   fpu = GmosFpuMask.Builtin(GmosSouthFpu.LongSlit_1_00).some
//                 ),
//                 stepConfig = StepConfig.Science(Offset.microarcseconds.reverseGet((10000000, 0))),
//                 time = StepTime(
//                   configChange = 7.secTimeSpan,
//                   exposure = 20.secTimeSpan,
//                   readout = 71400.msTimeSpan,
//                   write = 10.secTimeSpan,
//                   total = 108400.msTimeSpan
//                 ),
//                 breakpoint = Breakpoint.Disabled
//               )
//             )
//           )
//         ),
//         science = List.empty,
//         setupTime = 1080.secTimeSpan
//       )

//     assertParsedStreamEquals("/manualSequence1.json", expected)
//   }
// }
