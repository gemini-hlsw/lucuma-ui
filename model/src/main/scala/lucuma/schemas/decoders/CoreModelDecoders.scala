// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import coulomb._
import coulomb.syntax._
import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.refined._
import lucuma.core.math.WavelengthDelta
import lucuma.core.math.WavelengthDither
import lucuma.core.math.dimensional._
import lucuma.core.util.*

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

  given Decoder[WavelengthDither] = Decoder.instance(
    _.downField("picometers").as[Int].map(WavelengthDither.intPicometers.get)
  )

  given Decoder[WavelengthDelta] = Decoder.instance(
    _.downField("picometers").as[PosInt].map(WavelengthDelta.apply)
  )
}
