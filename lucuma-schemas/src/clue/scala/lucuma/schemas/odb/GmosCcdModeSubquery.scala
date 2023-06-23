// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB

@clue.annotation.GraphQL
abstract class GmosCcdModeSubquery extends GraphQLSubquery[ObservationDB]("GmosCcdMode"):
  override val subquery: String = """
        {
          xBin
          yBin
          ampCount
          ampGain
          ampReadMode
        }
      """

@clue.annotation.GraphQLStub
object GmosCcdModeSubquery
