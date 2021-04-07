// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Eq
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import lucuma.core.optics.SplitEpi

sealed abstract case class TruncatedBigDecimal private (value: BigDecimal)

object TruncatedBigDecimal {
  type Decimals    = Interval.Closed[1, 10]
  type IntDecimals = Int Refined Decimals

  def apply(value:         BigDecimal, decimals: IntDecimals): TruncatedBigDecimal =
    new TruncatedBigDecimal(
      value.setScale(decimals.value, BigDecimal.RoundingMode.FLOOR)
    ) {}

  def bigDecimal(decimals: IntDecimals): SplitEpi[BigDecimal, TruncatedBigDecimal] =
    SplitEpi(TruncatedBigDecimal(_, decimals), _.value)

  implicit val truncatedBigDecimalEq: Eq[TruncatedBigDecimal] =
    Eq.by(_.value)
}
