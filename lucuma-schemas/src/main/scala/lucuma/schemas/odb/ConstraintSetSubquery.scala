// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.model.ConstraintSet
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given

object ConstraintSetSubquery
    extends GraphQLSubquery.Typed[ObservationDB, ConstraintSet]("ConstraintSet"):
  override val subquery: String = """
        {
          cloudExtinction
          imageQuality
          skyBackground
          waterVapor
          elevationRange {
            airMass {
              min
              max
            }
            hourAngle {
              minHours
              maxHours
            }
          }
        }
      """
