// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.enum.TacCategory
import lucuma.core.enum.ToOActivation
import lucuma.core.model.IntPercent
import lucuma.core.model.NonNegDuration
import lucuma.core.model.Partner
import lucuma.core.model.Proposal
import lucuma.core.model.ProposalClass.LargeProgram

import java.time.Duration
import scala.collection.immutable.SortedMap

class ProposalDecodersSuite extends InputStreamSuite {
  test("Proposal decoder") {
    val expected =
      Proposal(
        title = NonEmptyString("Classy Proposal").some,
        proposalClass = LargeProgram(77, 88, NonNegDuration.unsafeFrom(Duration.ofNanos(660000))),
        category = TacCategory.ExoplanetHostStar.some,
        toOActivation = ToOActivation.Standard,
        abstrakt = none,
        partnerSplits = SortedMap((Partner.Uh, IntPercent(40)), (Partner.Cl, IntPercent(60)))
      )
    assertParsedStreamEquals("/proposal1.json", expected)
  }
}
