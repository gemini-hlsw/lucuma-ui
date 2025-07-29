// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.RadioButton

import scalajs.js

final case class RadioButtonView[V[_], A](
  id:        NonEmptyString,
  value:     A,                                         // The value this radio button will assign to the ExternalValue
  view:      V[A],
  label:     NonEmptyString,
  name:      js.UndefOr[NonEmptyString] = js.undefined, // name of the "radio button group"
  inputId:   js.UndefOr[String] = js.undefined,
  disabled:  js.UndefOr[Boolean] = js.undefined,
  clazz:     js.UndefOr[Css] = js.undefined,
  required:  js.UndefOr[Boolean] = js.undefined,
  onChange:  js.UndefOr[(A, Boolean) => Callback] =
    js.undefined, // shouldn't usually need to use this, the ViewLike is set internally.
  modifiers: Seq[TagMod] = Seq.empty
)(using val eqA: Eq[A], val ev: ViewLike[V])
    extends ReactFnProps(RadioButtonView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object RadioButtonView {
  type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[RadioButtonView[V, A]] { props =>
    import props.given

    <.div(
      LucumaPrimeStyles.RadioButtonWithLabel,
      RadioButton(
        id = props.id.value,
        value = props.value,
        inputId = props.inputId,
        name = props.name.map(_.value),
        disabled = props.disabled,
        clazz = props.clazz,
        required = props.required,
        modifiers = props.modifiers,
        checked = props.view.get.exists(_ === props.value),
        onChange = (a, checked) =>
          props.view.set(a).when_(checked)
            >> props.onChange.toOption.foldMap(_(a, checked))
      ),
      <.label(^.htmlFor := props.id.value, props.label.value)
    )
  }

  private val component = buildComponent[AnyF, Any]
}
