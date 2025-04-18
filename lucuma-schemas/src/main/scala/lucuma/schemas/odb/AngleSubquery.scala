// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.math.Angle
import lucuma.odb.json.angle.decoder.given
import lucuma.schemas.ObservationDB

object AngleSubquery extends GraphQLSubquery.Typed[ObservationDB, Angle]("Angle"):
  override val subquery: String = """
        {
          microarcseconds
        }
      """
