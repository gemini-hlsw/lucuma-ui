// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Eq
import lucuma.core.math.HourAngle
import lucuma.core.math.RightAscension
import lucuma.core.optics.SplitEpi

/**
 * A wrapper for RightAscension that is truncated to 3
 * decimal places of precision. This is used for input in
 * the UI to allow for lawful ValidFormatInput instances.
 *
 * @param ra The wrapped RightAscension. Guaranteed to have
 * no more than 3 decimals of precision.
 */
sealed abstract case class TruncatedRA private (ra: RightAscension)

object TruncatedRA {
  def apply(ra: RightAscension): TruncatedRA = {
    val microSecs = ra.toHourAngle.toMicroseconds / 1000 * 1000
    new TruncatedRA(RightAscension(HourAngle.fromMicroseconds(microSecs))) {}
  }

  val rightAscension: SplitEpi[RightAscension, TruncatedRA] =
    SplitEpi(TruncatedRA(_), _.ra)

  implicit val truncatedRAEq: Eq[TruncatedRA] = Eq.fromUniversalEquals
}
