// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import algebra.instances.all.given
import cats.syntax.all._
import coulomb._
import coulomb.policy.spire.standard.given
import coulomb.syntax._
import eu.timepit.refined.types.numeric.NonNegLong
import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.math.Angle
import lucuma.core.math.Axis
import lucuma.core.math.Coordinates
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.Offset
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.math.RightAscension
import lucuma.core.math.Wavelength
import lucuma.core.math.WavelengthDelta
import lucuma.core.math.WavelengthDither
import lucuma.core.math.dimensional._
import lucuma.core.math.units.CentimetersPerSecond
import lucuma.core.math.units.MetersPerSecond
import lucuma.core.model.NonNegDuration
import lucuma.core.optics.Format
import lucuma.core.util.*
import lucuma.odb.json.angle.decoder.given

import java.time.Duration
import java.time.temporal.ChronoUnit

trait CoreModelDecoders {
  given quantityDecoder[N: Decoder, U]: Decoder[Quantity[N, U]] =
    Decoder.instance(_.as[N].map(_.withUnit[U]))

  given taggedMeasureDecoder[N: Decoder, T](using
    unitDecoder: Decoder[Units Of T]
  ): Decoder[Measure[N] Of T] =
    Decoder.instance(c =>
      for {
        v <- c.downField("value").as[N]
        u <- c.downField("units").as[Units Of T]
      } yield u.withValueTagged(v)
    )

  given epochDecoder: Decoder[Epoch] =
    Decoder.decodeString.emap(e =>
      Epoch.fromString.getOption(e).toRight(s"Invalid epoch value: $e")
    )

  private val rvmsDecoder: Decoder[RadialVelocity] =
    Decoder.decodeBigDecimal.emap(x =>
      RadialVelocity(x.withUnit[CentimetersPerSecond].toUnit[MetersPerSecond])
        .toRight(s"Invalid radial velocity $x")
    )

  given rvDecoder: Decoder[RadialVelocity] =
    Decoder.instance(_.downField("centimetersPerSecond").as[RadialVelocity](rvmsDecoder))

  private val pxµasDecoder: Decoder[Parallax] =
    Decoder.decodeLong.map(Parallax.fromMicroarcseconds)

  given pxDecoder: Decoder[Parallax] =
    Decoder.instance(_.downField("microarcseconds").as[Parallax](pxµasDecoder))

  given raDecoder: Decoder[RightAscension] =
    Decoder.instance(
      _.as[Angle].map(
        (RightAscension.fromAngleExact.getOption _).map(_.getOrElse(RightAscension.Zero))
      )
    )

  given decDecoder: Decoder[Declination] =
    Decoder
      .instance(
        _.as[Angle]
          .map(Declination.fromAngle.getOption _)
      )
      .emap(_.toRight("Invalid µarcsec value for declination"))

  given Decoder[Coordinates] = semiauto.deriveDecoder

  private val pmraµasDecoder: Decoder[ProperMotion.RA] =
    Decoder.decodeLong
      .map(ProperMotion.RA.microarcsecondsPerYear.get)

  given Decoder[ProperMotion.RA] =
    Decoder.instance(_.downField("microarcsecondsPerYear").as[ProperMotion.RA](pmraµasDecoder))

  private val pmdecµasDecoder: Decoder[ProperMotion.Dec] =
    Decoder.decodeLong
      .map(ProperMotion.Dec.microarcsecondsPerYear.get)

  given Decoder[ProperMotion.Dec] =
    Decoder.instance(_.downField("microarcsecondsPerYear").as[ProperMotion.Dec](pmdecµasDecoder))

  given Decoder[ProperMotion] = semiauto.deriveDecoder[ProperMotion]

  given Decoder[WavelengthDither] = Decoder.instance(
    _.downField("picometers").as[Int].map(WavelengthDither.intPicometers.get)
  )

  given Decoder[WavelengthDelta] = Decoder.instance(
    _.downField("picometers").as[PosInt].map(WavelengthDelta.apply)
  )
}
