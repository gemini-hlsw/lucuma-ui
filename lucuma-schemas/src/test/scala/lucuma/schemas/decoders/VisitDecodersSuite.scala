// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.effect.IO
import cats.syntax.all.*
import lucuma.core.enums.GmosAmpCount
import lucuma.core.enums.GmosAmpGain
import lucuma.core.enums.GmosAmpReadMode
import lucuma.core.enums.GmosDtax
import lucuma.core.enums.GmosNorthDetector
import lucuma.core.enums.GmosNorthFilter
import lucuma.core.enums.GmosNorthStageMode
import lucuma.core.enums.GmosRoi
import lucuma.core.enums.GmosSouthDetector
import lucuma.core.enums.GmosSouthFilter
import lucuma.core.enums.GmosSouthStageMode
import lucuma.core.enums.GmosXBinning
import lucuma.core.enums.GmosYBinning
import lucuma.core.enums.GuideState
import lucuma.core.enums.MosPreImaging
import lucuma.core.enums.ObserveClass
import lucuma.core.enums.SequenceType
import lucuma.core.math.Offset
import lucuma.core.model.sequence.*
import lucuma.core.model.sequence.gmos.*
import lucuma.core.syntax.timespan.*
import lucuma.core.util.TimeSpan
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.refined.*
import lucuma.schemas.model.AtomRecord
import lucuma.schemas.model.ExecutionVisits
import lucuma.schemas.model.StepRecord
import lucuma.schemas.model.Visit

import java.time.LocalDateTime
import java.util.UUID

class VisitDecodersSuite extends InputStreamSuite {
  val expectedVisitsGmosNorth: ExecutionVisits = ExecutionVisits.GmosNorth(
    StaticConfig.GmosNorth(
      stageMode = GmosNorthStageMode.FollowXy,
      detector = GmosNorthDetector.Hamamatsu,
      mosPreImaging = MosPreImaging.IsNotMosPreImaging,
      nodAndShuffle = none
    ),
    List(
      Visit.GmosNorth(
        id = Visit.Id(457L.refined),
        created =
          Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2024, 2, 12, 17, 22, 6, 372335000)),
        interval = TimestampInterval
          .between(
            Timestamp.unsafeFromLocalDateTime(
              LocalDateTime.of(2024, 2, 12, 17, 22, 6, 937281000)
            ),
            Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000))
          )
          .some,
        atoms = List(
          AtomRecord.GmosNorth(
            id = Atom.Id.fromUuid(UUID.fromString("03e40772-09c1-443d-b4c8-b952995ad109")),
            created = Timestamp.unsafeFromLocalDateTime(
              LocalDateTime.of(2024, 2, 12, 17, 22, 6, 673584000)
            ),
            interval = TimestampInterval
              .between(
                Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                ),
                Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                )
              )
              .some,
            sequenceType = SequenceType.Acquisition,
            steps = List(
              StepRecord.GmosNorth(
                id = Step.Id.fromUuid(UUID.fromString("7adfa674-3753-4158-8dd8-cd08eddbb802")),
                created = Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 490332000)
                ),
                interval = TimestampInterval
                  .between(
                    Timestamp.unsafeFromLocalDateTime(
                      LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                    ),
                    Timestamp.unsafeFromLocalDateTime(
                      LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                    )
                  )
                  .some,
                instrumentConfig = DynamicConfig.GmosNorth(
                  exposure = 1000000.µsTimeSpan,
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
                  filter = Some(GmosNorthFilter.RPrime),
                  fpu = none
                ),
                stepConfig = StepConfig.Science(
                  offset = Offset(Offset.P.Zero, Offset.Q.Zero),
                  guiding = GuideState.Enabled
                ),
                observeClass = ObserveClass.Science,
                qaState = none,
                datasets = Nil
              )
            )
          )
        )
      )
    )
  )

  val expectedVisitsGmosSouth: ExecutionVisits = ExecutionVisits.GmosSouth(
    StaticConfig.GmosSouth(
      stageMode = GmosSouthStageMode.FollowXy,
      detector = GmosSouthDetector.Hamamatsu,
      mosPreImaging = MosPreImaging.IsNotMosPreImaging,
      nodAndShuffle = none
    ),
    List(
      Visit.GmosSouth(
        id = Visit.Id(457L.refined),
        created =
          Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2024, 2, 12, 17, 22, 6, 372335000)),
        interval = TimestampInterval
          .between(
            Timestamp.unsafeFromLocalDateTime(
              LocalDateTime.of(2024, 2, 12, 17, 22, 6, 937281000)
            ),
            Timestamp.unsafeFromLocalDateTime(LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000))
          )
          .some,
        atoms = List(
          AtomRecord.GmosSouth(
            id = Atom.Id.fromUuid(UUID.fromString("03e40772-09c1-443d-b4c8-b952995ad109")),
            created = Timestamp.unsafeFromLocalDateTime(
              LocalDateTime.of(2024, 2, 12, 17, 22, 6, 673584000)
            ),
            interval = TimestampInterval
              .between(
                Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                ),
                Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                )
              )
              .some,
            sequenceType = SequenceType.Acquisition,
            steps = List(
              StepRecord.GmosSouth(
                id = Step.Id.fromUuid(UUID.fromString("7adfa674-3753-4158-8dd8-cd08eddbb802")),
                created = Timestamp.unsafeFromLocalDateTime(
                  LocalDateTime.of(2024, 2, 12, 17, 22, 7, 490332000)
                ),
                interval = TimestampInterval
                  .between(
                    Timestamp.unsafeFromLocalDateTime(
                      LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                    ),
                    Timestamp.unsafeFromLocalDateTime(
                      LocalDateTime.of(2024, 2, 12, 17, 22, 7, 761573000)
                    )
                  )
                  .some,
                instrumentConfig = DynamicConfig.GmosSouth(
                  exposure = 1000000.µsTimeSpan,
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
                  filter = Some(GmosSouthFilter.RPrime),
                  fpu = none
                ),
                stepConfig = StepConfig.Science(
                  offset = Offset(Offset.P.Zero, Offset.Q.Zero),
                  guiding = GuideState.Enabled
                ),
                observeClass = ObserveClass.Science,
                qaState = none,
                datasets = Nil
              )
            )
          )
        )
      )
    )
  )

  test("Visits Gmos North decoder") {
    jsonResult("/visitGmosNorth.json")
      .map(_.hcursor.downField("observation").downField("execution"))
      .map(_.as[Option[ExecutionVisits]])
      .flatMap(IO.fromEither)
      .map(visits => assertEquals(visits, Some(expectedVisitsGmosNorth)))
  }

  test("Visits Gmos South decoder") {
    jsonResult("/visitGmosSouth.json")
      .map(_.hcursor.downField("observation").downField("execution"))
      .map(_.as[Option[ExecutionVisits]])
      .flatMap(IO.fromEither)
      .map(visits => assertEquals(visits, Some(expectedVisitsGmosSouth)))
  }

  test("Visits no sequence decoder") {
    jsonResult("/visitNoSequence.json")
      .map(_.hcursor.downField("observation").downField("execution"))
      .map(_.as[Option[ExecutionVisits]])
      .flatMap(IO.fromEither)
      .map(visits => assertEquals(visits, None))
  }
}
