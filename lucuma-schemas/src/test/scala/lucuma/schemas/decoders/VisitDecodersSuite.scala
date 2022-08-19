// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.effect.IO
import cats.syntax.all.{_, given}
import lucuma.core.enums.GmosAmpCount
import lucuma.core.enums.GmosAmpGain
import lucuma.core.enums.GmosAmpReadMode
import lucuma.core.enums.GmosDtax
import lucuma.core.enums.GmosRoi
import lucuma.core.enums.GmosSouthDetector
import lucuma.core.enums.GmosSouthStageMode
import lucuma.core.enums.GmosXBinning
import lucuma.core.enums.GmosYBinning
import lucuma.core.enums.MosPreImaging
import lucuma.core.enums.SequenceCommand
import lucuma.core.enums.SequenceType
import lucuma.core.enums.StepStage
import lucuma.core.math.Offset
import lucuma.core.model.ExecutionEvent
import lucuma.core.model.NonNegDuration
import lucuma.core.model.sequence.DynamicConfig
import lucuma.core.model.sequence.GmosCcdMode
import lucuma.core.model.sequence.StaticConfig
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import lucuma.refined.*
import lucuma.refined.*
import lucuma.schemas.model.SequenceEvent
import lucuma.schemas.model.StepEvent
import lucuma.schemas.model.StepRecord
import lucuma.schemas.model.Visit

import java.time.Duration
import java.time.Instant
import java.util.UUID

class VisitDecodersSuite extends InputStreamSuite {
  val expectedVisits: List[Visit] = List(
    Visit.GmosSouth(
      id = Visit.Id.fromUuid(UUID.fromString("7d093b73-3ac7-4886-bb34-0005bcb53ba4")),
      created = Instant.parse("2022-08-22T18:18:46.236929950Z"),
      startTime = Instant.parse("2022-08-22T18:18:56.336Z").some,
      endTime = Instant.parse("2022-08-22T18:26:36.809Z").some,
      duration = NonNegDuration.unsafeFrom(Duration.ofNanos(460473000000L)).some,
      staticConfig = StaticConfig.GmosSouth(
        GmosSouthStageMode.FollowXy,
        GmosSouthDetector.Hamamatsu,
        MosPreImaging.IsNotMosPreImaging,
        none
      ),
      steps = List(
        StepRecord.GmosSouth(
          id = Step.Id.fromUuid(UUID.fromString("f1214814-93c8-4cdb-848b-8a69e61fc754")),
          created = Instant.parse("2022-08-22T18:19:03.230191206Z"),
          startTime = Instant.parse("2022-08-22T18:26:42.092Z").some,
          endTime = Instant.parse("2022-08-22T18:26:36.809Z").some,
          duration = NonNegDuration.zero.some,
          instrumentConfig = DynamicConfig.GmosSouth(
            exposure = Duration.ofNanos(120000000000L),
            readout = GmosCcdMode(
              xBin = GmosXBinning.One,
              yBin = GmosYBinning.One,
              ampCount = GmosAmpCount.Three,
              ampGain = GmosAmpGain.Low,
              ampReadMode = GmosAmpReadMode.Slow
            ),
            dtax = GmosDtax.Zero,
            roi = GmosRoi.FullFrame,
            gratingConfig = none,
            filter = none,
            fpu = none
          ),
          stepConfig = StepConfig.Science(Offset(Offset.P.Zero, Offset.Q.Zero)),
          stepEvents = List(
            StepEvent(
              id = ExecutionEvent.Id(5.refined),
              received = Instant.parse("2022-08-22T18:26:42.092Z"),
              sequenceType = SequenceType.Science,
              stepStage = StepStage.EndStep
            ),
            StepEvent(
              id = ExecutionEvent.Id(4.refined),
              received = Instant.parse("2022-08-22T18:26:36.809Z"),
              sequenceType = SequenceType.Science,
              stepStage = StepStage.StartStep
            )
          ),
          stepQaState = none,
          datasetEvents = List.empty,
          datasets = List.empty
        )
      ),
      sequenceEvents = List(
        SequenceEvent(
          id = ExecutionEvent.Id(2.refined),
          received = Instant.parse("2022-08-22T18:18:56.336Z"),
          command = SequenceCommand.Start
        ),
        SequenceEvent(
          id = ExecutionEvent.Id(3.refined),
          received = Instant.parse("2022-08-22T18:20:42.047Z"),
          command = SequenceCommand.Stop
        )
      )
    )
  )

  test("Visits decoder") {
    jsonResult("/v1.json")
      .map(_.hcursor.downField("visits"))
      .map(_.as[List[Visit]])
      .flatMap(IO.fromEither)
      .map(visits => assertEquals(visits, expectedVisits))
  }

}
