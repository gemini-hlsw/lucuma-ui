// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.circe.HCursor
import io.circe.refined._
import lucuma.core.enums.CatalogName
import lucuma.core.math.Coordinates
import lucuma.core.math.Epoch
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.model.CatalogInfo
import lucuma.core.model.EphemerisKey
import lucuma.core.model.SiderealTracking
import lucuma.core.model.SourceProfile
import lucuma.core.model.Target

trait TargetDecoders {

  implicit val siderealTrackingDecoder: Decoder[SiderealTracking] = Decoder.instance(c =>
    for {
      bc  <- c.as[Coordinates]
      ep  <- c.downField("epoch").as[Epoch]
      pm  <- c.downField("properMotion").as[Option[ProperMotion]]
      rv  <- c.downField("radialVelocity").as[Option[RadialVelocity]]
      par <- c.downField("parallax").as[Option[Parallax]]
    } yield SiderealTracking(bc, ep, pm, rv, par)
  )

  implicit val catalogInfoeDecoder: Decoder[CatalogInfo] = Decoder.instance(c =>
    for {
      name <- c.downField("name").as[CatalogName]
      id   <- c.downField("id").as[NonEmptyString]
      ot   <- c.downField("objectType").as[Option[NonEmptyString]]
    } yield CatalogInfo(name, id, ot)
  )

  implicit val siderealTargetDecoder: Decoder[Target.Sidereal] = Decoder.instance(c =>
    for {
      name          <- c.downField("name").as[NonEmptyString]
      sourceProfile <- c.downField("sourceProfile").as[SourceProfile]
      s             <- c.downField("sidereal").as[HCursor]
      tracking      <- s.as[SiderealTracking]
      catalogInfo   <- s.downField("catalogInfo").as[Option[CatalogInfo]]
    } yield Target.Sidereal(name, tracking, sourceProfile, catalogInfo)
  )

  implicit val nonsiderealTargetDecoder: Decoder[Target.Nonsidereal] = Decoder.instance(c =>
    for {
      name          <- c.downField("name").as[NonEmptyString]
      ephemerisKey  <- c.downField("nonsidereal").downField("key").as[EphemerisKey]
      sourceProfile <- c.downField("sourceProfile").as[SourceProfile]
    } yield Target.Nonsidereal(name, ephemerisKey, sourceProfile)
  )

  implicit val targetDecoder: Decoder[Target] =
    List[Decoder[Target]](
      Decoder[Target.Sidereal].widen,
      Decoder[Target.Nonsidereal].widen
    ).reduceLeft(_ or _)
}
