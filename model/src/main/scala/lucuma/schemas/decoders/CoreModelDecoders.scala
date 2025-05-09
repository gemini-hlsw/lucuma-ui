// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Encoder
import io.circe.refined.*
import lucuma.core.math.WavelengthDelta
import lucuma.core.math.WavelengthDither
import lucuma.core.math.dimensional.*
import lucuma.core.model.Semester
import lucuma.core.util.*

trait CoreModelDecoders:
  given taggedMeasureDecoder[N: Decoder, T](using
    unitDecoder: Decoder[Units Of T]
  ): Decoder[Measure[N] Of T] =
    Decoder.instance: c =>
      for
        v <- c.downField("value").as[N]
        u <- c.downField("units").as[Units Of T]
      yield u.withValueTagged(v)

  given Decoder[WavelengthDither] = Decoder.instance:
    _.downField("picometers").as[Int].map(WavelengthDither.intPicometers.get)

  given Decoder[WavelengthDelta] = Decoder.instance(
    _.downField("picometers").as[PosInt].map(WavelengthDelta.apply)
  )

  // TODO: This needs to be to a common location for schemas and ODB. Currently it is private within the ODB
  given Decoder[Semester] =
    Decoder.instance:
      _.as[String].flatMap: s =>
        Semester.fromString.getOption(s).toRight(DecodingFailure(s"Invalid Semester `$s`", List()))

  given Encoder[Semester] =
    Encoder.encodeString.contramap[Semester](_.format)

  given Decoder[TimestampInterval] = Decoder.instance: c =>
    for
      start <- c.downField("start").as[Timestamp]
      end   <- c.downField("end").as[Timestamp]
    yield TimestampInterval.between(start, end)

  given calculatedValueDecoder[A: Decoder]: Decoder[CalculatedValue[A]] =
    Decoder.instance: c =>
      for
        s <- c.downField("state").as[CalculationState]
        v <- c.downField("value").as[A]
      yield CalculatedValue(s, v)
