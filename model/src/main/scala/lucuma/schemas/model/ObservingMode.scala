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
    case _: ObservingMode.GmosNorthLongSlit => ObservingModeType.GmosNorthLongSlit
    case _: ObservingMode.GmosSouthLongSlit => ObservingModeType.GmosSouthLongSlit
    case _: ObservingMode.F2LongSlit        => ObservingModeType.Flamingos2LongSlit

  def gmosFpuAlternative: Option[Either[GmosNorthFpu, GmosSouthFpu]] = this match
    case o: ObservingMode.GmosNorthLongSlit => o.fpu.asLeft.some
    case o: ObservingMode.GmosSouthLongSlit => o.fpu.asRight.some
    case _                                  => none

  def siteFor: Site = this match
    case _: ObservingMode.GmosNorthLongSlit => Site.GN
    case _: ObservingMode.GmosSouthLongSlit => Site.GS
    case _: ObservingMode.F2LongSlit        => Site.GS

  def toBasicConfiguration: BasicConfiguration = this match
    case n: ObservingMode.GmosNorthLongSlit =>
      BasicConfiguration.GmosNorthLongSlit(n.grating, n.filter, n.fpu, n.centralWavelength)
    case s: ObservingMode.GmosSouthLongSlit =>
      BasicConfiguration.GmosSouthLongSlit(s.grating, s.filter, s.fpu, s.centralWavelength)
    case f: ObservingMode.F2LongSlit        =>
      BasicConfiguration.F2LongSlit(f.disperser, f.filter, f.fpu)
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
            c.downField("flamingos2LongSlit").as[F2LongSlit]

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

  case class F2LongSlit(
    initialDisperser:    F2Disperser,
    disperser:           F2Disperser,
    initialFilter:       F2Filter,
    filter:              F2Filter,
    initialFpu:          F2Fpu,
    fpu:                 F2Fpu,
    defaultReadMode:     F2ReadMode,
    explicitReadMode:    Option[F2ReadMode],
    defaultReads:        F2Reads,
    explicitReads:       Option[F2Reads],
    defaultDecker:       F2Decker,
    explicitDecker:      Option[F2Decker],
    defaultReadoutMode:  F2ReadoutMode,
    explicitReadoutMode: Option[F2ReadoutMode]
  ) extends ObservingMode(Instrument.GmosSouth) derives Eq:
    val readMode: F2ReadMode       =
      explicitReadMode.getOrElse(defaultReadMode)
    val reads: F2Reads             =
      explicitReads.getOrElse(defaultReads)
    val decker: F2Decker           =
      explicitDecker.getOrElse(defaultDecker)
    val readoutMode: F2ReadoutMode =
      explicitReadoutMode.getOrElse(defaultReadoutMode)

    def isCustomized: Boolean =
      initialDisperser =!= disperser ||
        initialFilter =!= filter ||
        initialFpu =!= fpu ||
        explicitReadMode.exists(_ =!= defaultReadMode) ||
        explicitReads.exists(_ =!= defaultReads) ||
        explicitDecker.exists(_ =!= defaultDecker) ||
        explicitReadoutMode.exists(_ =!= defaultReadoutMode)

    def revertCustomizations: F2LongSlit =
      this.copy(
        disperser = this.initialDisperser,
        filter = this.initialFilter,
        fpu = this.initialFpu,
        explicitReadMode = None,
        explicitReads = None,
        explicitDecker = None,
        explicitReadoutMode = None
      )

  object F2LongSlit:
    given Decoder[F2LongSlit] = deriveDecoder

    val initialDisperser: Lens[F2LongSlit, F2Disperser]              =
      Focus[F2LongSlit](_.initialDisperser)
    val disperser: Lens[F2LongSlit, F2Disperser]                     =
      Focus[F2LongSlit](_.disperser)
    val initialFilter: Lens[F2LongSlit, F2Filter]                    =
      Focus[F2LongSlit](_.initialFilter)
    val filter: Lens[F2LongSlit, F2Filter]                           =
      Focus[F2LongSlit](_.filter)
    val initialFpu: Lens[F2LongSlit, F2Fpu]                          =
      Focus[F2LongSlit](_.initialFpu)
    val fpu: Lens[F2LongSlit, F2Fpu]                                 =
      Focus[F2LongSlit](_.fpu)
    val defaultReadMode: Lens[F2LongSlit, F2ReadMode]                =
      Focus[F2LongSlit](_.defaultReadMode)
    val explicitReadMode: Lens[F2LongSlit, Option[F2ReadMode]]       =
      Focus[F2LongSlit](_.explicitReadMode)
    val defaultReads: Lens[F2LongSlit, F2Reads]                      =
      Focus[F2LongSlit](_.defaultReads)
    val explicitReads: Lens[F2LongSlit, Option[F2Reads]]             =
      Focus[F2LongSlit](_.explicitReads)
    val defaultDecker: Lens[F2LongSlit, F2Decker]                    =
      Focus[F2LongSlit](_.defaultDecker)
    val explicitDecker: Lens[F2LongSlit, Option[F2Decker]]           =
      Focus[F2LongSlit](_.explicitDecker)
    val defaultReadoutMode: Lens[F2LongSlit, F2ReadoutMode]          =
      Focus[F2LongSlit](_.defaultReadoutMode)
    val explicitReadoutMode: Lens[F2LongSlit, Option[F2ReadoutMode]] =
      Focus[F2LongSlit](_.explicitReadoutMode)

  val gmosNorthLongSlit: Prism[ObservingMode, GmosNorthLongSlit] =
    GenPrism[ObservingMode, GmosNorthLongSlit]

  val gmosSouthLongSlit: Prism[ObservingMode, GmosSouthLongSlit] =
    GenPrism[ObservingMode, GmosSouthLongSlit]

  val f2LongSlit: Prism[ObservingMode, F2LongSlit] =
    GenPrism[ObservingMode, F2LongSlit]
