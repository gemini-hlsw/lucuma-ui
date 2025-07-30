// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import clue.annotation.GraphQL
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.odb.json.flamingos2.given
import lucuma.schemas.ObservationDB

@GraphQL
object Flamingos2DynamicConfigSubquery
    extends GraphQLSubquery.Typed[ObservationDB, Flamingos2DynamicConfig]("Flamingos2Dynamic"):
  override val subquery: String = s"""
    {
      exposure $TimeSpanSubquery
      disperser 
      filter
      readMode
      lyotWheel
      fpu {
        customMask {
          filename
          slitWidth
        }
        builtin
      }
      decker
      readoutMode
      reads
    }
  """
