// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.Instrument
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.StaticConfig

enum ExecutionVisits(val instrument: Instrument) derives Eq:
  case GmosNorth protected[schemas] (
    staticConfig: StaticConfig.GmosNorth,
    visits:       List[Visit.GmosNorth]
  ) extends ExecutionVisits(Instrument.GmosNorth)

  case GmosSouth protected[schemas] (
    staticConfig: StaticConfig.GmosSouth,
    visits:       List[Visit.GmosSouth]
  ) extends ExecutionVisits(Instrument.GmosSouth)
