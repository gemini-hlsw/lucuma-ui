// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all._
import munit.DisciplineSuite
import lucuma.ui.optics.laws.discipline.ValidateTests
import org.scalacheck.Prop._
import cats.data.Validated
import cats.data.NonEmptyChain
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

final class ValidateSpec extends DisciplineSuite {

// Our example Validate allows only positive ints and injects into even ints
  val example: ValidateNec[String, Int, Boolean] =
    ValidateNec(n => if (n > 0) (n % 2 == 0).validNec else "Must be > 0".invalidNec,
                b => if (b) 2 else 1
    )

// Ensure it's lawful
  checkAll("Validate.example", ValidateTests(example).validate)

  test("unsafeGet.consistent with getValidate") {
    forAll { (n: Int) =>
      assertEquals(
        example.getValidated(n),
        Validated
          .catchNonFatal(example.unsafeGet(n))
          .leftMap(_ => NonEmptyChain("Must be > 0"))
      )
    }
  }

  test("unsafeGet.error message") {
    forAll { (n: Int) =>
      Validated.catchNonFatal(example.unsafeGet(n)) match {
        case Invalid(s) => s.getMessage === s"unsafeGet failed: $n"
        case Valid(_)   => true
      }
    }
  }

}
