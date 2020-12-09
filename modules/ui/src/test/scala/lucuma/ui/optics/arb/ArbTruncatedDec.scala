// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.arb

import lucuma.core.math.arb._
import lucuma.core.math.Declination
import lucuma.ui.optics.TruncatedDec
import org.scalacheck._
import org.scalacheck.Arbitrary._

trait ArbTruncatedDec {
  import ArbDeclination._

  implicit val arbTruncatedDec: Arbitrary[TruncatedDec] =
    Arbitrary(arbitrary[Declination].map(TruncatedDec(_)))

  implicit val cogTruncatedDec: Cogen[TruncatedDec] =
    Cogen[Declination].contramap(_.dec)
}

object ArbTruncatedDec extends ArbTruncatedDec
