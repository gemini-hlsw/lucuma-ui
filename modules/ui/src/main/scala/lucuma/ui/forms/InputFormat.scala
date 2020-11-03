// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.Validate
// import eu.timepit.refined.auto._
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import mouse.all._

/**
 * Convenience constructors for Prism to convert from A to String and optionally viceversa
 * It is meant to be used for Input widgets targeting some A
 */
object InputFormat {
  val id: InputFormat[String] = fromIso(Iso.id[String])

  def apply[A](_getOption: String => Option[A])(_reverseGet: A => String): InputFormat[A] =
    Format(_getOption, _reverseGet)

  /**
   * Build optics from a Prism
   */
  def fromPrism[A](prism: Prism[String, A]) =
    Format.fromPrism(prism)

  /**
   * Build optics from a Iso
   */
  def fromIso[A](iso: Iso[String, A]): InputFormat[A] =
    Format.fromIso(iso)

  val forInt: InputFormat[Int] =
    InputFormat[Int](_.parseIntOption)(_.toString)

  type RefinedInt[P] = Int Refined P
  def forRefinedInt[P](implicit v: Validate[Int, P]): InputFormat[RefinedInt[P]] =
    forInt ^<-? refinedIntPrism[P]

  def refinedIntPrism[P](implicit v: Validate[Int, P]): Prism[Int, RefinedInt[P]] =
    Prism[Int, RefinedInt[P]](i => refineV[P](i)(v).toOption)(_.value)

  val upperCase: InputFormat[String] = InputFormat[String](s => s.toUpperCase.some)(s => s)
}
