// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.all.given
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.gmos.StaticConfig
import lucuma.core.util.TimeSpan
import lucuma.core.util.WithUid
import lucuma.refined.*
import org.typelevel.cats.time.given

import java.time.Instant

sealed trait Visit derives Eq:
  def id: Visit.Id
  def created: Instant
  def startTime: Option[Instant]
  def endTime: Option[Instant]
  def duration: Option[TimeSpan]
  def staticConfig: StaticConfig
  def steps: List[StepRecord]
  def sequenceEvents: List[SequenceEvent]

  // Remove these implementations when the API supports querying by type directly.
  // See https://app.shortcut.com/lucuma/story/1853/separate-visit-steps-by-sequence-type
  def acquisitionSteps: List[StepRecord] =
    steps.filter(_.stepEvents.headOption.map(_.sequenceType).contains_(SequenceType.Acquisition))

  def scienceSteps: List[StepRecord] =
    steps.filter(_.stepEvents.headOption.map(_.sequenceType).contains_(SequenceType.Science))

object Visit extends WithUid('v'.refined):
  final case class GmosNorth protected[schemas] (
    id:             Visit.Id,
    created:        Instant,
    startTime:      Option[Instant],
    endTime:        Option[Instant],
    duration:       Option[TimeSpan],
    staticConfig:   StaticConfig.GmosNorth,
    steps:          List[StepRecord.GmosNorth],
    sequenceEvents: List[SequenceEvent]
  ) extends Visit
      derives Eq

  final case class GmosSouth protected[schemas] (
    id:             Visit.Id,
    created:        Instant,
    startTime:      Option[Instant],
    endTime:        Option[Instant],
    duration:       Option[TimeSpan],
    staticConfig:   StaticConfig.GmosSouth,
    steps:          List[StepRecord.GmosSouth],
    sequenceEvents: List[SequenceEvent]
  ) extends Visit
      derives Eq
