// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.syntax.all._
import coulomb._
import eu.timepit.refined.types.numeric.PosBigDecimal
import io.circe.Decoder
import io.circe.refined._
import lucuma.core.enum.Band
import lucuma.core.math.BrightnessUnits._
import lucuma.core.math.BrightnessValue
import lucuma.core.math.Wavelength
import lucuma.core.math.dimensional._
import lucuma.core.math.units._
import lucuma.core.model.EmissionLine
import lucuma.core.model.SpectralDefinition
import lucuma.core.model.UnnormalizedSED

import scala.collection.immutable.SortedMap

trait SpectralDefinitionDecoders {

  implicit val brightnessValueDecoder: Decoder[BrightnessValue] =
    Decoder.instance(_.as[BigDecimal].map(BrightnessValue.fromBigDecimal.get))

  implicit def bandNormalizedSpectralDefinitionDecoder[T](implicit
    unitDecoder: Decoder[Units Of Brightness[T]]
  ): Decoder[SpectralDefinition.BandNormalized[T]] = {
    implicit val brightnessEntryDecoder: Decoder[(Band, BrightnessMeasure[T])] =
      Decoder.instance(c =>
        for {
          v <- c.as[BrightnessMeasure[T]]
          b <- c.downField("band").as[Band]
          e <- c.downField("error").as[Option[BrightnessValue]]
        } yield (b, Measure.errorTagged.replace(e)(v))
      )

    Decoder.instance(c =>
      for {
        sed          <- c.downField("sed").as[UnnormalizedSED]
        brightnesses <- c.downField("brightnesses").as[List[(Band, BrightnessMeasure[T])]]
      } yield SpectralDefinition.BandNormalized(sed, SortedMap.from(brightnesses))
    )
  }

  implicit def emissionLineDecoder[T](implicit
    unitDecoder: Decoder[Units Of LineFlux[T]]
  ): Decoder[EmissionLine[T]] =
    Decoder.instance(c =>
      for {
        lw <- c.downField("lineWidth").as[Quantity[PosBigDecimal, KilometersPerSecond]]
        lf <- c.downField("lineFlux").as[Measure[PosBigDecimal] Of LineFlux[T]]
      } yield EmissionLine[T](lw, lf)
    )

  implicit def emissionLinesSpectralDefinitionDecoder[T](implicit
    lineFluxUnitDecoder:  Decoder[Units Of LineFlux[T]],
    continuumUnitDecoder: Decoder[Units Of FluxDensityContinuum[T]]
  ): Decoder[SpectralDefinition.EmissionLines[T]] = Decoder.instance { c =>
    implicit val emissionLineEntryDecoder: Decoder[(Wavelength, EmissionLine[T])] =
      Decoder.instance(c =>
        for {
          w <- c.downField("wavelength").as[Wavelength]
          v <- c.downField("value").as[EmissionLine[T]]
        } yield (w, v)
      )

    for {
      ls  <- c.downField("lines").as[List[(Wavelength, EmissionLine[T])]]
      fdc <-
        c.downField("fluxDensityContinuum").as[Measure[PosBigDecimal] Of FluxDensityContinuum[T]]
    } yield SpectralDefinition.EmissionLines(SortedMap.from(ls), fdc)
  }

  def spectralDefinitionDecoder[T](implicit
    brightnessUnitDecoder: Decoder[Units Of Brightness[T]],
    lineFluxUnitDecoder:   Decoder[Units Of LineFlux[T]],
    continuumUnitDecoder:  Decoder[Units Of FluxDensityContinuum[T]]
  ): Decoder[SpectralDefinition[T]] =
    List[Decoder[SpectralDefinition[T]]](
      Decoder[SpectralDefinition.BandNormalized[T]].widen,
      Decoder[SpectralDefinition.EmissionLines[T]].widen
    ).reduceLeft(_ or _)

  // Pre-built spectralDefinitionDecoder instances for Integrated and Surface
  implicit val integratedSpectralDefinitionDecoder: Decoder[SpectralDefinition[Integrated]] =
    spectralDefinitionDecoder[Integrated]

  implicit val surfaceSpectralDefinitionDecoder: Decoder[SpectralDefinition[Surface]] =
    spectralDefinitionDecoder[Surface]
}
