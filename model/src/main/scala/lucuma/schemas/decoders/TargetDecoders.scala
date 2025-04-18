// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import io.circe.Decoder
import lucuma.core.model.Target
import lucuma.odb.json.all.query.given
import lucuma.schemas.model.TargetWithId

trait TargetDecoders {

  given Decoder[TargetWithId] = Decoder.instance(c =>
    for {
      id     <- c.get[Target.Id]("id")
      target <- c.as[Target]
    } yield TargetWithId(id, target)
  )
}
