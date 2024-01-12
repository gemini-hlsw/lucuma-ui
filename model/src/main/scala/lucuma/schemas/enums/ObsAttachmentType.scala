// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.enums

import cats.Eq
import cats.derived.*
import io.circe.*
import io.circe.generic.semiauto
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.core.util.NewType
import monocle.Focus
import monocle.Lens

object FileExtension extends NewType[String]
type FileExtension = FileExtension.Type

case class ObsAttachmentType(
  tag:            String,
  shortName:      String,
  longName:       String,
  fileExtensions: List[FileExtension]
) derives Eq:
  def accept: String = fileExtensions.map("." + _.value).mkString(",")

object ObsAttachmentType:
  given Display[ObsAttachmentType] = Display.by(_.shortName, _.longName)

  val tag: Lens[ObsAttachmentType, String]                         = Focus[ObsAttachmentType](_.tag)
  val shortName: Lens[ObsAttachmentType, String]                   = Focus[ObsAttachmentType](_.shortName)
  val longName: Lens[ObsAttachmentType, String]                    = Focus[ObsAttachmentType](_.longName)
  val fileExtensions: Lens[ObsAttachmentType, List[FileExtension]] =
    Focus[ObsAttachmentType](_.fileExtensions)

  def Finder(using e: Enumerated[ObsAttachmentType]): ObsAttachmentType = e.unsafeFromTag("FINDER")

  given Enumerated[ObsAttachmentType] = {
    given Decoder[FileExtension] = Decoder.instance: c =>
      c.downField("fileExtension").as[String].map(FileExtension(_))
    DynamicEnums.enumeratedInstance[ObsAttachmentType]("obsAttachmentTypeMeta", _.tag)(using
      semiauto.deriveDecoder
    )
  }
