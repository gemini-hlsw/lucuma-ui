// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import cats.effect._
import munit._
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import lucuma.core.model.SiderealTracking
import lucuma.core.math.Coordinates
import lucuma.core.math.RightAscension
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import io.circe.parser._
import lucuma.core.math.ProperMotion
import lucuma.core.math.Parallax
import lucuma.core.math.RadialVelocity
import lucuma.core.model.Target
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.model.SourceProfile
import lucuma.core.model.SpectralDefinition
import lucuma.core.model.UnnormalizedSED
import lucuma.core.enum.GalaxySpectrum
import lucuma.core.enum.Band
import scala.collection.immutable.SortedMap
import lucuma.core.model.BandBrightness
import lucuma.core.math.units._
import lucuma.core.math.BrightnessValue
import eu.timepit.refined.types.numeric.PosLong

class DecodersSuite extends CatsEffectSuite {

  protected def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO.blocking(new FileInputStream(f)) // build
    } { inStream =>
      IO.blocking(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  test("Target decoder") {
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
            SortedMap.from(
              List(
                BandBrightness[ABMagnitude](BrightnessValue.fromDouble(14.147),
                                            Band.SloanU,
                                            BrightnessValue.fromDouble(0.005)
                ),
                BandBrightness[ABMagnitude](BrightnessValue.fromDouble(12.924),
                                            Band.SloanG,
                                            BrightnessValue.fromDouble(0.002)
                ),
                BandBrightness[ABMagnitude](BrightnessValue.fromDouble(12.252),
                                            Band.SloanR,
                                            BrightnessValue.fromDouble(0.002)
                ),
                BandBrightness[ABMagnitude](BrightnessValue.fromDouble(11.888),
                                            Band.SloanI,
                                            BrightnessValue.fromDouble(0.002)
                ),
                BandBrightness[ABMagnitude](BrightnessValue.fromDouble(11.636),
                                            Band.SloanZ,
                                            BrightnessValue.fromDouble(0.002)
                ),
                BandBrightness[VegaMagnitude](BrightnessValue.fromDouble(12.7), Band.B),
                BandBrightness[VegaMagnitude](BrightnessValue.fromDouble(10.279),
                                              Band.J,
                                              BrightnessValue.fromDouble(0.001)
                ),
                BandBrightness[VegaMagnitude](BrightnessValue.fromDouble(9.649),
                                              Band.H,
                                              BrightnessValue.fromDouble(0.012)
                ),
                BandBrightness[VegaMagnitude](BrightnessValue.fromDouble(9.425),
                                              Band.K,
                                              BrightnessValue.fromDouble(0.017)
                )
              ).map(b => b.band -> b)
            )
          )
        ),
        none,
        none
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
}
