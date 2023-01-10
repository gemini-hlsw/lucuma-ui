// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.HCursor
import io.circe.generic.semiauto
import io.circe.refined.given
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.DatasetStage
import lucuma.core.enums.Instrument
import lucuma.core.enums.SequenceCommand
import lucuma.core.enums.SequenceType
import lucuma.core.enums.StepQaState
import lucuma.core.enums.StepStage
import lucuma.core.model.ExecutionEvent
import lucuma.core.model.NonNegDuration
import lucuma.core.model.sequence.DynamicConfig
import lucuma.core.model.sequence.StaticConfig
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import lucuma.schemas.model.*

import java.time.Instant

trait VisitDecoders:
  given Decoder[DatasetEvent] = Decoder.instance(c =>
    for
      id           <- c.downField("id").as[ExecutionEvent.Id]
      received     <- c.downField("received").as[Instant]
      index        <- c.downField("datasetId").downField("index").as[PosInt]
      payload      <- c.downField("payload").as[HCursor]
      filename     <- payload.downField("filename").as[NonEmptyString]
      datasetStage <- payload.downField("datasetStage").as[DatasetStage]
    yield DatasetEvent(id, received, index, filename, datasetStage)
  )

  given Decoder[SequenceEvent] = Decoder.instance(c =>
    for
      id       <- c.downField("id").as[ExecutionEvent.Id]
      received <- c.downField("received").as[Instant]
      command  <- c.downField("payload").downField("command").as[SequenceCommand]
    yield SequenceEvent(id, received, command)
  )

  given Decoder[StepEvent] = Decoder.instance(c =>
    for
      id           <- c.downField("id").as[ExecutionEvent.Id]
      received     <- c.downField("received").as[Instant]
      payload      <- c.downField("payload").as[HCursor]
      sequenceType <- payload.downField("sequenceType").as[SequenceType]
      stepStage    <- payload.downField("stepStage").as[StepStage]
    yield StepEvent(id, received, sequenceType, stepStage)
  )

  given Decoder[Dataset] = Decoder.instance(c =>
    for
      index    <- c.downField("id").downField("index").as[PosInt]
      filename <- c.downField("filename").as[NonEmptyString]
      qaState  <- c.downField("qaState").as[Option[DatasetQaState]]
    yield Dataset(index, filename, qaState)
  )

  given Decoder[StepRecord.GmosNorth] = Decoder.instance(c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Instant]
      startTime        <- c.downField("startTime").as[Option[Instant]]
      endTime          <- c.downField("endTime").as[Option[Instant]]
      duration         <- c.downField("duration").as[Option[NonNegDuration]]
      instrumentConfig <- c.downField("instrumentConfig").as[DynamicConfig.GmosNorth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      stepEvents       <- c.downField("stepEvents").as[Option[List[StepEvent]]]
      stepQaState      <- c.downField("stepQaState").as[Option[StepQaState]]
      datasetEvents    <- c.downField("datasetEvents").as[Option[List[DatasetEvent]]]
      datasets         <- c.downField("datasets").as[Option[List[Dataset]]]
    yield StepRecord.GmosNorth(
      id,
      created,
      startTime,
      endTime,
      duration,
      instrumentConfig,
      stepConfig,
      stepEvents.orEmpty,
      stepQaState,
      datasetEvents.orEmpty,
      datasets.orEmpty
    )
  )

  given Decoder[StepRecord.GmosSouth] = Decoder.instance(c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Instant]
      startTime        <- c.downField("startTime").as[Option[Instant]]
      endTime          <- c.downField("endTime").as[Option[Instant]]
      duration         <- c.downField("duration").as[Option[NonNegDuration]]
      instrumentConfig <- c.downField("instrumentConfig").as[DynamicConfig.GmosSouth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      stepEvents       <- c.downField("stepEvents").as[Option[List[StepEvent]]]
      stepQaState      <- c.downField("stepQaState").as[Option[StepQaState]]
      datasetEvents    <- c.downField("datasetEvents").as[Option[List[DatasetEvent]]]
      datasets         <- c.downField("datasets").as[Option[List[Dataset]]]
    yield StepRecord.GmosSouth(
      id,
      created,
      startTime,
      endTime,
      duration,
      instrumentConfig,
      stepConfig,
      stepEvents.orEmpty,
      stepQaState,
      datasetEvents.orEmpty,
      datasets.orEmpty
    )
  )

  // We must specify a name since we have a generated name conflict here.
  // See https://dotty.epfl.ch/docs/reference/contextual/givens.html#anonymous-givens
  given decoderVisitGmosNorth: Decoder[Visit.GmosNorth] = Decoder.instance(c =>
    for
      id             <- c.downField("id").as[Visit.Id]
      created        <- c.downField("created").as[Instant]
      startTime      <- c.downField("startTime").as[Option[Instant]]
      endTime        <- c.downField("endTime").as[Option[Instant]]
      duration       <- c.downField("duration").as[Option[NonNegDuration]]
      staticConfig   <- c.downField("staticN").as[StaticConfig.GmosNorth]
      steps          <- c.downField("stepsN").as[List[StepRecord.GmosNorth]]
      sequenceEvents <- c.downField("sequenceEvents").as[List[SequenceEvent]]
    yield Visit.GmosNorth(
      id,
      created,
      startTime,
      endTime,
      duration,
      staticConfig,
      steps,
      sequenceEvents
    )
  )

  given decoderVisitGmosSouth: Decoder[Visit.GmosSouth] = Decoder.instance(c =>
    for
      id             <- c.downField("id").as[Visit.Id]
      created        <- c.downField("created").as[Instant]
      startTime      <- c.downField("startTime").as[Option[Instant]]
      endTime        <- c.downField("endTime").as[Option[Instant]]
      duration       <- c.downField("duration").as[Option[NonNegDuration]]
      staticConfig   <- c.downField("staticS").as[StaticConfig.GmosSouth]
      steps          <- c.downField("stepsS").as[List[StepRecord.GmosSouth]]
      sequenceEvents <- c.downField("sequenceEvents").as[List[SequenceEvent]]
    yield Visit.GmosSouth(
      id,
      created,
      startTime,
      endTime,
      duration,
      staticConfig,
      steps,
      sequenceEvents
    )
  )

  given Decoder[Visit] =
    List[Decoder[Visit]](
      Decoder[Visit.GmosNorth].widen,
      Decoder[Visit.GmosSouth].widen
    ).reduceLeft(_ or _)

  given decoderExecutionVisitsGmosNorth: Decoder[ExecutionVisits.GmosNorth] = semiauto.deriveDecoder

  given decoderExecutionVisitsGmosSouth: Decoder[ExecutionVisits.GmosSouth] = semiauto.deriveDecoder

  given Decoder[ExecutionVisits] = Decoder.instance(c =>
    c.downField("instrument").as[Instrument].flatMap {
      case Instrument.GmosNorth => c.as[ExecutionVisits.GmosNorth]
      case Instrument.GmosSouth => c.as[ExecutionVisits.GmosSouth]
      case _                    => DecodingFailure("Only Gmos supported", c.history).asLeft
    }
  )
