// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.data.NonEmptyMap
import cats.syntax.all._
import coulomb._
import coulomb.syntax._
import coulomb.units.si.Kelvin
import eu.timepit.refined.types.numeric.PosBigDecimal
import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.refined._
import lucuma.core.enums._
import lucuma.core.math.Wavelength
import lucuma.core.model.UnnormalizedSED

import scala.collection.immutable.SortedMap

trait SEDDecoders {

  implicit val stellarLibraryDecoder: Decoder[UnnormalizedSED.StellarLibrary] =
    Decoder.instance(
      _.downField("stellarLibrary")
        .as[StellarLibrarySpectrum]
        .map(UnnormalizedSED.StellarLibrary.apply)
    )

  implicit val coolStarModelDecoder: Decoder[UnnormalizedSED.CoolStarModel] =
    Decoder.instance(
      _.downField("coolStar")
        .as[CoolStarTemperature]
        .map(UnnormalizedSED.CoolStarModel.apply)
    )

  implicit val galaxyDecoder: Decoder[UnnormalizedSED.Galaxy] =
    Decoder.instance(
      _.downField("galaxy")
        .as[GalaxySpectrum]
        .map(UnnormalizedSED.Galaxy.apply)
    )

  implicit val planetDecoder: Decoder[UnnormalizedSED.Planet] =
    Decoder.instance(
      _.downField("planet")
        .as[PlanetSpectrum]
        .map(UnnormalizedSED.Planet.apply)
    )

  implicit val quasarDecoder: Decoder[UnnormalizedSED.Quasar] =
    Decoder.instance(
      _.downField("quasar")
        .as[QuasarSpectrum]
        .map(UnnormalizedSED.Quasar.apply)
    )

  implicit val hiiRegionDecoder: Decoder[UnnormalizedSED.HIIRegion] =
    Decoder.instance(
      _.downField("hiiRegion")
        .as[HIIRegionSpectrum]
        .map(UnnormalizedSED.HIIRegion.apply)
    )

  implicit val planetaryNebulaDecoder: Decoder[UnnormalizedSED.PlanetaryNebula] =
    Decoder.instance(
      _.downField("planetaryNebula")
        .as[PlanetaryNebulaSpectrum]
        .map(UnnormalizedSED.PlanetaryNebula.apply)
    )

  implicit val powerLawDecoder: Decoder[UnnormalizedSED.PowerLaw] =
    Decoder.instance(
      _.downField("powerLaw")
        .as[BigDecimal]
        .map(UnnormalizedSED.PowerLaw.apply)
    )

  implicit val blackBodyDecoder: Decoder[UnnormalizedSED.BlackBody] =
    Decoder.instance(
      _.downField("blackBodyTempK")
        .as[PosInt]
        .map(_.withUnit[Kelvin])
        .map(UnnormalizedSED.BlackBody.apply)
    )

  implicit val userDefinedDecoder: Decoder[UnnormalizedSED.UserDefined] = Decoder.instance { c =>
    implicit val fluxDensityDecoder: Decoder[(Wavelength, PosBigDecimal)] =
      Decoder.instance(c =>
        for {
          w <- c.downField("wavelength").as[Wavelength]
          v <- c.downField("density").as[PosBigDecimal]
        } yield (w, v)
      )

    c.downField("fluxDensities")
      .as[List[(Wavelength, PosBigDecimal)]]
      .map(fds => UnnormalizedSED.UserDefined(NonEmptyMap.fromMapUnsafe(SortedMap.from(fds))))
  }

  implicit val unnormalizedSEDDecoder: Decoder[UnnormalizedSED] = List[Decoder[UnnormalizedSED]](
    Decoder[UnnormalizedSED.StellarLibrary].widen,
    Decoder[UnnormalizedSED.CoolStarModel].widen,
    Decoder[UnnormalizedSED.Galaxy].widen,
    Decoder[UnnormalizedSED.Planet].widen,
    Decoder[UnnormalizedSED.Quasar].widen,
    Decoder[UnnormalizedSED.HIIRegion].widen,
    Decoder[UnnormalizedSED.PlanetaryNebula].widen,
    Decoder[UnnormalizedSED.PowerLaw].widen,
    Decoder[UnnormalizedSED.BlackBody].widen,
    Decoder[UnnormalizedSED.UserDefined].widen
  ).reduceLeft(_ or _)
}
