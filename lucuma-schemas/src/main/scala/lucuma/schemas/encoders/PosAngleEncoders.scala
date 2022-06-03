// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.encoders

import io.circe.Encoder
import io.circe.Json
import io.circe.syntax._
import lucuma.core.math.Angle
import lucuma.core.model.{PosAngle => PosAngleConstraint}

trait PosAngleEncoders {

  implicit val posAngleConstraintEncoder: Encoder[PosAngleConstraint] = {

    def withAngle(n: String, a: Angle): Json =
      Json.obj(
        "constraint" -> n.asJson,
        "angle"      -> Json.obj("microarcseconds" -> a.toMicroarcseconds.asJson)
      )

    def withoutAngle(n: String): Json =
      Json.obj("constraint" -> n.asJson)

    Encoder.instance {
      case PosAngleConstraint.Fixed(a)               => withAngle("FIXED", a)
      case PosAngleConstraint.AllowFlip(a)           => withAngle("ALLOW_FLIP", a)
      case PosAngleConstraint.AverageParallactic     => withoutAngle("AVERAGE_PARALLACTIC")
      case PosAngleConstraint.ParallacticOverride(a) => withAngle("AVERAGE_PARALLACTIC", a)
      case PosAngleConstraint.Unconstrained          => Json.Null
    }

  }

}
