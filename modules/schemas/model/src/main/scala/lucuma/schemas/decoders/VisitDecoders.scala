// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosShort
import eu.timepit.refined.types.string.NonEmptyString
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
import lucuma.core.model.sequence.TelescopeConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.gmos
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.odb.json.flamingos2.given
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
      id        <- c.downField("id").as[Dataset.Id]
      index     <- c.downField("index").as[PosShort]
      filename  <- c.downField("filename").as[Dataset.Filename]
      qaState   <- c.downField("qaState").as[Option[DatasetQaState]]
      comment   <- c.downField("comment").as[Option[NonEmptyString]]
      interval  <- c.downField("interval").as[Option[TimestampInterval]]
      isWritten <- c.downField("isWritten").as[Boolean]
    yield Dataset(id, index, filename, qaState, comment, interval, isWritten)

  given Decoder[StepRecord.GmosNorth] = Decoder.instance: c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Timestamp]
      executionState   <- c.downField("executionState").as[StepExecutionState]
      interval         <- c.downField("interval").as[Option[TimestampInterval]]
      instrumentConfig <- c.downField("gmosNorth").as[gmos.DynamicConfig.GmosNorth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      telescopeConfig  <- c.downField("telescopeConfig").as[TelescopeConfig]
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
      telescopeConfig,
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
      instrumentConfig <- c.downField("gmosSouth").as[gmos.DynamicConfig.GmosSouth]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      telescopeConfig  <- c.downField("telescopeConfig").as[TelescopeConfig]
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
      telescopeConfig,
      observeClass,
      qaState,
      datasets,
      generatedId
    )

  given Decoder[StepRecord.Flamingos2] = Decoder.instance: c =>
    for
      id               <- c.downField("id").as[Step.Id]
      created          <- c.downField("created").as[Timestamp]
      executionState   <- c.downField("executionState").as[StepExecutionState]
      interval         <- c.downField("interval").as[Option[TimestampInterval]]
      instrumentConfig <- c.downField("flamingos2").as[Flamingos2DynamicConfig]
      stepConfig       <- c.downField("stepConfig").as[StepConfig]
      telescopeConfig  <- c.downField("telescopeConfig").as[TelescopeConfig]
      observeClass     <- c.downField("observeClass").as[ObserveClass]
      qaState          <- c.downField("qaState").as[Option[DatasetQaState]]
      datasets         <- c.downField("datasets").downField("matches").as[List[Dataset]]
      generatedId      <- c.downField("generatedId").as[Option[Step.Id]]
    yield StepRecord.Flamingos2(
      id,
      created,
      executionState,
      interval,
      instrumentConfig,
      stepConfig,
      telescopeConfig,
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

  given decoderAtomFlamingos2: Decoder[AtomRecord.Flamingos2] = Decoder.instance: c =>
    for
      id             <- c.downField("id").as[Atom.Id]
      created        <- c.downField("created").as[Timestamp]
      executionState <- c.downField("executionState").as[AtomExecutionState]
      interval       <- c.downField("interval").as[Option[TimestampInterval]]
      sequenceType   <- c.downField("sequenceType").as[SequenceType]
      steps          <- c.downField("steps").downField("matches").as[List[StepRecord.Flamingos2]]
      generatedId    <- c.downField("generatedId").as[Option[Atom.Id]]
    yield AtomRecord.Flamingos2(
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
      instrument <- c.downField("instrument").as[Instrument]
      _          <- instrument match
                      case i if i === Instrument.GmosNorth => Right(())
                      case _                               => Left(DecodingFailure("Not a GmosNorth Visit", c.history))
      id         <- c.downField("id").as[Visit.Id]
      created    <- c.downField("created").as[Timestamp]
      interval   <- c.downField("interval").as[Option[TimestampInterval]]
      steps      <- c.downField("atomRecords").downField("matches").as[List[AtomRecord.GmosNorth]]
    yield Visit.GmosNorth(id, created, interval, steps)

  given decoderVisitGmosSouth: Decoder[Visit.GmosSouth] = Decoder.instance: c =>
    for
      instrument <- c.downField("instrument").as[Instrument]
      _          <- instrument match
                      case i if i === Instrument.GmosSouth => Right(())
                      case _                               => Left(DecodingFailure("Not a GmosSouth Visit", c.history))
      id         <- c.downField("id").as[Visit.Id]
      created    <- c.downField("created").as[Timestamp]
      interval   <- c.downField("interval").as[Option[TimestampInterval]]
      steps      <- c.downField("atomRecords").downField("matches").as[List[AtomRecord.GmosSouth]]
    yield Visit.GmosSouth(id, created, interval, steps)

  given decoderVisitFlamingos2: Decoder[Visit.Flamingos2] = Decoder.instance: c =>
    for
      instrument <- c.downField("instrument").as[Instrument]
      _          <- instrument match
                      case i if i === Instrument.Flamingos2 => Right(())
                      case _                                => Left(DecodingFailure("Not a Flamingos2 Visit", c.history))
      id         <- c.downField("id").as[Visit.Id]
      created    <- c.downField("created").as[Timestamp]
      interval   <- c.downField("interval").as[Option[TimestampInterval]]
      steps      <- c.downField("atomRecords").downField("matches").as[List[AtomRecord.Flamingos2]]
    yield Visit.Flamingos2(id, created, interval, steps)

  given decoderExecutionVisitsGmosNorth: Decoder[ExecutionVisits.GmosNorth] = Decoder.instance: c =>
    c.downField("visits")
      .downField("matches")
      .as[NonEmptyList[Visit.GmosNorth]]
      .map:
        ExecutionVisits.GmosNorth(_)

  given decoderExecutionVisitsGmosSouth: Decoder[ExecutionVisits.GmosSouth] = Decoder.instance: c =>
    c.downField("visits")
      .downField("matches")
      .as[NonEmptyList[Visit.GmosSouth]]
      .map:
        ExecutionVisits.GmosSouth(_)

  given decoderExecutionVisitsFlamingos2: Decoder[ExecutionVisits.Flamingos2] = Decoder.instance:
    c =>
      c.downField("visits")
        .downField("matches")
        .as[NonEmptyList[Visit.Flamingos2]]
        .map:
          ExecutionVisits.Flamingos2(_)

  given Decoder[Option[ExecutionVisits]] =
    List(
      Decoder[ExecutionVisits.GmosNorth].widen,
      Decoder[ExecutionVisits.GmosSouth].widen,
      Decoder[ExecutionVisits.Flamingos2].widen
    )
      .reduceLeft(_ or _)
      .map(_.some)
      .or(Decoder.const(none))
