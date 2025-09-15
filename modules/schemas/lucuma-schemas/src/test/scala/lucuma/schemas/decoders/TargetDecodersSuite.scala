// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order.*
import cats.syntax.all.*
import coulomb.Quantity
import coulomb.syntax.*
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.Positive
import io.circe.Decoder
import lucuma.core.enums.Band
import lucuma.core.enums.CatalogName
import lucuma.core.enums.GalaxySpectrum
import lucuma.core.math.BrightnessUnits.*
import lucuma.core.math.BrightnessValue
import lucuma.core.math.Coordinates
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.FluxDensityContinuumValue
import lucuma.core.math.LineFluxValue
import lucuma.core.math.LineWidthValue
import lucuma.core.math.Parallax
import lucuma.core.math.ProperMotion
import lucuma.core.math.RadialVelocity
import lucuma.core.math.RightAscension
import lucuma.core.math.Wavelength
import lucuma.core.math.dimensional.*
import lucuma.core.math.dimensional.syntax.*
import lucuma.core.math.units.*
import lucuma.core.model.CatalogInfo
import lucuma.core.model.EmissionLine
import lucuma.core.model.SiderealTracking
import lucuma.core.model.SourceProfile
import lucuma.core.model.SpectralDefinition
import lucuma.core.model.Target
import lucuma.core.model.UnnormalizedSED
import lucuma.core.refined.auto.*
import lucuma.odb.json.target.decoder.given
import lucuma.schemas.model.TargetWithId

import scala.collection.immutable.SortedMap

class DecodersSuite extends InputStreamSuite {
  inline given Predicate[Long, Positive] with
    transparent inline def isValid(inline t: Long): Boolean = t > 0

  given Decoder[(Target.Id, Target)] = Decoder.instance { c =>
    val root = c.downField("data").downField("target")
    for {
      id     <- root.downField("id").as[Target.Id]
      target <- root.as[Target]
    } yield (id, target)
  }

  test("Target decoder - Point - BandNormalized") {
    val expectedId: Target.Id           = Target.Id(2L.refined)
    val expectedTarget: Target.Sidereal =
      Target.Sidereal(
        "NGC 5949".refined,
        SiderealTracking(
          Coordinates(
            RightAscension.fromStringHMS.getOption("15:28:00.668").get,
            Declination.fromStringSignedDMS.getOption("+64:45:47.4").get
          ),
          Epoch.J2000,
          ProperMotion.Zero.some,
          RadialVelocity.fromMetersPerSecond.getOption(BigDecimal(423607)),
          Parallax.Zero.some
        ),
        SourceProfile.Point(
          SpectralDefinition.BandNormalized(
            UnnormalizedSED.Galaxy(GalaxySpectrum.Spiral).some,
            SortedMap(
              Band.SloanU ->
                BrightnessValue
                  .unsafeFrom(14.147)
                  .withUnit[ABMagnitude]
                  .toMeasureTagged
                  .withError(BrightnessValue.unsafeFrom(0.005)),
              Band.SloanG -> BrightnessValue
                .unsafeFrom(12.924)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.002)),
              Band.SloanR -> BrightnessValue
                .unsafeFrom(12.252)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.002)),
              Band.SloanI -> BrightnessValue
                .unsafeFrom(11.888)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.002)),
              Band.SloanZ -> BrightnessValue
                .unsafeFrom(11.636)
                .withUnit[ABMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.002)),
              Band.B      -> BrightnessValue.unsafeFrom(12.7).withUnit[VegaMagnitude].toMeasureTagged,
              Band.J      -> BrightnessValue
                .unsafeFrom(10.279)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.001)),
              Band.H      -> BrightnessValue
                .unsafeFrom(9.649)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.012)),
              Band.K      -> BrightnessValue
                .unsafeFrom(9.425)
                .withUnit[VegaMagnitude]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.017))
            )
          )
        ),
        CatalogInfo(
          CatalogName.Simbad,
          "M   1".refined[NonEmpty],
          Option("SNR".refined[NonEmpty])
        ).some
      )

    assertParsedStreamEquals("/t2.json", TargetWithId(expectedId, expectedTarget))
  }

  test("Target decoder - Point - EmissionLines") {
    val expectedId: Target.Id  = Target.Id(3L.refined)
    val expectedTarget: Target =
      Target.Sidereal(
        "NGC 5949".refined,
        SiderealTracking(
          Coordinates(
            RightAscension.fromStringHMS.getOption("15:28:00.668").get,
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
              Wavelength.unsafeFromIntPicometers(1000000)    -> EmissionLine(
                LineWidthValue.unsafeFrom(1).withUnit[KilometersPerSecond],
                LineFluxValue.unsafeFrom(1).withUnit[WattsPerMeter2].toMeasureTagged
              ),
              Wavelength.unsafeFromIntPicometers(1000000000) -> EmissionLine(
                LineWidthValue.unsafeFrom(1).withUnit[KilometersPerSecond],
                LineFluxValue.unsafeFrom(1).withUnit[ErgsPerSecondCentimeter2].toMeasureTagged
              )
            ),
            FluxDensityContinuumValue
              .unsafeFrom(0.12)
              .withUnit[ErgsPerSecondCentimeter2Angstrom]
              .toMeasureTagged
          )
        ),
        none
      )

    assertParsedStreamEquals("/t3.json", TargetWithId(expectedId, expectedTarget))
  }

  test("Target decoder - Uniform - BandNormalized") {
    val expectedId: Target.Id  = Target.Id(4L.refined)
    val expectedTarget: Target =
      Target.Sidereal(
        "NGC 3312".refined,
        SiderealTracking(
          Coordinates(
            RightAscension.fromStringHMS.getOption("10:37:02.549").get,
            Declination.fromStringSignedDMS.getOption("-27:33:54.17").get
          ),
          Epoch.J2000,
          ProperMotion.Zero.some,
          RadialVelocity.fromMetersPerSecond.getOption(BigDecimal(2826483)),
          Parallax.Zero.some
        ),
        SourceProfile.Uniform(
          SpectralDefinition.BandNormalized(
            UnnormalizedSED.Galaxy(GalaxySpectrum.Spiral).some,
            SortedMap(
              Band.B -> BrightnessValue
                .unsafeFrom(12.63)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged,
              Band.V -> BrightnessValue
                .unsafeFrom(13.96)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged,
              Band.J -> BrightnessValue
                .unsafeFrom(9.552)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.016)),
              Band.H -> BrightnessValue
                .unsafeFrom(8.907)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.017)),
              Band.K -> BrightnessValue
                .unsafeFrom(8.665)
                .withUnit[VegaMagnitudePerArcsec2]
                .toMeasureTagged
                .withError(BrightnessValue.unsafeFrom(0.028))
            )
          )
        ),
        none
      )

    assertParsedStreamEquals("/t4.json", TargetWithId(expectedId, expectedTarget))
  }
}
