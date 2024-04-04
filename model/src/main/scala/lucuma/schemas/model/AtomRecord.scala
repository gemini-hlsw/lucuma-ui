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
import monocle.Focus
import monocle.Lens

enum AtomRecord[+D] derives Eq:
  def id: Atom.Id
  def created: Timestamp
  def interval: Option[TimestampInterval]
  def sequenceType: SequenceType
  def steps: List[StepRecord[D]]

  case GmosNorth protected[schemas] (
    id:           Atom.Id,
    created:      Timestamp,
    interval:     Option[TimestampInterval],
    sequenceType: SequenceType,
    steps:        List[StepRecord[DynamicConfig.GmosNorth]]
  ) extends AtomRecord[DynamicConfig.GmosNorth]

  case GmosSouth protected[schemas] (
    id:           Atom.Id,
    created:      Timestamp,
    interval:     Option[TimestampInterval],
    sequenceType: SequenceType,
    steps:        List[StepRecord[DynamicConfig.GmosSouth]]
  ) extends AtomRecord[DynamicConfig.GmosSouth]

object AtomRecord:
  object GmosNorth:
    val id: Lens[GmosNorth, Atom.Id] =
      Focus[GmosNorth](_.id)

    val created: Lens[GmosNorth, Timestamp] =
      Focus[GmosNorth](_.created)

    val interval: Lens[GmosNorth, Option[TimestampInterval]] =
      Focus[GmosNorth](_.interval)

    val sequenceType: Lens[GmosNorth, SequenceType] =
      Focus[GmosNorth](_.sequenceType)

    val steps: Lens[GmosNorth, List[StepRecord[DynamicConfig.GmosNorth]]] =
      Focus[GmosNorth](_.steps)

  object GmosSouth:
    val id: Lens[GmosSouth, Atom.Id] =
      Focus[GmosSouth](_.id)

    val created: Lens[GmosSouth, Timestamp] =
      Focus[GmosSouth](_.created)

    val interval: Lens[GmosSouth, Option[TimestampInterval]] =
      Focus[GmosSouth](_.interval)

    val sequenceType: Lens[GmosSouth, SequenceType] =
      Focus[GmosSouth](_.sequenceType)

    val steps: Lens[GmosSouth, List[StepRecord[DynamicConfig.GmosSouth]]] =
      Focus[GmosSouth](_.steps)
