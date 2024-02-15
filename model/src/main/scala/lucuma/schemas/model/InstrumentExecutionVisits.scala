// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.Instrument

sealed trait InstrumentExecutionVisits(val instrument: Instrument) derives Eq

object InstrumentExecutionVisits:
  case class GmosNorth(executionVisits: ExecutionVisits.GmosNorth)
      extends InstrumentExecutionVisits(Instrument.GmosNorth) derives Eq

  case class GmosSouth(executionVisits: ExecutionVisits.GmosSouth)
      extends InstrumentExecutionVisits(Instrument.GmosSouth) derives Eq
