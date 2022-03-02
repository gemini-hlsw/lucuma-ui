// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import io.circe.Decoder
import io.circe.HCursor
import lucuma.core.math.Angle
import lucuma.core.math.BrightnessUnits._
import lucuma.core.model.SourceProfile
import lucuma.core.model.SpectralDefinition

trait SourceProfileDecoders {

  implicit val pointSourceProfileDecoder: Decoder[SourceProfile.Point] =
    Decoder.instance(
      _.downField("point")
        .as[SpectralDefinition[Integrated]]
        .map(SourceProfile.Point.apply)
    )

  implicit val uniformSourceProfileDecoder: Decoder[SourceProfile.Uniform] =
    Decoder.instance(
      _.downField("uniform")
        .as[SpectralDefinition[Surface]]
        .map(SourceProfile.Uniform.apply)
    )

  implicit val gaussianSourceProfileDecoder: Decoder[SourceProfile.Gaussian] =
    Decoder.instance(c =>
      for {
        g    <- c.downField("gaussian").as[HCursor]
        fwhm <- g.downField("fwhm").as[Angle]
        s    <- g.as[SpectralDefinition[Integrated]]
      } yield SourceProfile.Gaussian(fwhm, s)
    )

  implicit val sourceProfileDecoder: Decoder[SourceProfile] = List[Decoder[SourceProfile]](
    Decoder[SourceProfile.Point].widen,
    Decoder[SourceProfile.Uniform].widen,
    Decoder[SourceProfile.Gaussian].widen
  ).reduceLeft(_ or _)
}
