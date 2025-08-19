// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.TargetWithId

object TargetWithIdSubquery extends GraphQLSubquery.Typed[ObservationDB, TargetWithId]("Target"):
  override val subquery: String = s"""  
    {
      id
      name
      sidereal {
        ra $RASubquery
        dec $DecSubquery
        epoch
        properMotion $ProperMotionSubquery
        radialVelocity $RadialVelocitySubquery
        parallax $AngleSubquery
        catalogInfo {
          name
          id
          objectType
        }
      }
      opportunity {
        region {
          rightAscensionArc {
            type
            start $RASubquery
            end $RASubquery
          }
          declinationArc {
            type
            start $DecSubquery
            end $DecSubquery
          }
        }
      }
      sourceProfile {
        point {
          bandNormalized $BandNormalizedIntegratedSubquery
          emissionLines $EmissionLinesIntegratedSubquery
        }
        uniform {
          bandNormalized $BandNormalizedSurfaceSubquery
          emissionLines $EmissionLinesSurfaceSubquery
        }
        gaussian {
          fwhm $AngleSubquery
          bandNormalized $BandNormalizedIntegratedSubquery
          emissionLines $EmissionLinesIntegratedSubquery
        }
      }
    }
  """
