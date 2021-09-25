// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.arb

import lucuma.core.math.Declination
import lucuma.core.math.arb._
import lucuma.ui.optics.TruncatedDec
import org.scalacheck.Arbitrary._
import org.scalacheck._

trait ArbTruncatedDec {
  import ArbDeclination._

  implicit val arbTruncatedDec: Arbitrary[TruncatedDec] =
    Arbitrary(arbitrary[Declination].map(TruncatedDec(_)))

  implicit val cogTruncatedDec: Cogen[TruncatedDec]     =
    Cogen[Declination].contramap(_.dec)
}

object ArbTruncatedDec extends ArbTruncatedDec
