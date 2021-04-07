// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.kernel.laws.discipline.EqTests
import lucuma.core.optics.laws.discipline.SplitEpiTests
import lucuma.ui.optics.arb._
import org.scalacheck.Arbitrary._
import munit.DisciplineSuite

class TruncatedBigDecimalSpec extends DisciplineSuite {
  import ArbTruncatedBigDecimal._

  checkAll("TruncatedBigDecimal", EqTests[TruncatedBigDecimal[2]].eqv)

  checkAll("TruncatedRefinedBigDecimal.bigDecimal",
           SplitEpiTests(TruncatedBigDecimal.bigDecimal[2]).splitEpi
  )
}
