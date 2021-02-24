// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.arb

import lucuma.core.math.arb._
import lucuma.core.math.RightAscension
import lucuma.ui.optics.TruncatedRA
import org.scalacheck._
import org.scalacheck.Arbitrary._

trait ArbTruncatedRA {
  import ArbRightAscension._

  implicit val arbTruncatedRA: Arbitrary[TruncatedRA] =
    Arbitrary(arbitrary[RightAscension].map(TruncatedRA(_)))

  implicit val cogTruncatedRA: Cogen[TruncatedRA] =
    Cogen[RightAscension].contramap(_.ra)
}

object ArbTruncatedRA extends ArbTruncatedRA
