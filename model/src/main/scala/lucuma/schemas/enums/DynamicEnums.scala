// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.enums

import io.circe.*
import io.circe.parser.*
import lucuma.core.util.Enumerated

object DynamicEnums:
  val parsedEnums: ACursor =
    parse(Globals.enumMetadataString) match
      case Left(err)   => err.printStackTrace; throw err
      case Right(json) => json.hcursor

  def enumeratedInstance[E](
    field: String,
    tagFn: E => String
  )(using Decoder[E]): Enumerated[E] =
    val values: List[E] = parsedEnums.downField(field).as[List[E]] match
      case Left(err)   => err.printStackTrace; throw err
      case Right(list) => list
    Enumerated.from(values.head, values.tail: _*).withTag(tagFn)
