// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.ConstraintsSummary

object ConstraintsSummarySubquery
    extends GraphQLSubquery.Typed[ObservationDB, ConstraintsSummary]("ConstraintSet"):
  override val subquery: String = """
        {
          imageQuality
          cloudExtinction
          skyBackground
          waterVapor
        }
      """
