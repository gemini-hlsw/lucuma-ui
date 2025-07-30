// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.all.given
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.gmos
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import monocle.Focus
import monocle.Lens

enum Visit[+D]:
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
    atoms:    List[AtomRecord.GmosNorth]
  ) extends Visit[gmos.DynamicConfig.GmosNorth]

  case GmosSouth(
    id:       Visit.Id,
    created:  Timestamp,
    interval: Option[TimestampInterval],
    atoms:    List[AtomRecord.GmosSouth]
  ) extends Visit[gmos.DynamicConfig.GmosSouth]

  case Flamingos2(
    id:       Visit.Id,
    created:  Timestamp,
    interval: Option[TimestampInterval],
    atoms:    List[AtomRecord.Flamingos2]
  ) extends Visit[Flamingos2DynamicConfig]

object Visit:
  type Id = lucuma.core.model.Visit.Id
  val Id = lucuma.core.model.Visit.Id

  given [A]: Eq[Visit[A]] = Eq.derived

  object GmosNorth:
    given Eq[GmosNorth] = Eq.derived

    val id: Lens[GmosNorth, Visit.Id] =
      Focus[GmosNorth](_.id)

    val created: Lens[GmosNorth, Timestamp] =
      Focus[GmosNorth](_.created)

    val interval: Lens[GmosNorth, Option[TimestampInterval]] =
      Focus[GmosNorth](_.interval)

    val atoms: Lens[GmosNorth, List[AtomRecord.GmosNorth]] =
      Focus[GmosNorth](_.atoms)

  object GmosSouth:
    given Eq[GmosSouth] = Eq.derived

    val id: Lens[GmosSouth, Visit.Id] =
      Focus[GmosSouth](_.id)

    val created: Lens[GmosSouth, Timestamp] =
      Focus[GmosSouth](_.created)

    val interval: Lens[GmosSouth, Option[TimestampInterval]] =
      Focus[GmosSouth](_.interval)

    val atoms: Lens[GmosSouth, List[AtomRecord.GmosSouth]] =
      Focus[GmosSouth](_.atoms)

  object Flamingos2:
    given Eq[Flamingos2] = Eq.derived

    val id: Lens[Flamingos2, Visit.Id] =
      Focus[Flamingos2](_.id)

    val created: Lens[Flamingos2, Timestamp] =
      Focus[Flamingos2](_.created)

    val interval: Lens[Flamingos2, Option[TimestampInterval]] =
      Focus[Flamingos2](_.interval)

    val atoms: Lens[Flamingos2, List[AtomRecord.Flamingos2]] =
      Focus[Flamingos2](_.atoms)
