// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.arb

import cats.data.NonEmptyList
import cats.laws.discipline.arbitrary.*
import cats.syntax.all.*
import lucuma.core.enums.*
import lucuma.core.math.Wavelength
import lucuma.core.math.arb.ArbWavelength
import lucuma.core.util.arb.ArbEnumerated.given
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Gen

trait ArbBasicConfiguration {
  import ArbWavelength.given

  given Arbitrary[BasicConfiguration.GmosNorthLongSlit] =
    Arbitrary[BasicConfiguration.GmosNorthLongSlit](
      for {
        grating <- arbitrary[GmosNorthGrating]
        filter  <- arbitrary[Option[GmosNorthFilter]]
        fpu     <- arbitrary[GmosNorthFpu]
        cw      <- arbitrary[Wavelength]
      } yield BasicConfiguration.GmosNorthLongSlit(
        grating,
        filter,
        fpu,
        CentralWavelength(cw)
      )
    )

  given Arbitrary[BasicConfiguration.GmosSouthLongSlit] =
    Arbitrary[BasicConfiguration.GmosSouthLongSlit](
      for {
        grating <- arbitrary[GmosSouthGrating]
        filter  <- arbitrary[Option[GmosSouthFilter]]
        fpu     <- arbitrary[GmosSouthFpu]
        cw      <- arbitrary[Wavelength]
      } yield BasicConfiguration.GmosSouthLongSlit(
        grating,
        filter,
        fpu,
        CentralWavelength(cw)
      )
    )

  given Arbitrary[BasicConfiguration.GmosNorthImaging] =
    Arbitrary[BasicConfiguration.GmosNorthImaging](
      for {
        filter <- arbitrary[NonEmptyList[GmosNorthFilter]]
      } yield BasicConfiguration.GmosNorthImaging(filter)
    )

  given Arbitrary[BasicConfiguration.GmosSouthImaging] =
    Arbitrary[BasicConfiguration.GmosSouthImaging](
      for {
        filter <- arbitrary[NonEmptyList[GmosSouthFilter]]
      } yield BasicConfiguration.GmosSouthImaging(filter)
    )

  given Arbitrary[BasicConfiguration.Flamingos2LongSlit] =
    Arbitrary[BasicConfiguration.Flamingos2LongSlit](
      for {
        disperser <- arbitrary[Flamingos2Disperser]
        filter    <- arbitrary[Flamingos2Filter]
        fpu       <- arbitrary[Flamingos2Fpu]
      } yield BasicConfiguration.Flamingos2LongSlit(disperser, filter, fpu)
    )

  given Arbitrary[BasicConfiguration] = Arbitrary[BasicConfiguration](
    Gen.oneOf(
      arbitrary[BasicConfiguration.GmosNorthLongSlit],
      arbitrary[BasicConfiguration.GmosSouthLongSlit],
      arbitrary[BasicConfiguration.GmosNorthImaging],
      arbitrary[BasicConfiguration.GmosSouthImaging],
      arbitrary[BasicConfiguration.Flamingos2LongSlit]
    )
  )

  given Cogen[BasicConfiguration.GmosNorthLongSlit] =
    Cogen[
      (GmosNorthGrating, Option[GmosNorthFilter], GmosNorthFpu)
    ]
      .contramap(o =>
        (
          o.grating,
          o.filter,
          o.fpu
        )
      )

  given Cogen[BasicConfiguration.GmosSouthLongSlit] =
    Cogen[
      (GmosSouthGrating, Option[GmosSouthFilter], GmosSouthFpu)
    ]
      .contramap(o =>
        (
          o.grating,
          o.filter,
          o.fpu
        )
      )

  given Cogen[BasicConfiguration.Flamingos2LongSlit] =
    Cogen[
      (Flamingos2Disperser, Flamingos2Filter, Flamingos2Fpu)
    ]
      .contramap(o =>
        (
          o.disperser,
          o.filter,
          o.fpu
        )
      )

  given Cogen[BasicConfiguration.GmosNorthImaging] =
    Cogen[NonEmptyList[GmosNorthFilter]]
      .contramap(_.filter)

  given Cogen[BasicConfiguration.GmosSouthImaging] =
    Cogen[NonEmptyList[GmosSouthFilter]]
      .contramap(_.filter)

  given Cogen[BasicConfiguration] =
    Cogen[Either[
      BasicConfiguration.Flamingos2LongSlit,
      Either[
        BasicConfiguration.GmosNorthLongSlit,
        Either[
          BasicConfiguration.GmosSouthLongSlit,
          Either[
            BasicConfiguration.GmosNorthImaging,
            BasicConfiguration.GmosSouthImaging
          ]
        ]
      ]
    ]]
      .contramap {
        case f: BasicConfiguration.Flamingos2LongSlit => f.asLeft
        case n: BasicConfiguration.GmosNorthLongSlit  => n.asLeft.asRight
        case s: BasicConfiguration.GmosSouthLongSlit  => s.asLeft.asRight.asRight
        case n: BasicConfiguration.GmosNorthImaging   => n.asLeft.asRight.asRight.asRight
        case s: BasicConfiguration.GmosSouthImaging   => s.asRight.asRight.asRight.asRight
      }

}

object ArbBasicConfiguration extends ArbBasicConfiguration
