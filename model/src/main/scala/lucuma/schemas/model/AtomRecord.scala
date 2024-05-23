// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.Atom
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.schemas.model.enums.AtomExecutionState
import monocle.Focus
import monocle.Lens

enum AtomRecord[+D] derives Eq:
  def id: Atom.Id
  def created: Timestamp
  def executionState: AtomExecutionState
  def interval: Option[TimestampInterval]
  def sequenceType: SequenceType
  def steps: List[StepRecord[D]]

  case GmosNorth protected[schemas] (
    id:             Atom.Id,
    created:        Timestamp,
    executionState: AtomExecutionState,
    interval:       Option[TimestampInterval],
    sequenceType:   SequenceType,
    steps:          List[StepRecord.GmosNorth]
  ) extends AtomRecord[DynamicConfig.GmosNorth]

  case GmosSouth protected[schemas] (
    id:             Atom.Id,
    created:        Timestamp,
    executionState: AtomExecutionState,
    interval:       Option[TimestampInterval],
    sequenceType:   SequenceType,
    steps:          List[StepRecord.GmosSouth]
  ) extends AtomRecord[DynamicConfig.GmosSouth]

object AtomRecord:
  object GmosNorth:
    given Eq[GmosNorth] = Eq.derived

    val id: Lens[GmosNorth, Atom.Id] =
      Focus[GmosNorth](_.id)

    val created: Lens[GmosNorth, Timestamp] =
      Focus[GmosNorth](_.created)

    val executionState: Lens[GmosNorth, AtomExecutionState] =
      Focus[GmosNorth](_.executionState)

    val interval: Lens[GmosNorth, Option[TimestampInterval]] =
      Focus[GmosNorth](_.interval)

    val sequenceType: Lens[GmosNorth, SequenceType] =
      Focus[GmosNorth](_.sequenceType)

    val steps: Lens[GmosNorth, List[StepRecord.GmosNorth]] =
      Focus[GmosNorth](_.steps)

  object GmosSouth:
    given Eq[GmosSouth] = Eq.derived

    val id: Lens[GmosSouth, Atom.Id] =
      Focus[GmosSouth](_.id)

    val created: Lens[GmosSouth, Timestamp] =
      Focus[GmosSouth](_.created)

    val interval: Lens[GmosSouth, Option[TimestampInterval]] =
      Focus[GmosSouth](_.interval)

    val sequenceType: Lens[GmosSouth, SequenceType] =
      Focus[GmosSouth](_.sequenceType)

    val steps: Lens[GmosSouth, List[StepRecord.GmosSouth]] =
      Focus[GmosSouth](_.steps)
