// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.data.NonEmptyList
import cats.derived.*
import cats.syntax.all.*
import io.circe.Decoder
import io.circe.generic.semiauto.*
import lucuma.core.enums.*
import lucuma.core.math.Offset
import lucuma.core.math.Wavelength
import lucuma.core.math.WavelengthDither
import lucuma.odb.json.offset.decoder.given
import lucuma.odb.json.wavelength
import lucuma.odb.json.wavelength.decoder.given
import monocle.Focus
import monocle.Lens
import monocle.Prism
import monocle.macros.GenPrism

sealed abstract class ObservingMode(val instrument: Instrument) extends Product with Serializable
    derives Eq {
  def isCustomized: Boolean

  def obsModeType: ObservingModeType = this match
    case _: ObservingMode.GmosNorthLongSlit  => ObservingModeType.GmosNorthLongSlit
    case _: ObservingMode.GmosSouthLongSlit  => ObservingModeType.GmosSouthLongSlit
    case _: ObservingMode.GmosNorthImaging   => ObservingModeType.GmosNorthImaging
    case _: ObservingMode.GmosSouthImaging   => ObservingModeType.GmosSouthImaging
    case _: ObservingMode.Flamingos2LongSlit => ObservingModeType.Flamingos2LongSlit

  def gmosFpuAlternative: Option[Either[GmosNorthFpu, GmosSouthFpu]] = this match
    case o: ObservingMode.GmosNorthLongSlit => o.fpu.asLeft.some
    case o: ObservingMode.GmosSouthLongSlit => o.fpu.asRight.some
    case _                                  => none

  def siteFor: Site = this match
    case _: ObservingMode.GmosNorthLongSlit  => Site.GN
    case _: ObservingMode.GmosSouthLongSlit  => Site.GS
    case _: ObservingMode.GmosNorthImaging   => Site.GN
    case _: ObservingMode.GmosSouthImaging   => Site.GS
    case _: ObservingMode.Flamingos2LongSlit => Site.GS

  def toBasicConfiguration: BasicConfiguration = this match
    case n: ObservingMode.GmosNorthLongSlit  =>
      BasicConfiguration.GmosNorthLongSlit(n.grating, n.filter, n.fpu, n.centralWavelength)
    case s: ObservingMode.GmosSouthLongSlit  =>
      BasicConfiguration.GmosSouthLongSlit(s.grating, s.filter, s.fpu, s.centralWavelength)
    case n: ObservingMode.GmosNorthImaging   =>
      BasicConfiguration.GmosNorthImaging(n.filters)
    case s: ObservingMode.GmosSouthImaging   =>
      BasicConfiguration.GmosSouthImaging(s.filters)
    case f: ObservingMode.Flamingos2LongSlit =>
      BasicConfiguration.Flamingos2LongSlit(f.disperser, f.filter, f.fpu)
}

object ObservingMode:
  given Decoder[WavelengthDither] =
    Decoder.instance:
      _.downField("picometers").as[Int].map(WavelengthDither.intPicometers.get)

  given Decoder[ObservingMode] =
    Decoder
      .instance: c =>
        c.downField("gmosNorthLongSlit")
          .as[GmosNorthLongSlit]
          .orElse:
            c.downField("gmosSouthLongSlit").as[GmosSouthLongSlit]
          .orElse:
            c.downField("gmosNorthImaging").as[GmosNorthImaging]
          .orElse:
            c.downField("gmosSouthImaging").as[GmosSouthImaging]
          .orElse:
            c.downField("flamingos2LongSlit").as[Flamingos2LongSlit]

  case class GmosNorthLongSlit(
    initialGrating:            GmosNorthGrating,
    grating:                   GmosNorthGrating,
    initialFilter:             Option[GmosNorthFilter],
    filter:                    Option[GmosNorthFilter],
    initialFpu:                GmosNorthFpu,
    fpu:                       GmosNorthFpu,
    initialCentralWavelength:  CentralWavelength,
    centralWavelength:         CentralWavelength,
    defaultXBin:               GmosXBinning,
    explicitXBin:              Option[GmosXBinning],
    defaultYBin:               GmosYBinning,
    explicitYBin:              Option[GmosYBinning],
    defaultAmpReadMode:        GmosAmpReadMode,
    explicitAmpReadMode:       Option[GmosAmpReadMode],
    defaultAmpGain:            GmosAmpGain,
    explicitAmpGain:           Option[GmosAmpGain],
    defaultRoi:                GmosRoi,
    explicitRoi:               Option[GmosRoi],
    defaultWavelengthDithers:  NonEmptyList[WavelengthDither],
    explicitWavelengthDithers: Option[NonEmptyList[WavelengthDither]],
    defaultSpatialOffsets:     NonEmptyList[Offset.Q],
    explicitSpatialOffsets:    Option[NonEmptyList[Offset.Q]]
  ) extends ObservingMode(Instrument.GmosNorth) derives Eq:
    val xBin: GmosXBinning                                =
      explicitXBin.getOrElse(defaultXBin)
    val yBin: GmosYBinning                                =
      explicitYBin.getOrElse(defaultYBin)
    val ampReadMode: GmosAmpReadMode                      =
      explicitAmpReadMode.getOrElse(defaultAmpReadMode)
    val ampGain: GmosAmpGain                              =
      explicitAmpGain.getOrElse(defaultAmpGain)
    val roi: GmosRoi                                      =
      explicitRoi.getOrElse(defaultRoi)
    val wavelengthDithers: NonEmptyList[WavelengthDither] =
      explicitWavelengthDithers.getOrElse(defaultWavelengthDithers)
    val spatialOffsets: NonEmptyList[Offset.Q]            =
      explicitSpatialOffsets.getOrElse(defaultSpatialOffsets)

    def isCustomized: Boolean =
      initialGrating =!= grating ||
        initialFilter =!= filter ||
        initialFpu =!= fpu ||
        initialCentralWavelength =!= centralWavelength ||
        explicitXBin.exists(_ =!= defaultXBin) ||
        explicitYBin.exists(_ =!= defaultYBin) ||
        explicitAmpReadMode.exists(_ =!= defaultAmpReadMode) ||
        explicitAmpGain.exists(_ =!= defaultAmpGain) ||
        explicitRoi.exists(_ =!= defaultRoi) ||
        explicitWavelengthDithers.exists(_ =!= defaultWavelengthDithers) ||
        explicitSpatialOffsets.exists(_ =!= defaultSpatialOffsets)

    def revertCustomizations: GmosNorthLongSlit =
      this.copy(
        grating = this.initialGrating,
        filter = this.initialFilter,
        fpu = this.initialFpu,
        centralWavelength = this.initialCentralWavelength,
        explicitXBin = None,
        explicitYBin = None,
        explicitAmpReadMode = None,
        explicitAmpGain = None,
        explicitRoi = None,
        explicitWavelengthDithers = None,
        explicitSpatialOffsets = None
      )

  object GmosNorthLongSlit:
    given Decoder[GmosNorthLongSlit] = deriveDecoder

    val initialGrating: Lens[GmosNorthLongSlit, GmosNorthGrating]                                  =
      Focus[GmosNorthLongSlit](_.initialGrating)
    val grating: Lens[GmosNorthLongSlit, GmosNorthGrating]                                         =
      Focus[GmosNorthLongSlit](_.grating)
    val initialFilter: Lens[GmosNorthLongSlit, Option[GmosNorthFilter]]                            =
      Focus[GmosNorthLongSlit](_.initialFilter)
    val filter: Lens[GmosNorthLongSlit, Option[GmosNorthFilter]]                                   =
      Focus[GmosNorthLongSlit](_.filter)
    val initialFpu: Lens[GmosNorthLongSlit, GmosNorthFpu]                                          =
      Focus[GmosNorthLongSlit](_.initialFpu)
    val fpu: Lens[GmosNorthLongSlit, GmosNorthFpu]                                                 =
      Focus[GmosNorthLongSlit](_.fpu)
    val initialCentralWavelength: Lens[GmosNorthLongSlit, CentralWavelength]                       =
      Focus[GmosNorthLongSlit](_.initialCentralWavelength)
    val centralWavelength: Lens[GmosNorthLongSlit, CentralWavelength]                              =
      Focus[GmosNorthLongSlit](_.centralWavelength)
    val defaultXBin: Lens[GmosNorthLongSlit, GmosXBinning]                                         =
      Focus[GmosNorthLongSlit](_.defaultXBin)
    val explicitXBin: Lens[GmosNorthLongSlit, Option[GmosXBinning]]                                =
      Focus[GmosNorthLongSlit](_.explicitXBin)
    val defaultYBin: Lens[GmosNorthLongSlit, GmosYBinning]                                         =
      Focus[GmosNorthLongSlit](_.defaultYBin)
    val explicitYBin: Lens[GmosNorthLongSlit, Option[GmosYBinning]]                                =
      Focus[GmosNorthLongSlit](_.explicitYBin)
    val defaultAmpReadMode: Lens[GmosNorthLongSlit, GmosAmpReadMode]                               =
      Focus[GmosNorthLongSlit](_.defaultAmpReadMode)
    val explicitAmpReadMode: Lens[GmosNorthLongSlit, Option[GmosAmpReadMode]]                      =
      Focus[GmosNorthLongSlit](_.explicitAmpReadMode)
    val defaultAmpGain: Lens[GmosNorthLongSlit, GmosAmpGain]                                       =
      Focus[GmosNorthLongSlit](_.defaultAmpGain)
    val explicitAmpGain: Lens[GmosNorthLongSlit, Option[GmosAmpGain]]                              =
      Focus[GmosNorthLongSlit](_.explicitAmpGain)
    val defaultRoi: Lens[GmosNorthLongSlit, GmosRoi]                                               =
      Focus[GmosNorthLongSlit](_.defaultRoi)
    val explicitRoi: Lens[GmosNorthLongSlit, Option[GmosRoi]]                                      =
      Focus[GmosNorthLongSlit](_.explicitRoi)
    val defaultWavelengthDithers: Lens[GmosNorthLongSlit, NonEmptyList[WavelengthDither]]          =
      Focus[GmosNorthLongSlit](_.defaultWavelengthDithers)
    val explicitWavelengthDithers: Lens[GmosNorthLongSlit, Option[NonEmptyList[WavelengthDither]]] =
      Focus[GmosNorthLongSlit](_.explicitWavelengthDithers)
    val defaultSpatialOffsets: Lens[GmosNorthLongSlit, NonEmptyList[Offset.Q]]                     =
      Focus[GmosNorthLongSlit](_.defaultSpatialOffsets)
    val explicitSpatialOffsets: Lens[GmosNorthLongSlit, Option[NonEmptyList[Offset.Q]]]            =
      Focus[GmosNorthLongSlit](_.explicitSpatialOffsets)

  case class GmosSouthLongSlit(
    initialGrating:            GmosSouthGrating,
    grating:                   GmosSouthGrating,
    initialFilter:             Option[GmosSouthFilter],
    filter:                    Option[GmosSouthFilter],
    initialFpu:                GmosSouthFpu,
    fpu:                       GmosSouthFpu,
    initialCentralWavelength:  CentralWavelength,
    centralWavelength:         CentralWavelength,
    defaultXBin:               GmosXBinning,
    explicitXBin:              Option[GmosXBinning],
    defaultYBin:               GmosYBinning,
    explicitYBin:              Option[GmosYBinning],
    defaultAmpReadMode:        GmosAmpReadMode,
    explicitAmpReadMode:       Option[GmosAmpReadMode],
    defaultAmpGain:            GmosAmpGain,
    explicitAmpGain:           Option[GmosAmpGain],
    defaultRoi:                GmosRoi,
    explicitRoi:               Option[GmosRoi],
    defaultWavelengthDithers:  NonEmptyList[WavelengthDither],
    explicitWavelengthDithers: Option[NonEmptyList[WavelengthDither]],
    defaultSpatialOffsets:     NonEmptyList[Offset.Q],
    explicitSpatialOffsets:    Option[NonEmptyList[Offset.Q]]
  ) extends ObservingMode(Instrument.GmosSouth) derives Eq:
    val xBin: GmosXBinning                                =
      explicitXBin.getOrElse(defaultXBin)
    val yBin: GmosYBinning                                =
      explicitYBin.getOrElse(defaultYBin)
    val ampReadMode: GmosAmpReadMode                      =
      explicitAmpReadMode.getOrElse(defaultAmpReadMode)
    val ampGain: GmosAmpGain                              =
      explicitAmpGain.getOrElse(defaultAmpGain)
    val roi: GmosRoi                                      =
      explicitRoi.getOrElse(defaultRoi)
    val wavelengthDithers: NonEmptyList[WavelengthDither] =
      explicitWavelengthDithers.getOrElse(defaultWavelengthDithers)
    val spatialOffsets: NonEmptyList[Offset.Q]            =
      explicitSpatialOffsets.getOrElse(defaultSpatialOffsets)

    def isCustomized: Boolean =
      initialGrating =!= grating ||
        initialFilter =!= filter ||
        initialFpu =!= fpu ||
        initialCentralWavelength =!= centralWavelength ||
        explicitXBin.exists(_ =!= defaultXBin) ||
        explicitYBin.exists(_ =!= defaultYBin) ||
        explicitAmpReadMode.exists(_ =!= defaultAmpReadMode) ||
        explicitAmpGain.exists(_ =!= defaultAmpGain) ||
        explicitRoi.exists(_ =!= defaultRoi) ||
        explicitWavelengthDithers.exists(_ =!= defaultWavelengthDithers) ||
        explicitSpatialOffsets.exists(_ =!= defaultSpatialOffsets)

    def revertCustomizations: GmosSouthLongSlit =
      this.copy(
        grating = this.initialGrating,
        filter = this.initialFilter,
        fpu = this.initialFpu,
        centralWavelength = this.initialCentralWavelength,
        explicitXBin = None,
        explicitYBin = None,
        explicitAmpReadMode = None,
        explicitAmpGain = None,
        explicitRoi = None,
        explicitWavelengthDithers = None,
        explicitSpatialOffsets = None
      )

  object GmosSouthLongSlit:
    given Decoder[GmosSouthLongSlit] = deriveDecoder

    val initialGrating: Lens[GmosSouthLongSlit, GmosSouthGrating]                                  =
      Focus[GmosSouthLongSlit](_.initialGrating)
    val grating: Lens[GmosSouthLongSlit, GmosSouthGrating]                                         =
      Focus[GmosSouthLongSlit](_.grating)
    val initialFilter: Lens[GmosSouthLongSlit, Option[GmosSouthFilter]]                            =
      Focus[GmosSouthLongSlit](_.initialFilter)
    val filter: Lens[GmosSouthLongSlit, Option[GmosSouthFilter]]                                   =
      Focus[GmosSouthLongSlit](_.filter)
    val initialFpu: Lens[GmosSouthLongSlit, GmosSouthFpu]                                          =
      Focus[GmosSouthLongSlit](_.initialFpu)
    val fpu: Lens[GmosSouthLongSlit, GmosSouthFpu]                                                 =
      Focus[GmosSouthLongSlit](_.fpu)
    val initialCentralWavelength: Lens[GmosSouthLongSlit, CentralWavelength]                       =
      Focus[GmosSouthLongSlit](_.initialCentralWavelength)
    val centralWavelength: Lens[GmosSouthLongSlit, CentralWavelength]                              =
      Focus[GmosSouthLongSlit](_.centralWavelength)
    val defaultXBin: Lens[GmosSouthLongSlit, GmosXBinning]                                         =
      Focus[GmosSouthLongSlit](_.defaultXBin)
    val explicitXBin: Lens[GmosSouthLongSlit, Option[GmosXBinning]]                                =
      Focus[GmosSouthLongSlit](_.explicitXBin)
    val defaultYBin: Lens[GmosSouthLongSlit, GmosYBinning]                                         =
      Focus[GmosSouthLongSlit](_.defaultYBin)
    val explicitYBin: Lens[GmosSouthLongSlit, Option[GmosYBinning]]                                =
      Focus[GmosSouthLongSlit](_.explicitYBin)
    val defaultAmpReadMode: Lens[GmosSouthLongSlit, GmosAmpReadMode]                               =
      Focus[GmosSouthLongSlit](_.defaultAmpReadMode)
    val explicitAmpReadMode: Lens[GmosSouthLongSlit, Option[GmosAmpReadMode]]                      =
      Focus[GmosSouthLongSlit](_.explicitAmpReadMode)
    val defaultAmpGain: Lens[GmosSouthLongSlit, GmosAmpGain]                                       =
      Focus[GmosSouthLongSlit](_.defaultAmpGain)
    val explicitAmpGain: Lens[GmosSouthLongSlit, Option[GmosAmpGain]]                              =
      Focus[GmosSouthLongSlit](_.explicitAmpGain)
    val defaultRoi: Lens[GmosSouthLongSlit, GmosRoi]                                               =
      Focus[GmosSouthLongSlit](_.defaultRoi)
    val explicitRoi: Lens[GmosSouthLongSlit, Option[GmosRoi]]                                      =
      Focus[GmosSouthLongSlit](_.explicitRoi)
    val defaultWavelengthDithers: Lens[GmosSouthLongSlit, NonEmptyList[WavelengthDither]]          =
      Focus[GmosSouthLongSlit](_.defaultWavelengthDithers)
    val explicitWavelengthDithers: Lens[GmosSouthLongSlit, Option[NonEmptyList[WavelengthDither]]] =
      Focus[GmosSouthLongSlit](_.explicitWavelengthDithers)
    val defaultSpatialOffsets: Lens[GmosSouthLongSlit, NonEmptyList[Offset.Q]]                     =
      Focus[GmosSouthLongSlit](_.defaultSpatialOffsets)
    val explicitSpatialOffsets: Lens[GmosSouthLongSlit, Option[NonEmptyList[Offset.Q]]]            =
      Focus[GmosSouthLongSlit](_.explicitSpatialOffsets)

  case class GmosNorthImaging(
    initialFilters:              NonEmptyList[GmosNorthFilter],
    filters:                     NonEmptyList[GmosNorthFilter],
    offsets:                     List[Offset],
    defaultMultipleFiltersMode:  MultipleFiltersMode,
    explicitMultipleFiltersMode: Option[MultipleFiltersMode],
    defaultBin:                  GmosBinning,
    explicitBin:                 Option[GmosBinning],
    defaultAmpReadMode:          GmosAmpReadMode,
    explicitAmpReadMode:         Option[GmosAmpReadMode],
    defaultAmpGain:              GmosAmpGain,
    explicitAmpGain:             Option[GmosAmpGain],
    defaultRoi:                  GmosRoi,
    explicitRoi:                 Option[GmosRoi]
  ) extends ObservingMode(Instrument.GmosNorth) derives Eq:
    val multipleFiltersMode: MultipleFiltersMode =
      explicitMultipleFiltersMode.getOrElse(defaultMultipleFiltersMode)
    val bin: GmosBinning                         =
      explicitBin.getOrElse(defaultBin)
    val ampReadMode: GmosAmpReadMode             =
      explicitAmpReadMode.getOrElse(defaultAmpReadMode)
    val ampGain: GmosAmpGain                     =
      explicitAmpGain.getOrElse(defaultAmpGain)
    val roi: GmosRoi                             =
      explicitRoi.getOrElse(defaultRoi)

    def isCustomized: Boolean =
      initialFilters =!= filters ||
        explicitMultipleFiltersMode.exists(_ =!= defaultMultipleFiltersMode) ||
        explicitBin.exists(_ =!= defaultBin) ||
        explicitAmpReadMode.exists(_ =!= defaultAmpReadMode) ||
        explicitAmpGain.exists(_ =!= defaultAmpGain) ||
        explicitRoi.exists(_ =!= defaultRoi)

    def revertCustomizations: GmosNorthImaging =
      this.copy(
        filters = this.initialFilters,
        offsets = Nil,
        explicitMultipleFiltersMode = None,
        explicitBin = None,
        explicitAmpReadMode = None,
        explicitAmpGain = None,
        explicitRoi = None
      )

  object GmosNorthImaging:
    given Decoder[GmosNorthImaging] = deriveDecoder

    val initialFilters: Lens[GmosNorthImaging, NonEmptyList[GmosNorthFilter]]            =
      Focus[GmosNorthImaging](_.initialFilters)
    val filters: Lens[GmosNorthImaging, NonEmptyList[GmosNorthFilter]]                   =
      Focus[GmosNorthImaging](_.filters)
    val defaultMultipleFiltersMode: Lens[GmosNorthImaging, MultipleFiltersMode]          =
      Focus[GmosNorthImaging](_.defaultMultipleFiltersMode)
    val explicitMultipleFiltersMode: Lens[GmosNorthImaging, Option[MultipleFiltersMode]] =
      Focus[GmosNorthImaging](_.explicitMultipleFiltersMode)
    val defaultBin: Lens[GmosNorthImaging, GmosBinning]                                  =
      Focus[GmosNorthImaging](_.defaultBin)
    val explicitBin: Lens[GmosNorthImaging, Option[GmosBinning]]                         =
      Focus[GmosNorthImaging](_.explicitBin)
    val defaultAmpReadMode: Lens[GmosNorthImaging, GmosAmpReadMode]                      =
      Focus[GmosNorthImaging](_.defaultAmpReadMode)
    val explicitAmpReadMode: Lens[GmosNorthImaging, Option[GmosAmpReadMode]]             =
      Focus[GmosNorthImaging](_.explicitAmpReadMode)
    val defaultAmpGain: Lens[GmosNorthImaging, GmosAmpGain]                              =
      Focus[GmosNorthImaging](_.defaultAmpGain)
    val explicitAmpGain: Lens[GmosNorthImaging, Option[GmosAmpGain]]                     =
      Focus[GmosNorthImaging](_.explicitAmpGain)
    val defaultRoi: Lens[GmosNorthImaging, GmosRoi]                                      =
      Focus[GmosNorthImaging](_.defaultRoi)
    val explicitRoi: Lens[GmosNorthImaging, Option[GmosRoi]]                             =
      Focus[GmosNorthImaging](_.explicitRoi)
    val offsets: Lens[GmosNorthImaging, List[Offset]]                                    =
      Focus[GmosNorthImaging](_.offsets)

  case class GmosSouthImaging(
    initialFilters:              NonEmptyList[GmosSouthFilter],
    filters:                     NonEmptyList[GmosSouthFilter],
    offsets:                     List[Offset],
    defaultMultipleFiltersMode:  MultipleFiltersMode,
    explicitMultipleFiltersMode: Option[MultipleFiltersMode],
    defaultBin:                  GmosBinning,
    explicitBin:                 Option[GmosBinning],
    defaultAmpReadMode:          GmosAmpReadMode,
    explicitAmpReadMode:         Option[GmosAmpReadMode],
    defaultAmpGain:              GmosAmpGain,
    explicitAmpGain:             Option[GmosAmpGain],
    defaultRoi:                  GmosRoi,
    explicitRoi:                 Option[GmosRoi]
  ) extends ObservingMode(Instrument.GmosSouth) derives Eq:
    val multipleFiltersMode: MultipleFiltersMode =
      explicitMultipleFiltersMode.getOrElse(defaultMultipleFiltersMode)
    val bin: GmosBinning                         =
      explicitBin.getOrElse(defaultBin)
    val ampReadMode: GmosAmpReadMode             =
      explicitAmpReadMode.getOrElse(defaultAmpReadMode)
    val ampGain: GmosAmpGain                     =
      explicitAmpGain.getOrElse(defaultAmpGain)
    val roi: GmosRoi                             =
      explicitRoi.getOrElse(defaultRoi)

    def isCustomized: Boolean =
      initialFilters =!= filters ||
        explicitMultipleFiltersMode.exists(_ =!= defaultMultipleFiltersMode) ||
        explicitBin.exists(_ =!= defaultBin) ||
        explicitAmpReadMode.exists(_ =!= defaultAmpReadMode) ||
        explicitAmpGain.exists(_ =!= defaultAmpGain) ||
        explicitRoi.exists(_ =!= defaultRoi)

    def revertCustomizations: GmosSouthImaging =
      this.copy(
        filters = this.initialFilters,
        offsets = Nil,
        explicitMultipleFiltersMode = None,
        explicitBin = None,
        explicitAmpReadMode = None,
        explicitAmpGain = None,
        explicitRoi = None
      )

  object GmosSouthImaging:
    given Decoder[GmosSouthImaging] = deriveDecoder

    val initialFilters: Lens[GmosSouthImaging, NonEmptyList[GmosSouthFilter]]            =
      Focus[GmosSouthImaging](_.initialFilters)
    val filters: Lens[GmosSouthImaging, NonEmptyList[GmosSouthFilter]]                   =
      Focus[GmosSouthImaging](_.filters)
    val defaultMultipleFiltersMode: Lens[GmosSouthImaging, MultipleFiltersMode]          =
      Focus[GmosSouthImaging](_.defaultMultipleFiltersMode)
    val explicitMultipleFiltersMode: Lens[GmosSouthImaging, Option[MultipleFiltersMode]] =
      Focus[GmosSouthImaging](_.explicitMultipleFiltersMode)
    val defaultBin: Lens[GmosSouthImaging, GmosBinning]                                  =
      Focus[GmosSouthImaging](_.defaultBin)
    val explicitBin: Lens[GmosSouthImaging, Option[GmosBinning]]                         =
      Focus[GmosSouthImaging](_.explicitBin)
    val defaultAmpReadMode: Lens[GmosSouthImaging, GmosAmpReadMode]                      =
      Focus[GmosSouthImaging](_.defaultAmpReadMode)
    val explicitAmpReadMode: Lens[GmosSouthImaging, Option[GmosAmpReadMode]]             =
      Focus[GmosSouthImaging](_.explicitAmpReadMode)
    val defaultAmpGain: Lens[GmosSouthImaging, GmosAmpGain]                              =
      Focus[GmosSouthImaging](_.defaultAmpGain)
    val explicitAmpGain: Lens[GmosSouthImaging, Option[GmosAmpGain]]                     =
      Focus[GmosSouthImaging](_.explicitAmpGain)
    val defaultRoi: Lens[GmosSouthImaging, GmosRoi]                                      =
      Focus[GmosSouthImaging](_.defaultRoi)
    val explicitRoi: Lens[GmosSouthImaging, Option[GmosRoi]]                             =
      Focus[GmosSouthImaging](_.explicitRoi)
    val offsets: Lens[GmosSouthImaging, List[Offset]]                                    =
      Focus[GmosSouthImaging](_.offsets)

  case class Flamingos2LongSlit(
    initialDisperser:    Flamingos2Disperser,
    disperser:           Flamingos2Disperser,
    initialFilter:       Flamingos2Filter,
    filter:              Flamingos2Filter,
    initialFpu:          Flamingos2Fpu,
    fpu:                 Flamingos2Fpu,
    explicitReadMode:    Option[Flamingos2ReadMode],
    explicitReads:       Option[Flamingos2Reads],
    defaultDecker:       Flamingos2Decker,
    explicitDecker:      Option[Flamingos2Decker],
    defaultReadoutMode:  Flamingos2ReadoutMode,
    explicitReadoutMode: Option[Flamingos2ReadoutMode],
    defaultOffsets:      NonEmptyList[Offset],
    explicitOffsets:     Option[NonEmptyList[Offset]]
  ) extends ObservingMode(Instrument.GmosSouth) derives Eq:
    val decker: Flamingos2Decker           =
      explicitDecker.getOrElse(defaultDecker)
    val readoutMode: Flamingos2ReadoutMode =
      explicitReadoutMode.getOrElse(defaultReadoutMode)
    val offsets: NonEmptyList[Offset]      =
      explicitOffsets.getOrElse(defaultOffsets)

    def isCustomized: Boolean =
      initialDisperser =!= disperser ||
        initialFilter =!= filter ||
        initialFpu =!= fpu ||
        explicitReadMode.isDefined ||
        explicitReads.isDefined ||
        explicitDecker.exists(_ =!= defaultDecker) ||
        explicitReadoutMode.exists(_ =!= defaultReadoutMode) ||
        explicitOffsets.exists(_ =!= defaultOffsets)

    def revertCustomizations: Flamingos2LongSlit =
      this.copy(
        disperser = this.initialDisperser,
        filter = this.initialFilter,
        fpu = this.initialFpu,
        explicitReadMode = None,
        explicitReads = None,
        explicitDecker = None,
        explicitReadoutMode = None,
        explicitOffsets = None
      )

  object Flamingos2LongSlit:
    given Decoder[Flamingos2LongSlit] = deriveDecoder

    val initialDisperser: Lens[Flamingos2LongSlit, Flamingos2Disperser]              =
      Focus[Flamingos2LongSlit](_.initialDisperser)
    val disperser: Lens[Flamingos2LongSlit, Flamingos2Disperser]                     =
      Focus[Flamingos2LongSlit](_.disperser)
    val initialFilter: Lens[Flamingos2LongSlit, Flamingos2Filter]                    =
      Focus[Flamingos2LongSlit](_.initialFilter)
    val filter: Lens[Flamingos2LongSlit, Flamingos2Filter]                           =
      Focus[Flamingos2LongSlit](_.filter)
    val initialFpu: Lens[Flamingos2LongSlit, Flamingos2Fpu]                          =
      Focus[Flamingos2LongSlit](_.initialFpu)
    val fpu: Lens[Flamingos2LongSlit, Flamingos2Fpu]                                 =
      Focus[Flamingos2LongSlit](_.fpu)
    val explicitReadMode: Lens[Flamingos2LongSlit, Option[Flamingos2ReadMode]]       =
      Focus[Flamingos2LongSlit](_.explicitReadMode)
    val explicitReads: Lens[Flamingos2LongSlit, Option[Flamingos2Reads]]             =
      Focus[Flamingos2LongSlit](_.explicitReads)
    val defaultDecker: Lens[Flamingos2LongSlit, Flamingos2Decker]                    =
      Focus[Flamingos2LongSlit](_.defaultDecker)
    val explicitDecker: Lens[Flamingos2LongSlit, Option[Flamingos2Decker]]           =
      Focus[Flamingos2LongSlit](_.explicitDecker)
    val defaultReadoutMode: Lens[Flamingos2LongSlit, Flamingos2ReadoutMode]          =
      Focus[Flamingos2LongSlit](_.defaultReadoutMode)
    val explicitReadoutMode: Lens[Flamingos2LongSlit, Option[Flamingos2ReadoutMode]] =
      Focus[Flamingos2LongSlit](_.explicitReadoutMode)
    val defaultOffsets: Lens[Flamingos2LongSlit, NonEmptyList[Offset]]               =
      Focus[Flamingos2LongSlit](_.defaultOffsets)
    val explicitOffsets: Lens[Flamingos2LongSlit, Option[NonEmptyList[Offset]]]      =
      Focus[Flamingos2LongSlit](_.explicitOffsets)

  val gmosNorthLongSlit: Prism[ObservingMode, GmosNorthLongSlit] =
    GenPrism[ObservingMode, GmosNorthLongSlit]

  val gmosSouthLongSlit: Prism[ObservingMode, GmosSouthLongSlit] =
    GenPrism[ObservingMode, GmosSouthLongSlit]

  val gmosNorthImaging: Prism[ObservingMode, GmosNorthImaging] =
    GenPrism[ObservingMode, GmosNorthImaging]

  val gmosSouthImaging: Prism[ObservingMode, GmosSouthImaging] =
    GenPrism[ObservingMode, GmosSouthImaging]

  val flamingos2LongSlit: Prism[ObservingMode, Flamingos2LongSlit] =
    GenPrism[ObservingMode, Flamingos2LongSlit]
