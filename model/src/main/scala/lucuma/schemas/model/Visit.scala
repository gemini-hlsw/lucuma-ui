// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.all.given
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import monocle.Focus
import monocle.Lens

enum Visit[+D] derives Eq:
  def id: Visit.Id
  def created: Timestamp
  def interval: Option[TimestampInterval]
  def atoms: List[AtomRecord[D]]

  def acquisitionAtoms: List[AtomRecord[D]] =
    atoms.filter(_.sequenceType === SequenceType.Acquisition)

  def scienceAtoms: List[AtomRecord[D]] =
    atoms.filter(_.sequenceType === SequenceType.Science)

  case GmosNorth(
    id:       Visit.Id,
    created:  Timestamp,
    interval: Option[TimestampInterval],
    atoms:    List[AtomRecord[DynamicConfig.GmosNorth]]
  ) extends Visit[DynamicConfig.GmosNorth]

  case GmosSouth(
    id:       Visit.Id,
    created:  Timestamp,
    interval: Option[TimestampInterval],
    atoms:    List[AtomRecord[DynamicConfig.GmosSouth]]
  ) extends Visit[DynamicConfig.GmosSouth]

object Visit:
  type Id = lucuma.core.model.Visit.Id
  val Id = lucuma.core.model.Visit.Id

  object GmosNorth:
    val id: Lens[GmosNorth, Visit.Id] =
      Focus[GmosNorth](_.id)

    val created: Lens[GmosNorth, Timestamp] =
      Focus[GmosNorth](_.created)

    val interval: Lens[GmosNorth, Option[TimestampInterval]] =
      Focus[GmosNorth](_.interval)

    val atoms: Lens[GmosNorth, List[AtomRecord[DynamicConfig.GmosNorth]]] =
      Focus[GmosNorth](_.atoms)

  object GmosSouth:
    val id: Lens[GmosSouth, Visit.Id] =
      Focus[GmosSouth](_.id)

    val created: Lens[GmosSouth, Timestamp] =
      Focus[GmosSouth](_.created)

    val interval: Lens[GmosSouth, Option[TimestampInterval]] =
      Focus[GmosSouth](_.interval)

    val atoms: Lens[GmosSouth, List[AtomRecord[DynamicConfig.GmosSouth]]] =
      Focus[GmosSouth](_.atoms)
