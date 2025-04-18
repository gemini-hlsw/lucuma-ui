// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined.*
import lucuma.core.model.ExposureTimeMode
import lucuma.odb.json.time.decoder.given
import lucuma.odb.json.wavelength.decoder.given

trait ExposureTimeModeDecoders {

  given Decoder[ExposureTimeMode.SignalToNoiseMode] =
    semiauto.deriveDecoder

  given Decoder[ExposureTimeMode.TimeAndCountMode] =
    semiauto.deriveDecoder

  given Decoder[ExposureTimeMode] = Decoder.instance { c =>
    c.downField("signalToNoise")
      .as[ExposureTimeMode.SignalToNoiseMode]
      .orElse(c.downField("timeAndCount").as[ExposureTimeMode.TimeAndCountMode])
  }
}
