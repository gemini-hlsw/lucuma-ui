// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.arb

import eu.timepit.refined.auto._
import eu.timepit.refined.scalacheck.numeric.intervalClosedArbitrary
import lucuma.ui.optics.TruncatedBigDecimal
import org.scalacheck._
import org.scalacheck.Arbitrary._

trait ArbTruncatedBigDecimal {

  implicit val arbClosedInterval: Arbitrary[TruncatedBigDecimal.IntDecimals] =
    intervalClosedArbitrary

  implicit val arbTruncatedDecimal = Arbitrary[TruncatedBigDecimal] {
    arbitrary[BigDecimal].map(TruncatedBigDecimal(_, 2))
  }

  implicit def cogTruncatedBigDecimal: Cogen[TruncatedBigDecimal] =
    Cogen[BigDecimal].contramap(_.value)
}

object ArbTruncatedBigDecimal extends ArbTruncatedBigDecimal
