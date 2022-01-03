// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import coulomb._
import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.math.Angle
import lucuma.core.math.Coordinates
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.math.RightAscension
import lucuma.core.math.Wavelength
import lucuma.core.math.dimensional._
import lucuma.core.math.units.CentimetersPerSecond

import java.time.Duration
import java.time.temporal.ChronoUnit

trait CoreModelDecoders {

  implicit def quantityDecoder[N: Decoder, U]: Decoder[Quantity[N, U]] =
    Decoder.instance(_.as[N].map(_.withUnit[U]))

  implicit def taggedMeasureDecoder[N: Decoder, T](implicit
    unitDecoder: Decoder[Units Of T]
  ): Decoder[Measure[N] Of T] =
    Decoder.instance(c =>
      for {
        v <- c.downField("value").as[N]
        u <- c.downField("units").as[Units Of T]
      } yield u.withValueTagged(v)
    )

  implicit val epochDecoder: Decoder[Epoch] =
    Decoder.decodeString.emap(e =>
      Epoch.fromString.getOption(e).toRight(s"Invalid epoch value: $e")
    )

  val rvmsDecoder: Decoder[RadialVelocity] =
    Decoder.decodeBigDecimal.emap(x =>
      RadialVelocity(x.withUnit[CentimetersPerSecond]).toRight(s"Invalid radial velocity $x")
    )

  implicit val rvDecoder: Decoder[RadialVelocity] =
    Decoder.instance(_.downField("centimetersPerSecond").as[RadialVelocity](rvmsDecoder))

  val pxµasDecoder: Decoder[Parallax] =
    Decoder.decodeLong.map(Parallax.fromMicroarcseconds)

  implicit val pxDecoder: Decoder[Parallax] =
    Decoder.instance(_.downField("microarcseconds").as[Parallax](pxµasDecoder))

  implicit val angleDecoder: Decoder[Angle] = Decoder.instance(
    _.downField("microarcseconds").as[Long].map(Angle.microarcseconds.reverseGet)
  )

  implicit val raDecoder: Decoder[RightAscension] =
    Decoder.instance(
      _.as[Angle].map(
        (RightAscension.fromAngleExact.getOption _).map(_.getOrElse(RightAscension.Zero))
      )
    )

  implicit val decDecoder: Decoder[Declination] =
    Decoder
      .instance(
        _.as[Angle]
          .map(Declination.fromAngle.getOption _)
      )
      .emap(_.toRight("Invalid µarcsec value for declination"))

  implicit val coordDecoder: Decoder[Coordinates] = semiauto.deriveDecoder

  val pmraµasDecoder: Decoder[ProperMotion.RA] =
    Decoder.decodeLong
      .map(ProperMotion.RA.microarcsecondsPerYear.reverseGet)

  implicit val pmraDecoder: Decoder[ProperMotion.RA] =
    Decoder.instance(_.downField("microarcsecondsPerYear").as[ProperMotion.RA](pmraµasDecoder))

  val pmdecµasDecoder: Decoder[ProperMotion.Dec] =
    Decoder.decodeLong
      .map(ProperMotion.Dec.microarcsecondsPerYear.reverseGet)

  implicit val pmdecDecoder: Decoder[ProperMotion.Dec] =
    Decoder.instance(_.downField("microarcsecondsPerYear").as[ProperMotion.Dec](pmdecµasDecoder))

  implicit val pmDecoder: Decoder[ProperMotion] = semiauto.deriveDecoder[ProperMotion]

  implicit val durationDecoder: Decoder[Duration] = Decoder.instance(
    _.downField("microseconds")
      .as[Long]
      .map(l => Duration.of(l, ChronoUnit.MICROS))
  )

  implicit val wavelengthDecoder: Decoder[Wavelength] = Decoder.instance(
    _.downField("picometers").as[PosInt].map(Wavelength.apply)
  )
}
