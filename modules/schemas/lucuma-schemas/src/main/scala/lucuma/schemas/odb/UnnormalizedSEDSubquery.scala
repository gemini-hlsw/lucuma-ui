// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.model.UnnormalizedSED
import lucuma.odb.json.sourceprofile.given
import lucuma.schemas.ObservationDB

object UnnormalizedSEDSubquery
    extends GraphQLSubquery.Typed[ObservationDB, UnnormalizedSED]("UnnormalizedSed"):
  override val subquery: String = s"""
        {
          stellarLibrary
          coolStar
          galaxy
          planet
          quasar
          hiiRegion
          planetaryNebula
          powerLaw
          blackBodyTempK
          fluxDensities {
            wavelength $WavelengthSubquery
            density
          }
          fluxDensitiesAttachment
        }
      """
