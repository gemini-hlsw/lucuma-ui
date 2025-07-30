// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.enums

import io.circe.*
import io.circe.generic.semiauto
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import monocle.Focus
import monocle.Lens

case class ProposalStatus(tag: String, name: String)

object ProposalStatus {
  given Display[ProposalStatus] = Display.byShortName(_.name)

  val tag: Lens[ProposalStatus, String]  = Focus[ProposalStatus](_.tag)
  val name: Lens[ProposalStatus, String] = Focus[ProposalStatus](_.name)

  lazy val NotSubmitted: ProposalStatus = Enumerated[ProposalStatus].unsafeFromTag("NOT_SUBMITTED")
  lazy val Submitted: ProposalStatus    = Enumerated[ProposalStatus].unsafeFromTag("SUBMITTED")
  lazy val Accepted: ProposalStatus     = Enumerated[ProposalStatus].unsafeFromTag("ACCEPTED")
  lazy val NotAccepted: ProposalStatus  = Enumerated[ProposalStatus].unsafeFromTag("NOT_ACCEPTED")

  given Enumerated[ProposalStatus] =
    DynamicEnums.enumeratedInstance[ProposalStatus]("proposalStatusMeta", _.tag)(using
      semiauto.deriveDecoder
    )
}
