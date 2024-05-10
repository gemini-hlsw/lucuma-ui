// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.eq.*
import lucuma.core.enums.Instrument
import lucuma.core.model.sequence.gmos.StaticConfig
import monocle.Focus
import monocle.Lens
import monocle.Prism
import monocle.macros.GenPrism

enum ExecutionVisits(val instrument: Instrument) derives Eq:

  private def removeDuplicateVisitOverlap[D, V <: Visit[D]](
    left:  List[V],
    right: List[V]
  ): List[V] =
    left.takeWhile: v =>
      right.headOption.forall(_.id =!= v.id)
    ++ right

  def extendWith(other: ExecutionVisits): ExecutionVisits =
    (this, other) match
      case (GmosNorth(leftConfig, leftVisits), GmosNorth(_, rightVisits)) =>
        GmosNorth(leftConfig, removeDuplicateVisitOverlap(leftVisits, rightVisits))
      case (GmosSouth(leftConfig, leftVisits), GmosSouth(_, rightVisits)) =>
        GmosSouth(leftConfig, removeDuplicateVisitOverlap(leftVisits, rightVisits))
      case (left, right)                                                  =>
        throw new Exception:
          s"Attempted to join ExecutionVisits for different instruments: ${left.instrument} and ${right.instrument}"

  case GmosNorth(
    staticConfig: StaticConfig.GmosNorth,
    visits:       List[Visit.GmosNorth]
  ) extends ExecutionVisits(Instrument.GmosNorth)

  case GmosSouth(
    staticConfig: StaticConfig.GmosSouth,
    visits:       List[Visit.GmosSouth]
  ) extends ExecutionVisits(Instrument.GmosSouth)

object ExecutionVisits:
  val gmosNorth: Prism[ExecutionVisits, ExecutionVisits.GmosNorth] =
    GenPrism[ExecutionVisits, ExecutionVisits.GmosNorth]

  val gmosSouth: Prism[ExecutionVisits, ExecutionVisits.GmosSouth] =
    GenPrism[ExecutionVisits, ExecutionVisits.GmosSouth]

  object GmosNorth:
    val staticConfig: Lens[GmosNorth, StaticConfig.GmosNorth] =
      Focus[GmosNorth](_.staticConfig)

    val visits: Lens[GmosNorth, List[Visit.GmosNorth]] =
      Focus[GmosNorth](_.visits)

  object GmosSouth:
    val staticConfig: Lens[GmosSouth, StaticConfig.GmosSouth] =
      Focus[GmosSouth](_.staticConfig)

    val visits: Lens[GmosSouth, List[Visit.GmosSouth]] =
      Focus[GmosSouth](_.visits)
