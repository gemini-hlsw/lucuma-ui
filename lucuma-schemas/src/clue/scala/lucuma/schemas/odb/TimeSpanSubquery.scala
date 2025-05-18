// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import clue.annotation.GraphQL
import lucuma.core.util.TimeSpan
import lucuma.odb.json.time.decoder.given
import lucuma.schemas.ObservationDB

@GraphQL
object TimeSpanSubquery extends GraphQLSubquery.Typed[ObservationDB, TimeSpan]("TimeSpan"):
  override val subquery: String = """
        {
          microseconds
        }
      """
