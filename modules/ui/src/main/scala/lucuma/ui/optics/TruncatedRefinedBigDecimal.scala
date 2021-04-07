// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Order
import eu.timepit.refined.refineV
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import eu.timepit.refined.cats._
import eu.timepit.refined.numeric.Interval
import lucuma.core.optics.SplitEpi

sealed abstract case class TruncatedRefinedBigDecimal[P] private (
  value: BigDecimal Refined P
)

object TruncatedRefinedBigDecimal {
  type Decimals    = Interval.Closed[1, 10]
  type IntDecimals = Int Refined Decimals

  def apply[P](value: BigDecimal Refined P, decimals: IntDecimals)(implicit
    v:                RefinedValidate[BigDecimal, P]
  ): Option[TruncatedRefinedBigDecimal[P]] = {
    val truncBD = value.value.setScale(decimals.value, BigDecimal.RoundingMode.HALF_UP)
    refineV[P](truncBD).toOption.map(v => new TruncatedRefinedBigDecimal[P](v) {})
  }

  def unsafeRefinedBigDecimal[P](
    decimals: IntDecimals
  )(implicit
    v:        RefinedValidate[BigDecimal, P]
  ): SplitEpi[BigDecimal Refined P, TruncatedRefinedBigDecimal[P]] =
    SplitEpi(TruncatedRefinedBigDecimal[P](_, decimals).get, _.value)

  implicit def truncatedRefinedBigDecimalOrder[P]: Order[TruncatedRefinedBigDecimal[P]] =
    Order.by(_.value)
}
