// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.codecs

import io.circe.testing.CodecTests
import io.circe.testing.instances.arbitraryJson
import lucuma.core.model.PosAngleConstraint
import lucuma.core.model.arb.ArbPosAngleConstraint
import lucuma.schemas.decoders._
import lucuma.schemas.encoders._
import munit.DisciplineSuite

class PosAngleConstraintCodecSuite extends DisciplineSuite {

  import ArbPosAngleConstraint._

  checkAll("PosAngleConstraint Codec", CodecTests[PosAngleConstraint].codec)

}
