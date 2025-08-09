// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.arb

import cats.data.NonEmptyList
import cats.laws.discipline.arbitrary.*
import cats.syntax.all.*
import lucuma.core.enums.*
import lucuma.core.math.Offset
import lucuma.core.math.Wavelength
import lucuma.core.math.WavelengthDither
import lucuma.core.math.arb.ArbOffset
import lucuma.core.math.arb.ArbWavelength
import lucuma.core.math.arb.ArbWavelengthDither
import lucuma.core.util.arb.ArbEnumerated.given
import lucuma.schemas.model.CentralWavelength
import lucuma.schemas.model.ObservingMode
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Gen

trait ArbObservingMode {
  import ArbOffset.given
  import ArbWavelength.given
  import ArbWavelengthDither.given

  given Arbitrary[ObservingMode.GmosNorthLongSlit] =
    Arbitrary[ObservingMode.GmosNorthLongSlit](
      for {
        initialGrating            <- arbitrary[GmosNorthGrating]
        grating                   <- arbitrary[GmosNorthGrating]
        initialFilter             <- arbitrary[Option[GmosNorthFilter]]
        filter                    <- arbitrary[Option[GmosNorthFilter]]
        initialFpu                <- arbitrary[GmosNorthFpu]
        fpu                       <- arbitrary[GmosNorthFpu]
        initialCentralWavelength  <- arbitrary[Wavelength]
        centralWavelength         <- arbitrary[Wavelength]
        defaultXBin               <- arbitrary[GmosXBinning]
        explicitXBin              <- arbitrary[Option[GmosXBinning]]
        defaultYBin               <- arbitrary[GmosYBinning]
        explicitYBin              <- arbitrary[Option[GmosYBinning]]
        defaultAmpReadMode        <- arbitrary[GmosAmpReadMode]
        explicitAmpReadMode       <- arbitrary[Option[GmosAmpReadMode]]
        defaultAmpGain            <- arbitrary[GmosAmpGain]
        explicitAmpGain           <- arbitrary[Option[GmosAmpGain]]
        defaultRoi                <- arbitrary[GmosRoi]
        explicitRoi               <- arbitrary[Option[GmosRoi]]
        defaultWavelengthDithers  <- arbitrary[NonEmptyList[WavelengthDither]]
        explicitWavelengthDithers <- arbitrary[Option[NonEmptyList[WavelengthDither]]]
        defaultSpatialOffsets     <- arbitrary[NonEmptyList[Offset.Q]]
        explicitSpatialOffsets    <- arbitrary[Option[NonEmptyList[Offset.Q]]]
      } yield ObservingMode.GmosNorthLongSlit(
        initialGrating,
        grating,
        initialFilter,
        filter,
        initialFpu,
        fpu,
        CentralWavelength(initialCentralWavelength),
        CentralWavelength(centralWavelength),
        defaultXBin,
        explicitXBin,
        defaultYBin,
        explicitYBin,
        defaultAmpReadMode,
        explicitAmpReadMode,
        defaultAmpGain,
        explicitAmpGain,
        defaultRoi,
        explicitRoi,
        defaultWavelengthDithers,
        explicitWavelengthDithers,
        defaultSpatialOffsets,
        explicitSpatialOffsets
      )
    )

  given Arbitrary[ObservingMode.GmosSouthLongSlit] =
    Arbitrary[ObservingMode.GmosSouthLongSlit](
      for {
        initialGrating            <- arbitrary[GmosSouthGrating]
        grating                   <- arbitrary[GmosSouthGrating]
        initialFilter             <- arbitrary[Option[GmosSouthFilter]]
        filter                    <- arbitrary[Option[GmosSouthFilter]]
        initialFpu                <- arbitrary[GmosSouthFpu]
        fpu                       <- arbitrary[GmosSouthFpu]
        initialCentralWavelength  <- arbitrary[Wavelength]
        centralWavelength         <- arbitrary[Wavelength]
        defaultXBin               <- arbitrary[GmosXBinning]
        explicitXBin              <- arbitrary[Option[GmosXBinning]]
        defaultYBin               <- arbitrary[GmosYBinning]
        explicitYBin              <- arbitrary[Option[GmosYBinning]]
        defaultAmpReadMode        <- arbitrary[GmosAmpReadMode]
        explicitAmpReadMode       <- arbitrary[Option[GmosAmpReadMode]]
        defaultAmpGain            <- arbitrary[GmosAmpGain]
        explicitAmpGain           <- arbitrary[Option[GmosAmpGain]]
        defaultRoi                <- arbitrary[GmosRoi]
        explicitRoi               <- arbitrary[Option[GmosRoi]]
        defaultWavelengthDithers  <- arbitrary[NonEmptyList[WavelengthDither]]
        explicitWavelengthDithers <- arbitrary[Option[NonEmptyList[WavelengthDither]]]
        defaultSpatialOffsets     <- arbitrary[NonEmptyList[Offset.Q]]
        explicitSpatialOffsets    <- arbitrary[Option[NonEmptyList[Offset.Q]]]
      } yield ObservingMode.GmosSouthLongSlit(
        initialGrating,
        grating,
        initialFilter,
        filter,
        initialFpu,
        fpu,
        CentralWavelength(initialCentralWavelength),
        CentralWavelength(centralWavelength),
        defaultXBin,
        explicitXBin,
        defaultYBin,
        explicitYBin,
        defaultAmpReadMode,
        explicitAmpReadMode,
        defaultAmpGain,
        explicitAmpGain,
        defaultRoi,
        explicitRoi,
        defaultWavelengthDithers,
        explicitWavelengthDithers,
        defaultSpatialOffsets,
        explicitSpatialOffsets
      )
    )

  given Cogen[ObservingMode.GmosNorthLongSlit] =
    Cogen[
      (GmosNorthGrating,
       GmosNorthGrating,
       Option[GmosNorthFilter],
       Option[GmosNorthFilter],
       GmosNorthFpu,
       GmosNorthFpu,
       Wavelength,
       Wavelength,
       GmosXBinning,
       Option[GmosXBinning],
       GmosYBinning,
       Option[GmosYBinning],
       GmosAmpReadMode,
       Option[GmosAmpReadMode],
       GmosAmpGain,
       Option[GmosAmpGain],
       GmosRoi,
       Option[GmosRoi],
       NonEmptyList[WavelengthDither],
       Option[NonEmptyList[WavelengthDither]],
       NonEmptyList[Offset.Q],
       Option[NonEmptyList[Offset.Q]]
      )
    ]
      .contramap(o =>
        (o.initialGrating,
         o.grating,
         o.initialFilter,
         o.filter,
         o.initialFpu,
         o.fpu,
         o.initialCentralWavelength.value,
         o.centralWavelength.value,
         o.defaultXBin,
         o.explicitXBin,
         o.defaultYBin,
         o.explicitYBin,
         o.defaultAmpReadMode,
         o.explicitAmpReadMode,
         o.defaultAmpGain,
         o.explicitAmpGain,
         o.defaultRoi,
         o.explicitRoi,
         o.defaultWavelengthDithers,
         o.explicitWavelengthDithers,
         o.defaultSpatialOffsets,
         o.explicitSpatialOffsets
        )
      )

  given Cogen[ObservingMode.GmosSouthLongSlit] =
    Cogen[
      (GmosSouthGrating,
       GmosSouthGrating,
       Option[GmosSouthFilter],
       Option[GmosSouthFilter],
       GmosSouthFpu,
       GmosSouthFpu,
       Wavelength,
       Wavelength,
       GmosXBinning,
       Option[GmosXBinning],
       GmosYBinning,
       Option[GmosYBinning],
       GmosAmpReadMode,
       Option[GmosAmpReadMode],
       GmosAmpGain,
       Option[GmosAmpGain],
       GmosRoi,
       Option[GmosRoi],
       NonEmptyList[WavelengthDither],
       Option[NonEmptyList[WavelengthDither]],
       NonEmptyList[Offset.Q],
       Option[NonEmptyList[Offset.Q]]
      )
    ]
      .contramap(o =>
        (o.initialGrating,
         o.grating,
         o.initialFilter,
         o.filter,
         o.initialFpu,
         o.fpu,
         o.initialCentralWavelength.value,
         o.centralWavelength.value,
         o.defaultXBin,
         o.explicitXBin,
         o.defaultYBin,
         o.explicitYBin,
         o.defaultAmpReadMode,
         o.explicitAmpReadMode,
         o.defaultAmpGain,
         o.explicitAmpGain,
         o.defaultRoi,
         o.explicitRoi,
         o.defaultWavelengthDithers,
         o.explicitWavelengthDithers,
         o.defaultSpatialOffsets,
         o.explicitSpatialOffsets
        )
      )

  given Arbitrary[ObservingMode.GmosNorthImaging] =
    Arbitrary[ObservingMode.GmosNorthImaging](
      for {
        initialFilters              <- arbitrary[NonEmptyList[GmosNorthFilter]]
        filters                     <- arbitrary[NonEmptyList[GmosNorthFilter]]
        defaultMultipleFiltersMode  <- arbitrary[MultipleFiltersMode]
        explicitMultipleFiltersMode <- arbitrary[Option[MultipleFiltersMode]]
        defaultBin                  <- arbitrary[GmosBinning]
        explicitBin                 <- arbitrary[Option[GmosBinning]]
        defaultAmpReadMode          <- arbitrary[GmosAmpReadMode]
        explicitAmpReadMode         <- arbitrary[Option[GmosAmpReadMode]]
        defaultAmpGain              <- arbitrary[GmosAmpGain]
        explicitAmpGain             <- arbitrary[Option[GmosAmpGain]]
        defaultRoi                  <- arbitrary[GmosRoi]
        explicitRoi                 <- arbitrary[Option[GmosRoi]]
        offsets                     <- arbitrary[List[Offset]]
      } yield ObservingMode.GmosNorthImaging(
        initialFilters,
        filters,
        offsets,
        defaultMultipleFiltersMode,
        explicitMultipleFiltersMode,
        defaultBin,
        explicitBin,
        defaultAmpReadMode,
        explicitAmpReadMode,
        defaultAmpGain,
        explicitAmpGain,
        defaultRoi,
        explicitRoi
      )
    )

  given Arbitrary[ObservingMode.GmosSouthImaging] =
    Arbitrary[ObservingMode.GmosSouthImaging](
      for {
        initialFilters              <- arbitrary[NonEmptyList[GmosSouthFilter]]
        filters                     <- arbitrary[NonEmptyList[GmosSouthFilter]]
        defaultMultipleFiltersMode  <- arbitrary[MultipleFiltersMode]
        explicitMultipleFiltersMode <- arbitrary[Option[MultipleFiltersMode]]
        defaultBin                  <- arbitrary[GmosBinning]
        explicitBin                 <- arbitrary[Option[GmosBinning]]
        defaultAmpReadMode          <- arbitrary[GmosAmpReadMode]
        explicitAmpReadMode         <- arbitrary[Option[GmosAmpReadMode]]
        defaultAmpGain              <- arbitrary[GmosAmpGain]
        explicitAmpGain             <- arbitrary[Option[GmosAmpGain]]
        defaultRoi                  <- arbitrary[GmosRoi]
        explicitRoi                 <- arbitrary[Option[GmosRoi]]
        offsets                     <- arbitrary[List[Offset]]
      } yield ObservingMode.GmosSouthImaging(
        initialFilters,
        filters,
        offsets,
        defaultMultipleFiltersMode,
        explicitMultipleFiltersMode,
        defaultBin,
        explicitBin,
        defaultAmpReadMode,
        explicitAmpReadMode,
        defaultAmpGain,
        explicitAmpGain,
        defaultRoi,
        explicitRoi
      )
    )

  given Arbitrary[ObservingMode.Flamingos2LongSlit] =
    Arbitrary[ObservingMode.Flamingos2LongSlit](
      for {
        initialDisperser   <- arbitrary[Flamingos2Disperser]
        disperser          <- arbitrary[Flamingos2Disperser]
        initialFilter      <- arbitrary[Flamingos2Filter]
        filter             <- arbitrary[Flamingos2Filter]
        initialFpu         <- arbitrary[Flamingos2Fpu]
        fpu                <- arbitrary[Flamingos2Fpu]
        explicitReadMode   <- arbitrary[Option[Flamingos2ReadMode]]
        explicitReads      <- arbitrary[Option[Flamingos2Reads]]
        defaultDecker      <- arbitrary[Flamingos2Decker]
        explicitDecker     <- arbitrary[Option[Flamingos2Decker]]
        defaultReadoutMode <- arbitrary[Flamingos2ReadoutMode]
        expicitReadoutMode <- arbitrary[Option[Flamingos2ReadoutMode]]
        defaultOffsets     <- arbitrary[NonEmptyList[Offset]]
        explicitOffsets    <- arbitrary[Option[NonEmptyList[Offset]]]
      } yield ObservingMode.Flamingos2LongSlit(
        initialDisperser,
        disperser,
        initialFilter,
        filter,
        initialFpu,
        fpu,
        explicitReadMode,
        explicitReads,
        defaultDecker,
        explicitDecker,
        defaultReadoutMode,
        expicitReadoutMode,
        defaultOffsets,
        explicitOffsets
      )
    )

  given Cogen[ObservingMode.Flamingos2LongSlit] =
    Cogen[
      (Flamingos2Disperser,
       Flamingos2Disperser,
       Flamingos2Filter,
       Flamingos2Filter,
       Flamingos2Fpu,
       Flamingos2Fpu,
       Option[Flamingos2ReadMode],
       Option[Flamingos2Reads],
       Flamingos2Decker,
       Option[Flamingos2Decker],
       Flamingos2ReadoutMode,
       Option[Flamingos2ReadoutMode],
       NonEmptyList[Offset],
       Option[NonEmptyList[Offset]]
      )
    ]
      .contramap(o =>
        (
          o.initialDisperser,
          o.disperser,
          o.initialFilter,
          o.filter,
          o.initialFpu,
          o.fpu,
          o.explicitReadMode,
          o.explicitReads,
          o.defaultDecker,
          o.explicitDecker,
          o.defaultReadoutMode,
          o.explicitReadoutMode,
          o.defaultOffsets,
          o.explicitOffsets
        )
      )

  given Cogen[ObservingMode.GmosNorthImaging] =
    Cogen[
      (NonEmptyList[GmosNorthFilter],
       NonEmptyList[GmosNorthFilter],
       MultipleFiltersMode,
       Option[MultipleFiltersMode],
       GmosBinning,
       Option[GmosBinning],
       GmosAmpReadMode,
       Option[GmosAmpReadMode],
       GmosAmpGain,
       Option[GmosAmpGain],
       GmosRoi,
       Option[GmosRoi],
       List[Offset]
      )
    ]
      .contramap(o =>
        (
          o.initialFilters,
          o.filters,
          o.defaultMultipleFiltersMode,
          o.explicitMultipleFiltersMode,
          o.defaultBin,
          o.explicitBin,
          o.defaultAmpReadMode,
          o.explicitAmpReadMode,
          o.defaultAmpGain,
          o.explicitAmpGain,
          o.defaultRoi,
          o.explicitRoi,
          o.offsets
        )
      )

  given Cogen[ObservingMode.GmosSouthImaging] =
    Cogen[
      (NonEmptyList[GmosSouthFilter],
       NonEmptyList[GmosSouthFilter],
       MultipleFiltersMode,
       Option[MultipleFiltersMode],
       GmosBinning,
       Option[GmosBinning],
       GmosAmpReadMode,
       Option[GmosAmpReadMode],
       GmosAmpGain,
       Option[GmosAmpGain],
       GmosRoi,
       Option[GmosRoi],
       List[Offset]
      )
    ]
      .contramap(o =>
        (
          o.initialFilters,
          o.filters,
          o.defaultMultipleFiltersMode,
          o.explicitMultipleFiltersMode,
          o.defaultBin,
          o.explicitBin,
          o.defaultAmpReadMode,
          o.explicitAmpReadMode,
          o.defaultAmpGain,
          o.explicitAmpGain,
          o.defaultRoi,
          o.explicitRoi,
          o.offsets
        )
      )

  given Arbitrary[ObservingMode] = Arbitrary[ObservingMode](
    Gen.oneOf(
      arbitrary[ObservingMode.GmosNorthLongSlit],
      arbitrary[ObservingMode.GmosSouthLongSlit],
      arbitrary[ObservingMode.GmosNorthImaging],
      arbitrary[ObservingMode.GmosSouthImaging],
      arbitrary[ObservingMode.Flamingos2LongSlit]
    )
  )

  given Cogen[ObservingMode] =
    Cogen[Either[
      ObservingMode.Flamingos2LongSlit,
      Either[ObservingMode.GmosNorthLongSlit, Either[ObservingMode.GmosSouthLongSlit,
                                                     Either[ObservingMode.GmosNorthImaging,
                                                            ObservingMode.GmosSouthImaging
                                                     ]
      ]]
    ]]
      .contramap {
        case f: ObservingMode.Flamingos2LongSlit => f.asLeft
        case n: ObservingMode.GmosNorthLongSlit  => n.asLeft.asRight
        case s: ObservingMode.GmosSouthLongSlit  => s.asLeft.asRight.asRight
        case n: ObservingMode.GmosNorthImaging   => n.asLeft.asRight.asRight.asRight
        case s: ObservingMode.GmosSouthImaging   => s.asRight.asRight.asRight.asRight
      }

}

object ArbObservingMode extends ArbObservingMode
