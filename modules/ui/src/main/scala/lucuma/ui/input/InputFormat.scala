// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.input

import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism

/**
 * Convenience constructors for Prism to convert from A to String and optionally viceversa It is
 * meant to be used for Input widgets targeting some A
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
}
