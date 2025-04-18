// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.generic.semiauto.*
import lucuma.core.enums.*
import lucuma.core.math.Wavelength
import lucuma.odb.json.wavelength.decoder.given

// For when we don't need the whole observing mode, such as in the ObsSummary.
// This is also used to create the configuration.
sealed abstract class BasicConfiguration(val instrument: Instrument)
    extends Product
    with Serializable derives Eq:
  def gmosFpuAlternative: Option[Either[GmosNorthFpu, GmosSouthFpu]] = this match
    case BasicConfiguration.GmosNorthLongSlit(_, _, fpu, _) => fpu.asLeft.some
    case BasicConfiguration.GmosSouthLongSlit(_, _, fpu, _) => fpu.asRight.some
    case BasicConfiguration.F2LongSlit(_, _, _)             => none

  def siteFor: Site = this match
    case _: BasicConfiguration.GmosNorthLongSlit => Site.GN
    case _: BasicConfiguration.GmosSouthLongSlit => Site.GS
    case _: BasicConfiguration.F2LongSlit        => Site.GS

  def obsModeType: ObservingModeType = this match
    case n: BasicConfiguration.GmosNorthLongSlit => ObservingModeType.GmosNorthLongSlit
    case s: BasicConfiguration.GmosSouthLongSlit => ObservingModeType.GmosSouthLongSlit
    case s: BasicConfiguration.F2LongSlit        => ObservingModeType.Flamingos2LongSlit

object BasicConfiguration:
  given Decoder[BasicConfiguration] =
    Decoder
      .instance: c =>
        c.downField("gmosNorthLongSlit")
          .as[GmosNorthLongSlit]
          .orElse:
            c.downField("gmosSouthLongSlit")
              .as[GmosSouthLongSlit]
              .orElse:
                c.downField("flamingos2LongSlit").as[F2LongSlit]

  case class GmosNorthLongSlit(
    grating:           GmosNorthGrating,
    filter:            Option[GmosNorthFilter],
    fpu:               GmosNorthFpu,
    centralWavelength: CentralWavelength
  ) extends BasicConfiguration(Instrument.GmosNorth) derives Eq

  object GmosNorthLongSlit:
    given Decoder[GmosNorthLongSlit] = deriveDecoder

  case class GmosSouthLongSlit(
    grating:           GmosSouthGrating,
    filter:            Option[GmosSouthFilter],
    fpu:               GmosSouthFpu,
    centralWavelength: CentralWavelength
  ) extends BasicConfiguration(Instrument.GmosSouth) derives Eq

  object GmosSouthLongSlit:
    given Decoder[GmosSouthLongSlit] = deriveDecoder

  case class F2LongSlit(
    disperser: F2Disperser,
    filter:    F2Filter,
    fpu:       F2Fpu
  ) extends BasicConfiguration(Instrument.Flamingos2) derives Eq

  object F2LongSlit:
    given Decoder[F2LongSlit] = deriveDecoder
