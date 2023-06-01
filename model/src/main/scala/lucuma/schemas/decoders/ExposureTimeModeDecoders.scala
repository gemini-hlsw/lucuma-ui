// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.model.ExposureTimeMode
import lucuma.odb.json.time.decoder.given

trait ExposureTimeModeDecoders {

  implicit val decoderSignalToNoise: Decoder[ExposureTimeMode.SignalToNoiseMode] =
    semiauto.deriveDecoder

  implicit val decoderFixedExposure: Decoder[ExposureTimeMode.FixedExposureMode] =
    semiauto.deriveDecoder

  implicit val decoderExposureTimeMode: Decoder[ExposureTimeMode] = Decoder.instance { c =>
    c.downField("signalToNoise")
      .as[ExposureTimeMode.SignalToNoiseMode]
      .orElse(c.downField("fixedExposure").as[ExposureTimeMode.FixedExposureMode])
  }
}
