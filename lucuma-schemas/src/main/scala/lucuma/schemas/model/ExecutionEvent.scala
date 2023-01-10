// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.char.Letter
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import lucuma.core.enums.DatasetStage
import lucuma.core.enums.SequenceCommand
import lucuma.core.enums.SequenceType
import lucuma.core.enums.StepStage
import lucuma.core.model.*
import lucuma.core.util.WithGid
import monocle.Focus
import monocle.Lens
import org.typelevel.cats.time.given

import java.time.Instant

sealed trait ExecutionEvent {
  def id: ExecutionEvent.Id
  def received: Instant
}

final case class DatasetEvent protected[schemas] (
  id:           ExecutionEvent.Id,
  received:     Instant,
  index:        PosInt,
  filename:     NonEmptyString,
  datasetStage: DatasetStage
) extends ExecutionEvent
    derives Eq

final case class SequenceEvent protected[schemas] (
  id:       ExecutionEvent.Id,
  received: Instant,
  command:  SequenceCommand
) extends ExecutionEvent
    derives Eq

final case class StepEvent protected[schemas] (
  id:           ExecutionEvent.Id,
  received:     Instant,
  sequenceType: SequenceType,
  stepStage:    StepStage
) extends ExecutionEvent
    derives Eq
