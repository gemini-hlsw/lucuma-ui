// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*

import scalajs.js

final case class BooleanRadioButtons[V[_]] private (
  trueButton:  RadioButtonView[V, Boolean],
  falseButton: RadioButtonView[V, Boolean]
):
  def toTrueFalseFragment: TagMod = React.Fragment(trueButton, falseButton)
  def toFalseTrueFragment: TagMod = React.Fragment(falseButton, trueButton)

object BooleanRadioButtons {
  def apply[V[_]: ViewLike](
    idBase:     NonEmptyString, // base id for the 2 radio buttons. `-true` or `-false` is appended to this.
    view:       V[Boolean],
    name:       NonEmptyString, // name of "radio button group"
    trueLabel:  NonEmptyString,
    falseLabel: NonEmptyString,
    trueClass:  js.UndefOr[Css] = js.undefined,
    falseClass: js.UndefOr[Css] = js.undefined,
    disabled:   js.UndefOr[Boolean] = js.undefined,
    required:   js.UndefOr[Boolean] = js.undefined
  ): BooleanRadioButtons[V] =
    BooleanRadioButtons(
      trueButton = RadioButtonView(
        id = NonEmptyString.unsafeFrom(idBase.value + "-true"),
        value = true,
        view = view,
        label = trueLabel,
        name = name,
        clazz = trueClass,
        disabled = disabled,
        required = required
      ),
      falseButton = RadioButtonView(
        id = NonEmptyString.unsafeFrom(idBase.value + "-false"),
        value = false,
        view = view,
        label = falseLabel,
        name = name,
        clazz = falseClass,
        disabled = disabled,
        required = required
      )
    )
}
