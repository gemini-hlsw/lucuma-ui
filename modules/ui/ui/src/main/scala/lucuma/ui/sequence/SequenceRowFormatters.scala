// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.enums.Instrument
import lucuma.core.math.Angle
import lucuma.core.math.Axis
import lucuma.core.math.Offset
import lucuma.core.math.Wavelength
import lucuma.core.util.TimeSpan

object SequenceRowFormatters:
  private val FormatOffsetArcSec: BigDecimal => NonEmptyString =
    n => NonEmptyString.unsafeFrom(f"$n%03.2f″")

  private val ComponentToArcSec: [A] => Offset.Component[A] => BigDecimal =
    [A] => (c: Offset.Component[A]) => Angle.signedDecimalArcseconds.get(c.toAngle)

  private val FormatOffsetComponent: [A] => Offset.Component[A] => NonEmptyString =
    [A] => (c: Offset.Component[A]) => FormatOffsetArcSec(ComponentToArcSec(c))

  val FormatOffsetP: Offset.P => NonEmptyString = FormatOffsetComponent[Axis.P]
  val FormatOffsetQ: Offset.Q => NonEmptyString = FormatOffsetComponent[Axis.Q]

  val FormatWavelength: Wavelength => NonEmptyString =
    w => NonEmptyString.unsafeFrom(f"${Wavelength.decimalNanometers.reverseGet(w)}%.0f")

  val FormatExposureTime: Instrument => TimeSpan => NonEmptyString =
    i =>
      NonEmptyString.unsafeFrom
        .compose: (s: BigDecimal) =>
          i match // GMOS and Flamingos2 are limited to integer exposure times.
            case Instrument.GmosNorth | Instrument.GmosSouth | Instrument.Flamingos2 => f"$s%.0f"
            case _                                                                   => f"$s%.2f"
        .compose(_.toSeconds)
