// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.Order
import eu.timepit.refined.refineV
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.{ Validate => RefinedValidate }
import eu.timepit.refined.cats._
import lucuma.core.optics.SplitEpi

/**
 * A wrapper around a Refined BigDecimal that is limited to a specified
 * number of decimals places.
 *
 * The P type parameter is the refinement.
 * The Dec type parameter must be a Singleton Int, which is enforced
 *    by the compiler. The compiler cannot, however, keep you from
 *    specifying negative numbers or extreme values. So, don't.
 *
 * @param value The refined BigDecimal. It is guaranteed to have a scale of no more than Dec.
 * @param vo Evidence that Dec is a Singleton type.
 */
sealed abstract case class TruncatedRefinedBigDecimal[P, Dec <: Int] private (
  value:       BigDecimal Refined P
)(implicit vo: ValueOf[Dec]) {
  val decimals: Int = vo.value
}

object TruncatedRefinedBigDecimal {

  def apply[P, Dec <: Int](value: BigDecimal Refined P)(implicit
    v:                            RefinedValidate[BigDecimal, P],
    vo:                           ValueOf[Dec]
  ): Option[TruncatedRefinedBigDecimal[P, Dec]] = {
    val truncBD = value.value.setScale(vo.value, BigDecimal.RoundingMode.HALF_UP)
    refineV[P](truncBD).toOption.map(v => new TruncatedRefinedBigDecimal[P, Dec](v) {})
  }

  /**
   * Gets a SplitEpi for the TruncatedRefinedBigDecimal.
   *
   * This is unsave under some occassions. For instance, say P is
   * Interval.Closed[1.23, 7.28] and 1 decimal place is specified
   * via Dec. If the input value to get() is 1.23, it will get rounded
   * down to 1.2, which is invalid and an exception is thrown. Since
   * we are rounding up, an imput value of 7.28 will be rounded up
   * to 7.3, which is also invalid (using FLOOR would be safe for the
   * upper bound, but not the lower bound.).
   *
   * In general, you have to specify at least as many decimals for the
   * rounding as the scale implicitly specified in the Interval bounds.
   * For this example, Dec would need to be set to at least 2.
   *
   * Non Interval refinements can also be problematic, so use this with
   * caution.
   */
  def unsafeRefinedBigDecimal[P, Dec <: Int](implicit
    v:  RefinedValidate[BigDecimal, P],
    vo: ValueOf[Dec]
  ): SplitEpi[BigDecimal Refined P, TruncatedRefinedBigDecimal[P, Dec]] =
    SplitEpi(TruncatedRefinedBigDecimal[P, Dec](_).get, _.value)

  implicit def truncatedRefinedBigDecimalOrder[P, Dec <: Int]
    : Order[TruncatedRefinedBigDecimal[P, Dec]] =
    Order.by(_.value)
}
