// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.effect._
import cats.syntax.all._
import coulomb._
import eu.timepit.refined.types.numeric.PosBigDecimal
import eu.timepit.refined.types.numeric.PosLong
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.parser._
import lucuma.core.enum.Band
import lucuma.core.enum.CatalogName
import lucuma.core.enum.GalaxySpectrum
import lucuma.core.math.BrightnessUnits._
import lucuma.core.math.BrightnessValue
import lucuma.core.math.Coordinates
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.math.RightAscension
import lucuma.core.math.Wavelength
import lucuma.core.math.dimensional._
import lucuma.core.math.units._
import lucuma.core.model.CatalogInfo
import lucuma.core.model.EmissionLine
import lucuma.core.model.SiderealTracking
import lucuma.core.model.SourceProfile
import lucuma.core.model.SpectralDefinition
import lucuma.core.model.Target
import lucuma.core.model.UnnormalizedSED
import munit._

import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import scala.collection.immutable.SortedMap

class DecodersSuite extends CatsEffectSuite {

  protected def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO.blocking(new FileInputStream(f)) // build
    } { inStream =>
      IO.blocking(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  test("Target decoder - Point - BandNormalized") {
    val expectedId     = Target.Id(PosLong(2))
    val expectedTarget =
      Target.Sidereal(
        NonEmptyString("NGC 5949"),
        SiderealTracking(
          Coordinates(RightAscension.fromStringHMS.getOption("15:28:00.668").get,
                      Declination.fromStringSignedDMS.getOption("+64:45:47.4").get
          ),
          Epoch.J2000,
          ProperMotion.Zero.some,
          RadialVelocity.fromMetersPerSecond.getOption(BigDecimal(423607)),
          Parallax.Zero.some
        ),
        SourceProfile.Point(
          SpectralDefinition.BandNormalized(
            UnnormalizedSED.Galaxy(GalaxySpectrum.Spiral),
            SortedMap(
              Band.SloanU ->
                BrightnessValue
                  .fromDouble(14.147)
                  .withUnit[ABMagnitude]
                  .toMeasureTagged
                  .withError(BrightnessValue.fromDouble(0.005)),
              Band.SloanG -> BrightnessValue
                .fromDouble(12.924)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.002)),
              Band.SloanR -> BrightnessValue
                .fromDouble(12.252)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.002)),
              Band.SloanI -> BrightnessValue
                .fromDouble(11.888)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.002)),
              Band.SloanZ -> BrightnessValue
                .fromDouble(11.636)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.002)),
              Band.B      -> BrightnessValue.fromDouble(12.7).withUnit[VegaMagnitude].toMeasureTagged,
              Band.J      -> BrightnessValue
                .fromDouble(10.279)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.001)),
              Band.H      -> BrightnessValue
                .fromDouble(9.649)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.012)),
              Band.K      -> BrightnessValue
                .fromDouble(9.425)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.017))
            )
          )
        ),
        CatalogInfo(CatalogName.Simbad, NonEmptyString("M   1"), NonEmptyString("SNR").some).some
      )

    val jsonFile = "/t2.json"
    val url      = getClass().getResource(jsonFile)
    val file     = Paths.get(url.toURI()).toFile()
    inputStream(file).use { inStream =>
      for {
        str      <- IO.blocking(scala.io.Source.fromInputStream(inStream).mkString)
        decoded   = for {
                      json   <- parse(str)
                      c       = json.hcursor.downField("data").downField("target")
                      id     <- c.downField("id").as[Target.Id]
                      target <- c.as[Target]
                    } yield (id, target)
        obtained <- IO.fromEither(decoded)
      } yield assertEquals(obtained, (expectedId, expectedTarget))
    }
  }

  test("Target decoder - Point - EmissionLines") {
    val expectedId     = Target.Id(PosLong(3))
    val expectedTarget =
      Target.Sidereal(
        NonEmptyString("NGC 5949"),
        SiderealTracking(
          Coordinates(RightAscension.fromStringHMS.getOption("15:28:00.668").get,
                      Declination.fromStringSignedDMS.getOption("+64:45:47.4").get
          ),
          Epoch.J2000,
          ProperMotion.Zero.some,
          RadialVelocity.fromMetersPerSecond.getOption(BigDecimal(423607)),
          Parallax.Zero.some
        ),
        SourceProfile.Point(
          SpectralDefinition.EmissionLines(
            SortedMap(
              Wavelength.unsafeFromInt(1000000)    -> EmissionLine(
                PosBigDecimal(BigDecimal(1)).withUnit[KilometersPerSecond],
                PosBigDecimal(BigDecimal(1)).withUnit[WattsPerMeter2].toMeasureTagged
              ),
              Wavelength.unsafeFromInt(1000000000) -> EmissionLine(
                PosBigDecimal(BigDecimal(1)).withUnit[KilometersPerSecond],
                PosBigDecimal(BigDecimal(1)).withUnit[ErgsPerSecondCentimeter2].toMeasureTagged
              )
            ),
            PosBigDecimal(BigDecimal(12))
              .withUnit[ErgsPerSecondCentimeter2Angstrom]
              .toMeasureTagged
          )
        ),
        none
      )

    val jsonFile = "/t3.json"
    val url      = getClass().getResource(jsonFile)
    val file     = Paths.get(url.toURI()).toFile()
    inputStream(file).use { inStream =>
      for {
        str      <- IO.blocking(scala.io.Source.fromInputStream(inStream).mkString)
        decoded   = for {
                      json   <- parse(str)
                      c       = json.hcursor.downField("data").downField("target")
                      id     <- c.downField("id").as[Target.Id]
                      target <- c.as[Target]
                    } yield (id, target)
        obtained <- IO.fromEither(decoded)
      } yield assertEquals(obtained, (expectedId, expectedTarget))
    }
  }

  test("Target decoder - Uniform - BandNormalized") {
    val expectedId     = Target.Id(PosLong(4))
    val expectedTarget =
      Target.Sidereal(
        NonEmptyString("NGC 3312"),
        SiderealTracking(
          Coordinates(RightAscension.fromStringHMS.getOption("10:37:02.549").get,
                      Declination.fromStringSignedDMS.getOption("-27:33:54.17").get
          ),
          Epoch.J2000,
          ProperMotion.Zero.some,
          RadialVelocity.fromMetersPerSecond.getOption(BigDecimal(2826483)),
          Parallax.Zero.some
        ),
        SourceProfile.Uniform(
          SpectralDefinition.BandNormalized(
            UnnormalizedSED.Galaxy(GalaxySpectrum.Spiral),
            SortedMap(
              Band.B -> BrightnessValue
                .fromDouble(12.63)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged,
              Band.V -> BrightnessValue
                .fromDouble(13.96)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged,
              Band.J -> BrightnessValue
                .fromDouble(9.552)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.016)),
              Band.H -> BrightnessValue
                .fromDouble(8.907)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.017)),
              Band.K -> BrightnessValue
                .fromDouble(8.665)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.fromDouble(0.028))
            )
          )
        ),
        none
      )

    val jsonFile = "/t4.json"
    val url      = getClass().getResource(jsonFile)
    val file     = Paths.get(url.toURI()).toFile()
    inputStream(file).use { inStream =>
      for {
        str      <- IO.blocking(scala.io.Source.fromInputStream(inStream).mkString)
        decoded   = for {
                      json   <- parse(str)
                      c       = json.hcursor.downField("data").downField("target")
                      id     <- c.downField("id").as[Target.Id]
                      target <- c.as[Target]
                    } yield (id, target)
        obtained <- IO.fromEither(decoded)
      } yield assertEquals(obtained, (expectedId, expectedTarget))
    }
  }

}
