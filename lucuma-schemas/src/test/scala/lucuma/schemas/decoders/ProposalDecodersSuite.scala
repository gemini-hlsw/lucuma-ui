// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.enums.TacCategory
import lucuma.core.enums.ToOActivation
import lucuma.core.model.IntPercent
import lucuma.core.model.NonNegDuration
import lucuma.core.model.Partner
import lucuma.core.model.Proposal
import lucuma.core.model.ProposalClass.LargeProgram
import lucuma.core.model.ZeroTo100
import lucuma.refined._

import java.time.Duration
import scala.collection.immutable.SortedMap

class ProposalDecodersSuite extends InputStreamSuite {

  test("Proposal decoder") {
    val expected =
      Proposal(
        title = "Classy Proposal".refined[NonEmpty].some,
        proposalClass =
          LargeProgram(77.refined, 88.refined, NonNegDuration.unsafeFrom(Duration.ofNanos(660000))),
        category = TacCategory.ExoplanetHostStar.some,
        toOActivation = ToOActivation.Standard,
        abstrakt = None,
        partnerSplits =
          SortedMap((Partner.Uh, 40.refined[ZeroTo100]), (Partner.Cl, 60.refined[ZeroTo100]))
      )
    assertParsedStreamEquals("/proposal1.json", expected)
  }
}
