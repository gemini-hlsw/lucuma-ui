// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.math.Offset
import lucuma.odb.json.offset.decoder.given
import lucuma.schemas.ObservationDB

object OffsetSubquery extends GraphQLSubquery.Typed[ObservationDB, Offset]("Offset"):
  override val subquery: String = """
        {
          p { microarcseconds }
          q { microarcseconds }
        }
      """
