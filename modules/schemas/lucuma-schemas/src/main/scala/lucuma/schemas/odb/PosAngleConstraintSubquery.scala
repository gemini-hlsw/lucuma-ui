// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.model.PosAngleConstraint
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given

object PosAngleConstraintSubquery
    extends GraphQLSubquery.Typed[ObservationDB, PosAngleConstraint]("PosAngleConstraint"):
  override val subquery: String = s"""
        {
          mode
          angle $AngleSubquery
        }
      """
