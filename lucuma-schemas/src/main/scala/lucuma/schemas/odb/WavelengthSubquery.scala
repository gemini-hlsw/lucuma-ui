// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.math.Wavelength
import lucuma.odb.json.wavelength.decoder.given
import lucuma.schemas.ObservationDB

object WavelengthSubquery extends GraphQLSubquery.Typed[ObservationDB, Wavelength]("Wavelength"):
  override val subquery: String = """
        {
          picometers
        }
      """
