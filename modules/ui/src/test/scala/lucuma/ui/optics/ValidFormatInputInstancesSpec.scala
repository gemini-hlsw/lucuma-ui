// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import munit.DisciplineSuite
import lucuma.ui.optics.ValidFormatInput
import lucuma.ui.optics.laws.discipline.ValidFormatTests
import lucuma.ui.refined._
import eu.timepit.refined.cats._
import eu.timepit.refined.scalacheck.all._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary

final class ValidFormatInputInstancesSpec extends DisciplineSuite {
  val genUpperNES: Gen[UpperNES] =
    Gen.alphaUpperStr.suchThat(_.nonEmpty).map(UpperNES.unsafeFrom)

  implicit val arbUpperNES: Arbitrary[UpperNES] = Arbitrary(genUpperNES)

  // Laws
  checkAll("nonEmptyValidFormat",
           ValidFormatTests(ValidFormatInput.nonEmptyValidFormat).validFormat
  )
  checkAll("upperNESValidFormat",
           ValidFormatTests(ValidFormatInput.upperNESValidFormat).validFormat
  )
}
