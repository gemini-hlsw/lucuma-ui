// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import io.circe.{ CursorOp, Decoder, DecodingFailure }
import lucuma.core.math.Angle
import lucuma.core.model.{ PosAngle => PosAngleConstraint }

trait PosAngleDecoders {

  implicit val posAngleConstraintDecoder: Decoder[PosAngleConstraint] = {

    def missingAngle[A](kind: String, ops: => List[CursorOp]): Decoder.Result[A] =
      DecodingFailure(s"The $kind PosAngleConstraint requires an angle", ops).asLeft[A]

    def toPac(
      n:   String,
      a:   Option[Angle],
      ops: => List[CursorOp]
    ): Decoder.Result[PosAngleConstraint] =
      (n, a) match {
        case ("FIXED", Some(a))               => PosAngleConstraint.Fixed(a).asRight
        case ("FIXED", None)                  => missingAngle("FIXED", ops)
        case ("ALLOW_FLIP", Some(a))          => PosAngleConstraint.AllowFlip(a).asRight
        case ("ALLOW_FLIP", None)             => missingAngle("ALLOW_FLIP", ops)
        case ("AVERAGE_PARALLACTIC", None)    => PosAngleConstraint.AverageParallactic.asRight
        case ("AVERAGE_PARALLACTIC", Some(a)) => PosAngleConstraint.ParallacticOverride(a).asRight
        case _                                => DecodingFailure(s"Unknown constraint type `$n``", ops).asLeft
      }

    Decoder.instance { c =>
      if (c.value.isNull)
        PosAngleConstraint.Unconstrained.asRight
      else
        for {
          n <- c.downField("constraint").as[String]
          a <- c.downField("angle").as[Option[Angle]]
          p <- toPac(n, a, c.history)
        } yield p
    }
  }

}
