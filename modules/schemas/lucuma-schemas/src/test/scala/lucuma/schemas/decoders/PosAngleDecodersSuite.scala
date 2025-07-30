// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.effect.IO
import lucuma.core.math.Angle
import lucuma.core.model.PosAngleConstraint

class PosAngleDecodersSuite extends InputStreamSuite {

  val a42: Angle =
    Angle.fromDMS(42, 0, 0, 0, 0)

  def success(file: Int, expected: PosAngleConstraint): IO[Unit] =
    assertParsedStreamEquals(s"/pac$file.json", expected)

  def failure(file: Int, expected: String): IO[Unit] =
    assertParsedStreamFails[PosAngleConstraint](s"/pac$file.json", expected)

  test("Fixed error") {
    failure(2, "The FIXED PosAngleConstraint requires an angle")
  }

  test("Fixed 42") {
    success(3, PosAngleConstraint.Fixed(a42))
  }

  test("Override 42") {
    success(4, PosAngleConstraint.ParallacticOverride(a42))
  }

}
