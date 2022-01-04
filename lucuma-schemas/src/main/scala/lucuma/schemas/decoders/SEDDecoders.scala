// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.data.NonEmptyMap
import cats.syntax.all._
import eu.timepit.refined.types.numeric.PosBigDecimal
import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.math.Wavelength
import lucuma.core.model.UnnormalizedSED

import scala.collection.immutable.SortedMap

trait SEDDecoders {

  implicit val stellarLibraryDecoder: Decoder[UnnormalizedSED.StellarLibrary] =
    semiauto.deriveDecoder

  implicit val coolStarModelDecoder: Decoder[UnnormalizedSED.CoolStarModel] = semiauto.deriveDecoder

  implicit val galaxyDecoder: Decoder[UnnormalizedSED.Galaxy] = semiauto.deriveDecoder

  implicit val planetDecoder: Decoder[UnnormalizedSED.Planet] = semiauto.deriveDecoder

  implicit val quasarDecoder: Decoder[UnnormalizedSED.Quasar] = semiauto.deriveDecoder

  implicit val hiiRegionDecoder: Decoder[UnnormalizedSED.HIIRegion] = semiauto.deriveDecoder

  implicit val planetaryNebulaDecoder: Decoder[UnnormalizedSED.PlanetaryNebula] =
    semiauto.deriveDecoder

  implicit val powerLawDecoder: Decoder[UnnormalizedSED.PowerLaw] = semiauto.deriveDecoder

  implicit val blackBodyDecoder: Decoder[UnnormalizedSED.BlackBody] = semiauto.deriveDecoder

  implicit val userDefinedDecoder: Decoder[UnnormalizedSED.UserDefined] = Decoder.instance { c =>
    implicit val fluxDensityDecoder: Decoder[(Wavelength, PosBigDecimal)] =
      Decoder.instance(c =>
        for {
          w <- c.downField("wavelength").as[Wavelength]
          v <- c.downField("value").as[PosBigDecimal]
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
