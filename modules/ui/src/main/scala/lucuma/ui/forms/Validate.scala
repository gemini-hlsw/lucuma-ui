// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.data.Validated
import lucuma.core.optics.Format
import monocle.Iso
import monocle.Prism
import cats.data.NonEmptyList

abstract class Validate[T, A, E] extends Serializable { self =>
  val reverseGet: A => T

  val getValidated: T => Validated[E, A]
}

case class InputValidate[A](
  getValidated: String => Validated[NonEmptyList[String], A],
  reverseGet:   A => String
) extends Validate[String, A, NonEmptyList[String]]

object InputValidate {
  val id: InputValidate[String] = fromIso(Iso.id[String])

  /**
   * Build optics from a Prism
   */
  def fromFormat[A](format: Format[String, A]) =
    InputValidate(
      format.getOption.andThen(o => Validated.fromOption(o, NonEmptyList.of("Invalid format"))),
      format.reverseGet
    )

  /**
   * Build optics from a Prism
   */
  def fromPrism[A](prism: Prism[String, A]): InputValidate[A] =
    fromFormat(Format.fromPrism(prism))

  /**
   * Build optics from a Iso
   */
  def fromIso[A](iso: Iso[String, A]): InputValidate[A] =
    fromFormat(Format.fromIso(iso))
}
