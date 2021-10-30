// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas

import cats.syntax.all._
import coulomb._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{ Decoder, DecodingFailure, HCursor }
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.`enum`.{ MagnitudeBand, MagnitudeSystem }
import lucuma.core.math.{
  Angle,
  Coordinates,
  Declination,
  Epoch,
  MagnitudeValue,
  Parallax,
  ProperMotion,
  RadialVelocity,
  RightAscension,
  Wavelength
}
import lucuma.core.math.units.CentimetersPerSecond
import lucuma.core.model.{
  EphemerisKey,
  Magnitude,
  NonsiderealTarget,
  SiderealTarget,
  SiderealTracking,
  Target
}

import java.time.Duration
import java.time.temporal.ChronoUnit
import scala.collection.immutable.SortedMap

object decoders {
  implicit val epochDecoder: Decoder[Epoch] =
    Decoder.decodeString.emap(e =>
      Epoch.fromString.getOption(e).toRight(s"Invalid epoch value: $e")
    )

  val rvmsDecoder: Decoder[RadialVelocity] =
    Decoder.decodeBigDecimal.emap(x =>
      RadialVelocity(x.withUnit[CentimetersPerSecond]).toRight(s"Invalid radial velocity $x")
    )

  implicit val rvDecoder: Decoder[RadialVelocity] = new Decoder[RadialVelocity] {
    final def apply(c: HCursor): Decoder.Result[RadialVelocity] =
      c.downField("centimetersPerSecond").as[RadialVelocity](rvmsDecoder)
  }

  val pxµasDecoder: Decoder[Parallax] =
    Decoder.decodeLong.map(Parallax.fromMicroarcseconds)

  implicit val pxDecoder: Decoder[Parallax] = new Decoder[Parallax] {
    final def apply(c: HCursor): Decoder.Result[Parallax] =
      c.downField("microarcseconds").as[Parallax](pxµasDecoder)
  }

  val raµasDecoder: Decoder[RightAscension] =
    Decoder.decodeLong
      .map(
        (RightAscension.fromAngleExact.getOption _).compose(Angle.fromMicroarcseconds _)
      )
      .map(_.getOrElse(RightAscension.Zero))

  implicit val raDecoder: Decoder[RightAscension] = new Decoder[RightAscension] {
    final def apply(c: HCursor): Decoder.Result[RightAscension] =
      c.downField("microarcseconds").as[RightAscension](raµasDecoder)
  }

  val decµasDecoder: Decoder[Declination] =
    Decoder.decodeLong
      .map(
        (Declination.fromAngle.getOption _).compose(Angle.fromMicroarcseconds _)
      )
      .emap(_.toRight("Invalid µarcsec value for declination"))

  implicit val decDecoder: Decoder[Declination] = new Decoder[Declination] {
    final def apply(c: HCursor): Decoder.Result[Declination] =
      c.downField("microarcseconds").as[Declination](decµasDecoder)
  }

  implicit val coordDecoder: Decoder[Coordinates] = semiauto.deriveDecoder[Coordinates]

  val pmraµasDecoder: Decoder[ProperMotion.RA] =
    Decoder.decodeLong
      .map(ProperMotion.RA.microarcsecondsPerYear.reverseGet)

  implicit val pmraDecoder: Decoder[ProperMotion.RA] = new Decoder[ProperMotion.RA] {
    final def apply(c: HCursor): Decoder.Result[ProperMotion.RA] =
      c.downField("microarcsecondsPerYear").as[ProperMotion.RA](pmraµasDecoder)
  }

  val pmdecµasDecoder: Decoder[ProperMotion.Dec] =
    Decoder.decodeLong
      .map(ProperMotion.Dec.microarcsecondsPerYear.reverseGet)

  implicit val pmdecDecoder: Decoder[ProperMotion.Dec] = new Decoder[ProperMotion.Dec] {
    final def apply(c: HCursor): Decoder.Result[ProperMotion.Dec] =
      c.downField("microarcsecondsPerYear").as[ProperMotion.Dec](pmdecµasDecoder)
  }

  implicit val pmDecoder: Decoder[ProperMotion] = semiauto.deriveDecoder[ProperMotion]

  implicit val siderealTrackingDecoder = new Decoder[SiderealTracking] {
    final def apply(c: HCursor): Decoder.Result[SiderealTracking] =
      for {
        bc  <- c.downField("coordinates").as[Coordinates]
        ep  <- c.downField("epoch").as[Epoch]
        pm  <- c.downField("properMotion").as[Option[ProperMotion]]
        rv  <- c.downField("radialVelocity").as[Option[RadialVelocity]]
        par <- c.downField("parallax").as[Option[Parallax]]
      } yield SiderealTracking(none, bc, ep, pm, rv, par)
  }

  implicit val magnitudeValueDecoder: Decoder[MagnitudeValue] = new Decoder[MagnitudeValue] {
    final def apply(c: HCursor): Decoder.Result[MagnitudeValue] =
      c.as[BigDecimal]
        .map(MagnitudeValue.fromBigDecimal.getOption)
        .flatMap(_.toRight(DecodingFailure("Invalid MagnitudeValue", c.history)))
  }

  implicit val magnitudeDecoder: Decoder[Magnitude] = new Decoder[Magnitude] {
    final def apply(c: HCursor): Decoder.Result[Magnitude] =
      for {
        v <- c.downField("value").as[MagnitudeValue]
        b <- c.downField("band").as[MagnitudeBand]
        e <- c.downField("error").as[Option[MagnitudeValue]]
        s <- c.downField("system").as[MagnitudeSystem]
      } yield Magnitude(v, b, e, s)
  }

  implicit val durationDecoder: Decoder[Duration] = Decoder.instance(
    _.downField("microseconds")
      .as[Long]
      .map(l => Duration.of(l, ChronoUnit.MICROS))
  )

  implicit val wavelengthDecoder: Decoder[Wavelength] = Decoder.instance(
    _.downField("picometers").as[PosInt].map(Wavelength.apply)
  )

  implicit val ephemerisKeyDecoder: Decoder[EphemerisKey] = semiauto.deriveDecoder

  implicit val siderealTargetDecoder: Decoder[SiderealTarget] = Decoder.instance(c =>
    for {
      name       <- c.downField("name").as[NonEmptyString]
      tracking   <- c.downField("tracking").as[SiderealTracking]
      magnitudes <- c.downField("magnitudes")
                      .as[List[Magnitude]]
                      .map(mags => SortedMap(mags.map(mag => mag.band -> mag): _*))
    } yield SiderealTarget(name, tracking, magnitudes)
  )

  implicit val nonsiderealTargetDecoder: Decoder[NonsiderealTarget] = Decoder.instance(c =>
    for {
      name         <- c.downField("name").as[NonEmptyString]
      ephemerisKey <- c.downField("ephemerisKey").as[EphemerisKey]
      magnitudes   <- c.downField("magnitudes")
                        .as[List[Magnitude]]
                        .map(mags => SortedMap(mags.map(mag => mag.band -> mag): _*))
    } yield NonsiderealTarget(name, ephemerisKey, magnitudes)
  )

  implicit val targetDecoder: Decoder[Target] =
    List[Decoder[Target]](
      Decoder[SiderealTarget].widen,
      Decoder[NonsiderealTarget].widen
    ).reduceLeft(_ or _)

}
