// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosShort
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.refined.given
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.Instrument
import lucuma.core.enums.ObserveClass
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.Atom
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.StaticConfig
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.odb.json.gmos.given
import lucuma.odb.json.stepconfig.given
import lucuma.odb.json.time.decoder.given
import lucuma.schemas.model.*
import lucuma.schemas.model.enums.AtomExecutionState
import lucuma.schemas.model.enums.StepExecutionState

trait VisitDecoders:
  given Decoder[Dataset.Filename] = Decoder.instance: c =>
    c.as[String]
      .flatMap:
        Dataset.Filename
          .parse(_)
          .toRight(DecodingFailure("Error parsing Dataset.Filename", c.history))

  given Decoder[Dataset] = Decoder.instance: c =>
    for
      id       <- c.downField("id").as[Dataset.Id]
      index    <- c.downField("index").as[PosShort]
      filename <- c.downField("filename").as[Dataset.Filename]
      qaState  <- c.downField("qaState").as[Option[DatasetQaState]]
      interval <- c.downField("interval").as[Option[TimestampInterval]]
    yield Dataset(id, index, filename, qaState, interval)

  given Decoder[StepRecord.GmosNorth] = Decoder.instance: c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Timestamp]
      executionState   <- c.downField("executionState").as[StepExecutionState]
      interval         <- c.downField("interval").as[Option[TimestampInterval]]
      instrumentConfig <- c.downField("gmosNorth").as[DynamicConfig.GmosNorth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      observeClass     <- c.downField("observeClass").as[ObserveClass]
      qaState          <- c.downField("qaState").as[Option[DatasetQaState]]
      datasets         <- c.downField("datasets").downField("matches").as[List[Dataset]]
      generatedId      <- c.downField("generatedId").as[Option[Step.Id]]
    yield StepRecord.GmosNorth(
      id,
      created,
      executionState,
      interval,
      instrumentConfig,
      stepConfig,
      observeClass,
      qaState,
      datasets,
      generatedId
    )

  given Decoder[StepRecord.GmosSouth] = Decoder.instance: c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Timestamp]
      executionState   <- c.downField("executionState").as[StepExecutionState]
      interval         <- c.downField("interval").as[Option[TimestampInterval]]
      instrumentConfig <- c.downField("gmosSouth").as[DynamicConfig.GmosSouth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      observeClass     <- c.downField("observeClass").as[ObserveClass]
      qaState          <- c.downField("qaState").as[Option[DatasetQaState]]
      datasets         <- c.downField("datasets").downField("matches").as[List[Dataset]]
      generatedId      <- c.downField("generatedId").as[Option[Step.Id]]
    yield StepRecord.GmosSouth(
      id,
      created,
      executionState,
      interval,
      instrumentConfig,
      stepConfig,
      observeClass,
      qaState,
      datasets,
      generatedId
    )

  given decoderAtomGmosNorth: Decoder[AtomRecord.GmosNorth] = Decoder.instance: c =>
    for
      id             <- c.downField("id").as[Atom.Id]
      created        <- c.downField("created").as[Timestamp]
      executionState <- c.downField("executionState").as[AtomExecutionState]
      interval       <- c.downField("interval").as[Option[TimestampInterval]]
      sequenceType   <- c.downField("sequenceType").as[SequenceType]
      steps          <- c.downField("steps").downField("matches").as[List[StepRecord.GmosNorth]]
      generatedId    <- c.downField("generatedId").as[Option[Atom.Id]]
    yield AtomRecord.GmosNorth(
      id,
      created,
      executionState,
      interval,
      sequenceType,
      steps,
      generatedId
    )

  given decoderAtomGmosSouth: Decoder[AtomRecord.GmosSouth] = Decoder.instance: c =>
    for
      id             <- c.downField("id").as[Atom.Id]
      created        <- c.downField("created").as[Timestamp]
      executionState <- c.downField("executionState").as[AtomExecutionState]
      interval       <- c.downField("interval").as[Option[TimestampInterval]]
      sequenceType   <- c.downField("sequenceType").as[SequenceType]
      steps          <- c.downField("steps").downField("matches").as[List[StepRecord.GmosSouth]]
      generatedId    <- c.downField("generatedId").as[Option[Atom.Id]]
    yield AtomRecord.GmosSouth(
      id,
      created,
      executionState,
      interval,
      sequenceType,
      steps,
      generatedId
    )

  // We must specify a name since the automatic names only take the last part of the type path,
  // generating conflicts among all the `.GmosNorth` and `.GmosSouth` types.
  // See https://dotty.epfl.ch/docs/reference/contextual/givens.html#anonymous-givens
  given decoderVisitGmosNorth: Decoder[Visit.GmosNorth] = Decoder.instance: c =>
    for
      id       <- c.downField("id").as[Visit.Id]
      created  <- c.downField("created").as[Timestamp]
      interval <- c.downField("interval").as[Option[TimestampInterval]]
      steps    <- c.downField("atomRecords").downField("matches").as[List[AtomRecord.GmosNorth]]
    yield Visit.GmosNorth(id, created, interval, steps)

  given decoderVisitGmosSouth: Decoder[Visit.GmosSouth] = Decoder.instance: c =>
    for
      id       <- c.downField("id").as[Visit.Id]
      created  <- c.downField("created").as[Timestamp]
      interval <- c.downField("interval").as[Option[TimestampInterval]]
      steps    <- c.downField("atomRecords").downField("matches").as[List[AtomRecord.GmosSouth]]
    yield Visit.GmosSouth(id, created, interval, steps)

  given decoderExecutionVisitsGmosNorth: Decoder[ExecutionVisits.GmosNorth] = Decoder.instance: c =>
    for
      staticConfig <-
        c.downField("config").downField("gmosNorth").downField("static").as[StaticConfig.GmosNorth]
      visits       <- c.downField("visits").downField("matches").as[List[Visit.GmosNorth]]
    yield ExecutionVisits.GmosNorth(staticConfig, visits)

  given decoderExecutionVisitsGmosSouth: Decoder[ExecutionVisits.GmosSouth] = Decoder.instance: c =>
    for
      staticConfig <-
        c.downField("config").downField("gmosSouth").downField("static").as[StaticConfig.GmosSouth]
      visits       <- c.downField("visits").downField("matches").as[List[Visit.GmosSouth]]
    yield ExecutionVisits.GmosSouth(staticConfig, visits)

  given Decoder[Option[ExecutionVisits]] = Decoder.instance: c =>
    if (c.downField("config").downField("instrument").failed) None.asRight
    else
      c.downField("config")
        .downField("instrument")
        .as[Instrument]
        .flatMap:
          case Instrument.GmosNorth => c.as[ExecutionVisits.GmosNorth].map(_.some)
          case Instrument.GmosSouth => c.as[ExecutionVisits.GmosSouth].map(_.some)
          case _                    => DecodingFailure("Only Gmos supported", c.history).asLeft
