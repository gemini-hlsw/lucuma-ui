// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Eq
import lucuma.core.optics.SplitEpi

/**
 * A wrapper around a BigDecimal that is limited to a specified
 * number of decimals places.
 *
 * The Dec type parameter must be a Singleton Int, which is enforced
 *    by the compiler. The compiler cannot, however, keep you from
 *    specifying negative numbers or extreme values. So, don't.
 *
 * @param value The BigDecimal. It is guaranteed to have a scale of no more than Dec.
 * @param vo Evidence that Dec is a Singleton type.
 */
sealed abstract case class TruncatedBigDecimal[Dec <: Int] private (value: BigDecimal)(implicit
  vo:                                                                      ValueOf[Dec]
) { val decimals: Int = vo.value }

object TruncatedBigDecimal {

  def apply[Dec <: Int](value: BigDecimal)(implicit vo: ValueOf[Dec]): TruncatedBigDecimal[Dec] =
    new TruncatedBigDecimal[Dec](
      value.setScale(vo.value, BigDecimal.RoundingMode.HALF_UP)
    ) {}

  def bigDecimal[Dec <: Int](implicit
    vo:                        ValueOf[Dec]
  ): SplitEpi[BigDecimal, TruncatedBigDecimal[Dec]] =
    SplitEpi(TruncatedBigDecimal(_), _.value)

  implicit def truncatedBigDecimalEq[Dec <: Int]: Eq[TruncatedBigDecimal[Dec]] =
    Eq.by(_.value)
}
