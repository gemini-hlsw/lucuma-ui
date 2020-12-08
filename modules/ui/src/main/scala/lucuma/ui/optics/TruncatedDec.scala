// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Eq
import lucuma.core.math.Angle
import lucuma.core.math.Declination
import lucuma.core.optics.SplitEpi

/**
 * A wrapper for Declination that is truncated to 2
 * decimal places of precision. This is used for input in
 * the UI to allow for lawful ValidFormatInput instances.
 *
 * @param dec The wrapped Declination. Guaranteed to have
 * no more than 2 digits of precision.
 */
sealed abstract case class TruncatedDec private (dec: Declination)

object TruncatedDec {
  def apply(dec: Declination): TruncatedDec = {
    val microArcSecs = dec.toAngle.toMicroarcseconds / 10000 * 10000
    new TruncatedDec(Declination.fromAngleWithCarry(Angle.fromMicroarcseconds(microArcSecs))._1) {}
  }

  val declination: SplitEpi[Declination, TruncatedDec] =
    SplitEpi(TruncatedDec(_), _.dec)

  implicit val truncatedDecEq: Eq[TruncatedDec] = Eq.fromUniversalEquals
}
