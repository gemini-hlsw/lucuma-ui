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

  given Arbitrary[ObservingMode] = Arbitrary[ObservingMode](
    Gen.oneOf(
      arbitrary[ObservingMode.GmosNorthLongSlit],
      arbitrary[ObservingMode.GmosSouthLongSlit]
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

  given Arbitrary[ObservingMode.F2LongSlit] =
    Arbitrary[ObservingMode.F2LongSlit](
      for {
        initialDisperser   <- arbitrary[F2Disperser]
        disperser          <- arbitrary[F2Disperser]
        initialFilter      <- arbitrary[F2Filter]
        filter             <- arbitrary[F2Filter]
        initialFpu         <- arbitrary[F2Fpu]
        fpu                <- arbitrary[F2Fpu]
        defaultReadMode    <- arbitrary[F2ReadMode]
        explicitReadMode   <- arbitrary[Option[F2ReadMode]]
        defaultReads       <- arbitrary[F2Reads]
        explicitReads      <- arbitrary[Option[F2Reads]]
        defaultDecker      <- arbitrary[F2Decker]
        explicitDecker     <- arbitrary[Option[F2Decker]]
        defaultReadoutMode <- arbitrary[F2ReadoutMode]
        expicitReadoutMode <- arbitrary[Option[F2ReadoutMode]]
      } yield ObservingMode.F2LongSlit(
        initialDisperser,
        disperser,
        initialFilter,
        filter,
        initialFpu,
        fpu,
        defaultReadMode,
        explicitReadMode,
        defaultReads,
        explicitReads,
        defaultDecker,
        explicitDecker,
        defaultReadoutMode,
        expicitReadoutMode
      )
    )

  given Cogen[ObservingMode.F2LongSlit] =
    Cogen[
      (F2Disperser,
       F2Disperser,
       F2Filter,
       F2Filter,
       F2Fpu,
       F2Fpu,
       F2ReadMode,
       Option[F2ReadMode],
       F2Reads,
       Option[F2Reads],
       F2Decker,
       Option[F2Decker],
       F2ReadoutMode,
       Option[F2ReadoutMode]
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
          o.defaultReadMode,
          o.explicitReadMode,
          o.defaultReads,
          o.explicitReads,
          o.defaultDecker,
          o.explicitDecker,
          o.defaultReadoutMode,
          o.explicitReadoutMode
        )
      )

  given Cogen[ObservingMode] =
    Cogen[Either[ObservingMode.F2LongSlit, Either[ObservingMode.GmosNorthLongSlit,
                                                  ObservingMode.GmosSouthLongSlit
    ]]]
      .contramap {
        case n: ObservingMode.GmosNorthLongSlit => n.asLeft.asRight
        case s: ObservingMode.GmosSouthLongSlit => s.asRight.asRight
        case f: ObservingMode.F2LongSlit        => f.asLeft
      }

}

object ArbObservingMode extends ArbObservingMode
