// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.math.ProperMotion
import lucuma.odb.json.propermotion.decoder.given
import lucuma.schemas.ObservationDB

object ProperMotionSubquery
    extends GraphQLSubquery.Typed[ObservationDB, ProperMotion]("ProperMotion"):
  override val subquery: String = """
        {
          ra {
            microarcsecondsPerYear
          }
          dec {
            microarcsecondsPerYear
          }
        }
      """
