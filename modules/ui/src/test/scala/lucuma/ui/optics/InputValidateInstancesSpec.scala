// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import munit.DisciplineSuite
import lucuma.ui.optics.ValidateInput
import lucuma.ui.optics.laws.discipline.ValidateTests
import eu.timepit.refined.types.string._
import eu.timepit.refined.cats._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.char.UpperCase
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.collection.Forall
import eu.timepit.refined.boolean.And
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import eu.timepit.refined.scalacheck.all._

final class InputValidateInstancesSpec extends DisciplineSuite {

  val nonEmptyValidate = ValidateInput[NonEmptyString](
    s => NonEmptyString.from(s).fold(_ => "Can't be empty".invalidNec, _.validNec),
    _.toString
  )

  type UpperNES = String Refined And[NonEmpty, Forall[UpperCase]]
  object UpperNES extends RefinedTypeOps[UpperNES, String]

  val upperNESValidate = ValidateInput[UpperNES](
    s => UpperNES.from(s.toUpperCase).fold(_ => "Can't be empty".invalidNec, s => s.validNec),
    _.toString
  )

  val genUpperNES: Gen[UpperNES] =
    Gen.alphaUpperStr.suchThat(_.nonEmpty).map(UpperNES.unsafeFrom)

  implicit val arbUpperNES: Arbitrary[UpperNES] = Arbitrary(genUpperNES)

  // Laws
  checkAll("nonEmptyValidate", ValidateTests(nonEmptyValidate).validate)
  checkAll("upperNESValidate", ValidateTests(upperNESValidate).validate)
}
