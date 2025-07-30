// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
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
    parsedEnums.downField(field).as[List[E]] match
      case Left(err)     => err.printStackTrace; throw err
      case Right(values) => Enumerated.from(values.head, values.tail*).withTag(tagFn)

  // The givens are apparently (probably) constructed lazily.
  // See https://alexn.org/blog/2022/05/11/implicit-vs-scala-3-given/
  // We want to fail immediately if there is a problem, so we'll reference
  // the enumerated givens here.
  Enumerated[ProposalStatus]
