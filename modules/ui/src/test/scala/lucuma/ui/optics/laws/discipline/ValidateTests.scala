// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.laws
package discipline

import cats.Eq
import lucuma.ui.optics.Validate
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import cats.laws.discipline._

trait ValidateTests[E, T, A] extends Laws {
  val validateLaws: ValidateLaws[E, T, A]

  def validate(implicit
    at: Arbitrary[T],
    et: Eq[T],
    aa: Arbitrary[A],
    ea: Eq[A],
    ee: Eq[E]
  ): RuleSet =
    new SimpleRuleSet(
      "validate",
      "normalize"        -> forAll((t: T) => validateLaws.normalize(t)),
      "parse roundtrip"  -> forAll((t: T) => validateLaws.parseRoundTrip(t)),
      "format roundtrip" -> forAll((a: A) => validateLaws.formatRoundTrip(a)),
      "coverage"         -> exists((t: T) => validateLaws.demonstratesValidationOrNormalization(t))
    )

  /** Convenience constructor that allows passing an explicit generator for input values. */
  def validateWith(gt: Gen[T])(implicit
    et:                Eq[T],
    aa:                Arbitrary[A],
    ea:                Eq[A],
    ee:                Eq[E]
  ): RuleSet =
    validate(Arbitrary(gt), et, aa, ea, ee)

}

object ValidateTests extends Laws {

  def apply[T, A, E](v: Validate[T, A, E]): ValidateTests[T, A, E] =
    new ValidateTests[T, A, E] {
      val validateLaws: ValidateLaws[T, A, E] = ValidateLaws(v)
    }

}
