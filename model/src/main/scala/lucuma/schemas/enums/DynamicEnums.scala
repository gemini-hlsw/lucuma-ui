// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.enums

import io.circe.*
import io.circe.parser.*

object DynamicEnums:
  val parsedEnums: ACursor =
    parse(Globals.enumMetadataString) match
      case Left(err)   => err.printStackTrace; throw err
      case Right(json) => json.hcursor
