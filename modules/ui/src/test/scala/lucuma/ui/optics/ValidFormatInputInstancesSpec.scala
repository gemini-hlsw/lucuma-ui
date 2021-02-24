// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import munit.DisciplineSuite
import lucuma.core.math.Epoch
import lucuma.core.math.arb.ArbEpoch._
import lucuma.ui.optics.arb._
import lucuma.ui.optics.ValidFormatInput
import lucuma.ui.optics.laws.discipline.ValidFormatTests
import lucuma.ui.refined._
import eu.timepit.refined.cats._
import eu.timepit.refined.scalacheck.all._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary

final class ValidFormatInputInstancesSpec extends DisciplineSuite {
  import ArbTruncatedRA._
  import ArbTruncatedDec._

  val genUpperNES: Gen[UpperNES] =
    Gen.asciiStr.suchThat(_.nonEmpty).map(s => UpperNES.unsafeFrom(s.toUpperCase))

  implicit val arbUpperNES: Arbitrary[UpperNES] = Arbitrary(genUpperNES)

  // Laws
  checkAll("nonEmptyValidFormat",
           ValidFormatTests(ValidFormatInput.nonEmptyValidFormat).validFormat
  )
  checkAll("upperNESValidFormat",
           ValidFormatTests(ValidFormatInput.upperNESValidFormat).validFormat
  )
  checkAll("optionalEpochValidFormat",
           ValidFormatTests(ValidFormatInput.fromPrism(Epoch.fromString)).validFormat
  )
  checkAll("intValidFormat", ValidFormatTests(ValidFormatInput.intValidFormat()).validFormat)
  checkAll("bigDecimalValidFormat",
           ValidFormatTests(ValidFormatInput.bigDecimalValidFormat()).validFormat
  )
  checkAll("truncatedRAValidFormat", ValidFormatTests(ValidFormatInput.truncatedRA).validFormat)
  checkAll("truncatedDecValidFormat", ValidFormatTests(ValidFormatInput.truncatedDec).validFormat)
}
