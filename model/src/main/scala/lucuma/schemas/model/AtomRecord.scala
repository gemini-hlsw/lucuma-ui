// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.Atom
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.gmos
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.schemas.model.enums.AtomExecutionState
import monocle.Focus
import monocle.Lens

enum AtomRecord[+D]:
  def id: Atom.Id
  def created: Timestamp
  def executionState: AtomExecutionState
  def interval: Option[TimestampInterval]
  def sequenceType: SequenceType
  def steps: List[StepRecord[D]]
  def generatedId: Option[Atom.Id]

  case GmosNorth protected[schemas] (
    id:             Atom.Id,
    created:        Timestamp,
    executionState: AtomExecutionState,
    interval:       Option[TimestampInterval],
    sequenceType:   SequenceType,
    steps:          List[StepRecord.GmosNorth],
    generatedId:    Option[Atom.Id]
  ) extends AtomRecord[gmos.DynamicConfig.GmosNorth]

  case GmosSouth protected[schemas] (
    id:             Atom.Id,
    created:        Timestamp,
    executionState: AtomExecutionState,
    interval:       Option[TimestampInterval],
    sequenceType:   SequenceType,
    steps:          List[StepRecord.GmosSouth],
    generatedId:    Option[Atom.Id]
  ) extends AtomRecord[gmos.DynamicConfig.GmosSouth]

  case Flamingos2 protected[schemas] (
    id:             Atom.Id,
    created:        Timestamp,
    executionState: AtomExecutionState,
    interval:       Option[TimestampInterval],
    sequenceType:   SequenceType,
    steps:          List[StepRecord.Flamingos2],
    generatedId:    Option[Atom.Id]
  ) extends AtomRecord[Flamingos2DynamicConfig]

object AtomRecord:
  given [A]: Eq[AtomRecord[A]] = Eq.derived

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

  object Flamingos2:
    given Eq[Flamingos2] = Eq.derived

    val id: Lens[Flamingos2, Atom.Id] =
      Focus[Flamingos2](_.id)

    val created: Lens[Flamingos2, Timestamp] =
      Focus[Flamingos2](_.created)

    val executionState: Lens[Flamingos2, AtomExecutionState] =
      Focus[Flamingos2](_.executionState)

    val interval: Lens[Flamingos2, Option[TimestampInterval]] =
      Focus[Flamingos2](_.interval)

    val sequenceType: Lens[Flamingos2, SequenceType] =
      Focus[Flamingos2](_.sequenceType)

    val steps: Lens[Flamingos2, List[StepRecord.Flamingos2]] =
      Focus[Flamingos2](_.steps)
