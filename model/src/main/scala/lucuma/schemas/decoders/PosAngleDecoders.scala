// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all.*
import io.circe.CursorOp
import io.circe.Decoder
import io.circe.DecodingFailure
import lucuma.core.math.Angle
import lucuma.core.model.PosAngleConstraint
import lucuma.odb.json.angle.decoder.given

trait PosAngleDecoders {

  given Decoder[PosAngleConstraint] = {

    def missingAngle[A](kind: String, ops: => List[CursorOp]): Decoder.Result[A] =
      DecodingFailure(s"The $kind PosAngleConstraint requires an angle", ops).asLeft[A]

    def toPac(
      n:   String,
      a:   Option[Angle],
      ops: => List[CursorOp]
    ): Decoder.Result[PosAngleConstraint] =
      (n, a) match {
        case ("FIXED", Some(a))                => PosAngleConstraint.Fixed(a).asRight
        case ("FIXED", None)                   => missingAngle("FIXED", ops)
        case ("ALLOW_FLIP", Some(a))           => PosAngleConstraint.AllowFlip(a).asRight
        case ("ALLOW_FLIP", None)              => missingAngle("ALLOW_FLIP", ops)
        case ("AVERAGE_PARALLACTIC", _)        => PosAngleConstraint.AverageParallactic.asRight
        case ("PARALLACTIC_OVERRIDE", Some(a)) => PosAngleConstraint.ParallacticOverride(a).asRight
        case ("PARALLACTIC_OVERRIDE", None)    => missingAngle("PARALLACTIC_OVERRIDE", ops)
        case ("UNBOUNDED", _)                  => PosAngleConstraint.Unbounded.asRight
        case _                                 => DecodingFailure(s"Unknown constraint type `$n``", ops).asLeft
      }

    Decoder.instance { c =>
      for {
        n <- c.downField("mode").as[String]
        a <- c.downField("angle").as[Option[Angle]]
        p <- toPac(n, a, c.history)
      } yield p
    }
  }

}
