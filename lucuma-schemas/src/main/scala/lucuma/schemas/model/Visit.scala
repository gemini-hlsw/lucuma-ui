// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.data.Ior
import cats.derived.*
import cats.syntax.all.given
import eu.timepit.refined.cats.given
import lucuma.core.model.NonNegDuration
import lucuma.core.model.sequence.StaticConfig
import lucuma.core.util.WithUid
import lucuma.refined.*
import monocle.Focus
import monocle.Lens
import monocle.Prism
import monocle.macros.GenPrism
import org.typelevel.cats.time.given

import java.time.Instant

sealed trait Visit derives Eq {
  def id: Visit.Id
  def created: Instant
  def startTime: Option[Instant]
  def endTime: Option[Instant]
  def duration: Option[NonNegDuration]
  def staticConfig: StaticConfig
  def steps: List[StepRecord]
  def sequenceEvents: List[SequenceEvent]
}

object Visit extends WithUid('v'.refined) {
  final case class GmosNorth protected[schemas] (
    id:             Visit.Id,
    created:        Instant,
    startTime:      Option[Instant],
    endTime:        Option[Instant],
    duration:       Option[NonNegDuration],
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
    duration:       Option[NonNegDuration],
    staticConfig:   StaticConfig.GmosSouth,
    steps:          List[StepRecord.GmosSouth],
    sequenceEvents: List[SequenceEvent]
  ) extends Visit
      derives Eq
}
