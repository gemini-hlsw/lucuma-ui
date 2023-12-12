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

case class ProposalAttachmentType(
  tag:       String,
  shortName: String,
  longName:  String
) derives Eq

object ProposalAttachmentType:
  val accept: String = ".pdf" // only PDF files are currently allowed

  given Display[ProposalAttachmentType] = Display.by(_.shortName, _.longName)

  val tag: Lens[ProposalAttachmentType, String]       = Focus[ProposalAttachmentType](_.tag)
  val shortName: Lens[ProposalAttachmentType, String] = Focus[ProposalAttachmentType](_.shortName)
  val longName: Lens[ProposalAttachmentType, String]  = Focus[ProposalAttachmentType](_.longName)

  val values: List[ProposalAttachmentType] =
    // This is a meta decoder, not a decoder for enum instances (which comes from the `Enumerated` instance)
    given Decoder[ProposalAttachmentType] = semiauto.deriveDecoder

    DynamicEnums.parsedEnums
      .downField("proposalAttachmentTypeMeta")
      .as[List[ProposalAttachmentType]] match
      case Left(err)   => err.printStackTrace; throw err
      case Right(json) => json

  // The givens are apparently (probably) constructed lazily.
  // See https://alexn.org/blog/2022/05/11/implicit-vs-scala-3-given/
  // We want to fail immediately if there is a problem, so we'll reference
  // the enumerated givens here.
  Enumerated[ProposalAttachmentType]

  given Enumerated[ProposalAttachmentType] =
    Enumerated.from(values.head, values.tail: _*).withTag(_.tag)
