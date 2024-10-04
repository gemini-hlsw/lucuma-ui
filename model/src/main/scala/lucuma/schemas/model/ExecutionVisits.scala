// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.data.NonEmptyList
import cats.derived.*
import cats.syntax.eq.*
import lucuma.core.enums.Instrument
import monocle.Focus
import monocle.Lens
import monocle.Prism
import monocle.macros.GenPrism

enum ExecutionVisits(val instrument: Instrument) derives Eq:

  private def removeDuplicateVisitOverlap[D, V <: Visit[D]](
    left:  NonEmptyList[V],
    right: NonEmptyList[V]
  ): NonEmptyList[V] =
    NonEmptyList
      .fromList:
        left.toList.takeWhile: v =>
          right.head.id =!= v.id
      .fold(right)(_ ++ right.toList)

  def extendWith(other: ExecutionVisits): ExecutionVisits =
    (this, other) match
      case (GmosNorth(leftVisits), GmosNorth(rightVisits)) =>
        GmosNorth(removeDuplicateVisitOverlap(leftVisits, rightVisits))
      case (GmosSouth(leftVisits), GmosSouth(rightVisits)) =>
        GmosSouth(removeDuplicateVisitOverlap(leftVisits, rightVisits))
      case (left, right)                                   =>
        throw new Exception:
          s"Attempted to join ExecutionVisits for different instruments: ${left.instrument} and ${right.instrument}"

  case GmosNorth(visits: NonEmptyList[Visit.GmosNorth])
      extends ExecutionVisits(Instrument.GmosNorth)

  case GmosSouth(visits: NonEmptyList[Visit.GmosSouth])
      extends ExecutionVisits(Instrument.GmosSouth)

object ExecutionVisits:
  val gmosNorth: Prism[ExecutionVisits, ExecutionVisits.GmosNorth] =
    GenPrism[ExecutionVisits, ExecutionVisits.GmosNorth]

  val gmosSouth: Prism[ExecutionVisits, ExecutionVisits.GmosSouth] =
    GenPrism[ExecutionVisits, ExecutionVisits.GmosSouth]

  object GmosNorth:
    val visits: Lens[GmosNorth, NonEmptyList[Visit.GmosNorth]] =
      Focus[GmosNorth](_.visits)

  object GmosSouth:
    val visits: Lens[GmosSouth, NonEmptyList[Visit.GmosSouth]] =
      Focus[GmosSouth](_.visits)
