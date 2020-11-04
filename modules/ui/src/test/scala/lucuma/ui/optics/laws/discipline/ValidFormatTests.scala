// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics.laws
package discipline

import cats.Eq
import lucuma.ui.optics.ValidFormat
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import cats.laws.discipline._

trait ValidFormatTests[E, T, A] extends Laws {
  val validFormatLaws: ValidFormatLaws[E, T, A]

  def validFormat(implicit
    at: Arbitrary[T],
    et: Eq[T],
    aa: Arbitrary[A],
    ea: Eq[A],
    ee: Eq[E]
  ): RuleSet =
    new SimpleRuleSet(
      "validate",
      "normalize"        -> forAll((t: T) => validFormatLaws.normalize(t)),
      "parse roundtrip"  -> forAll((t: T) => validFormatLaws.parseRoundTrip(t)),
      "format roundtrip" -> forAll((a: A) => validFormatLaws.formatRoundTrip(a)),
      "coverage"         -> exists((t: T) => validFormatLaws.demonstratesValidationOrNormalization(t))
    )

  /** Convenience constructor that allows passing an explicit generator for input values. */
  def validFormatWith(gt: Gen[T])(implicit
    et:                   Eq[T],
    aa:                   Arbitrary[A],
    ea:                   Eq[A],
    ee:                   Eq[E]
  ): RuleSet =
    validFormat(Arbitrary(gt), et, aa, ea, ee)

}

object ValidFormatTests extends Laws {

  def apply[T, A, E](v: ValidFormat[T, A, E]): ValidFormatTests[T, A, E] =
    new ValidFormatTests[T, A, E] {
      val validFormatLaws: ValidFormatLaws[T, A, E] = ValidFormatLaws(v)
    }

}
