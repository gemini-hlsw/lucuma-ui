// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.input

import eu.timepit.refined.types.numeric.NonNegInt
import lucuma.ui.input.FormatUtils.*
import munit.DisciplineSuite
import org.scalacheck.*
import org.scalacheck.Arbitrary.*
import org.scalacheck.Prop.*

final class FormatUtilsSpec extends DisciplineSuite:
  val Zero = BigInt(0)
  test("stripZerosPastNPlaces") {
    forAll(arbitrary[BigInt], Gen.choose(0, 999999), Gen.choose(0, 9), Gen.choose(0, 9)) {
      (i, d0, n0, z) =>
        val d    = d0.toString + "0" * z
        val keep = d.take(n0) + d.drop(n0).reverse.dropWhile(_ == '0').reverse
        val n    = NonNegInt.unsafeFrom(n0)
        assertEquals(stripZerosPastNPlaces(s"$i.$d", n), s"$i.$keep")
    }
  }
