// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.StaticConfig

trait ExecutionVisits[S, D]:
  def visits: List[Visit[S, D]]

object ExecutionVisits:
  case class GmosNorth(visits: List[Visit.GmosNorth])
      extends ExecutionVisits[StaticConfig.GmosNorth, DynamicConfig.GmosNorth]
  case class GmosSouth(visits: List[Visit.GmosSouth])
      extends ExecutionVisits[StaticConfig.GmosSouth, DynamicConfig.GmosSouth]
