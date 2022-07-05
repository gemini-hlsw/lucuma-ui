// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Eq
import lucuma.core.optics.SplitEpi

import scala.annotation.unused
import scala.compiletime.ops.boolean.*
import scala.compiletime.ops.int.*

/**
 * A wrapper around a BigDecimal that is limited to a specified number of decimals places.
 *
 * The Dec type parameter must be a Singleton Int > 0 and < 10, which is enforced by the compiler.
 *
 * @param value
 *   The BigDecimal. It is guaranteed to have a scale of no more than Dec.
 * @param req
 *   Requirement that 0 < Dec < 10
 * @param vo
 *   Evidence that Dec is a Singleton type.
 */
sealed abstract case class TruncatedBigDecimal[Dec <: Int] private (value: BigDecimal)(implicit
  @unused req:                                                             Dec > 0 && Dec < 10,
  vo:                                                                      ValueOf[Dec]
) { val decimals: Int = vo.value }

object TruncatedBigDecimal {

  def apply[Dec <: Int](
    value:        BigDecimal
  )(implicit req: Dec > 0 && Dec < 10, vo: ValueOf[Dec]): TruncatedBigDecimal[Dec] =
    new TruncatedBigDecimal[Dec](
      value.setScale(vo.value, BigDecimal.RoundingMode.HALF_UP)
    ) {}

  def bigDecimal[Dec <: Int](implicit
    req: Dec > 0 && Dec < 10,
    vo:  ValueOf[Dec]
  ): SplitEpi[BigDecimal, TruncatedBigDecimal[Dec]] =
    SplitEpi(TruncatedBigDecimal(_), _.value)

  implicit def truncatedBigDecimalEq[Dec <: Int]: Eq[TruncatedBigDecimal[Dec]] =
    Eq.by(_.value)
}
