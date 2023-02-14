// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.arb

import lucuma.core.enums.*
import lucuma.core.util.arb.ArbEnumerated.*
import lucuma.schemas.model.ConstraintsSummary
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.*
import org.scalacheck.Cogen
import org.scalacheck.Cogen.*

trait ArbConstraintsSummary {
  def buildConstraintsSummaryArb[A <: ConstraintsSummary](
    build: (
      ImageQuality,
      CloudExtinction,
      SkyBackground,
      WaterVapor
    ) => A
  ) = Arbitrary[A] {
    for {
      iq <- arbitrary[ImageQuality]
      ce <- arbitrary[CloudExtinction]
      sb <- arbitrary[SkyBackground]
      wv <- arbitrary[WaterVapor]
    } yield build(iq, ce, sb, wv)
  }

  given Arbitrary[ConstraintsSummary] =
    buildConstraintsSummaryArb((iq, ce, sb, wv) => ConstraintsSummary(iq, ce, sb, wv))

  given Cogen[ConstraintsSummary] =
    Cogen[(ImageQuality, CloudExtinction, SkyBackground, WaterVapor)]
      .contramap(cs => (cs.imageQuality, cs.cloudExtinction, cs.skyBackground, cs.waterVapor))
}

object ArbConstraintsSummary extends ArbConstraintsSummary
