// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.Instrument
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.StaticConfig
import monocle.Focus
import monocle.Lens
import monocle.Prism
import monocle.macros.GenPrism

enum ExecutionVisits(val instrument: Instrument) derives Eq:
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
