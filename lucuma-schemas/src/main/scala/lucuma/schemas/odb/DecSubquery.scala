// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.math.Declination
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given

object DecSubquery extends GraphQLSubquery.Typed[ObservationDB, Declination]("Declination"):
  override val subquery: String = """
        {
          microarcseconds
        }
      """
