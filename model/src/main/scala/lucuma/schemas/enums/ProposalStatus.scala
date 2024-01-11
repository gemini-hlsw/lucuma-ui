// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.enums

import cats.Eq
import cats.derived.*
import io.circe.*
import io.circe.generic.semiauto
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import monocle.Focus
import monocle.Lens

case class ProposalStatus(tag: String, name: String) derives Eq

object ProposalStatus {
  given Display[ProposalStatus] = Display.byShortName(_.name)

  val tag: Lens[ProposalStatus, String]  = Focus[ProposalStatus](_.tag)
  val name: Lens[ProposalStatus, String] = Focus[ProposalStatus](_.name)

  lazy val NotSubmitted: ProposalStatus = Enumerated[ProposalStatus].unsafeFromTag("NOT_SUBMITTED")
  lazy val Submitted: ProposalStatus    = Enumerated[ProposalStatus].unsafeFromTag("SUBMITTED")

  // The givens are apparently (probably) constructed lazily.
  // See https://alexn.org/blog/2022/05/11/implicit-vs-scala-3-given/
  // We want to fail immediately if there is a problem, so we'll reference
  // the enumerated givens here.
  Enumerated[ProposalStatus]

  given Enumerated[ProposalStatus] =
    DynamicEnums.enumeratedInstance[ProposalStatus]("proposalStatusMeta", _.tag)(using
      semiauto.deriveDecoder
    )
}
