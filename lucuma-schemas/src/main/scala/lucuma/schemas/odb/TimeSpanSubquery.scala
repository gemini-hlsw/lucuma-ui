// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.util.TimeSpan
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given

object TimeSpanSubquery extends GraphQLSubquery.Typed[ObservationDB, TimeSpan]("TimeSpan"):
  override val subquery: String = """
        {
          microseconds
        }
      """
