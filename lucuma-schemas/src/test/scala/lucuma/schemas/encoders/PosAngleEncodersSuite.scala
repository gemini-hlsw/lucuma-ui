// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.encoders

import cats.effect.IO
import io.circe.Encoder
import lucuma.core.model.PosAngleConstraint
import lucuma.schemas.decoders._

class PosAngleEncodersSuite extends InputStreamSuite {

  def roundtrip(file: Int): IO[Unit] =
    for {
      jsonIn <- jsonResult(s"/pac$file.json")
      pac    <- IO.fromEither(jsonIn.as[PosAngleConstraint])
      jsonOut = Encoder[PosAngleConstraint].apply(pac)
    } yield assertEquals(jsonIn.spaces2, jsonOut.spaces2)

  test("Fixed 42") {
    roundtrip(3)
  }

  test("Override 42") {
    roundtrip(4)
  }

}
