// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.math.Coordinates
import lucuma.core.math.Epoch
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.model.AngularSize
import lucuma.core.model.CatalogInfo
import lucuma.core.model.EphemerisKey
import lucuma.core.model.SiderealTracking
import lucuma.core.model.SourceProfile
import lucuma.core.model.Target

trait TargetDecoders {

  implicit val ephemerisKeyDecoder: Decoder[EphemerisKey] = semiauto.deriveDecoder

  implicit val siderealTrackingDecoder: Decoder[SiderealTracking] = Decoder.instance(c =>
    for {
      bc  <- c.downField("coordinates").as[Coordinates]
      ep  <- c.downField("epoch").as[Epoch]
      pm  <- c.downField("properMotion").as[Option[ProperMotion]]
      rv  <- c.downField("radialVelocity").as[Option[RadialVelocity]]
      par <- c.downField("parallax").as[Option[Parallax]]
    } yield SiderealTracking(bc, ep, pm, rv, par)
  )

  implicit val catalogInfoeDecoder: Decoder[CatalogInfo] = semiauto.deriveDecoder

  implicit val angularSizeDecoder: Decoder[AngularSize] = semiauto.deriveDecoder

  implicit val siderealTargetDecoder: Decoder[Target.Sidereal] = Decoder.instance(c =>
    for {
      name          <- c.downField("name").as[NonEmptyString]
      tracking      <- c.downField("tracking").as[SiderealTracking]
      sourceProfile <- c.downField("sourceProfile").as[SourceProfile]
      catalogInfo   <- c.downField("catalogInfo").as[Option[CatalogInfo]]
      angSize       <- c.downField("angularSize").as[Option[AngularSize]]
    } yield Target.Sidereal(name, tracking, sourceProfile, catalogInfo, angSize)
  )

  implicit val nonsiderealTargetDecoder: Decoder[Target.Nonsidereal] = Decoder.instance(c =>
    for {
      name          <- c.downField("name").as[NonEmptyString]
      ephemerisKey  <- c.downField("ephemerisKey").as[EphemerisKey]
      sourceProfile <- c.downField("sourceProfile").as[SourceProfile]
      angSize       <- c.downField("angularSize").as[Option[AngularSize]]
    } yield Target.Nonsidereal(name, ephemerisKey, sourceProfile, angSize)
  )

  implicit val targetDecoder: Decoder[Target] =
    List[Decoder[Target]](
      Decoder[Target.Sidereal].widen,
      Decoder[Target.Nonsidereal].widen
    ).reduceLeft(_ or _)
}
