// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.Checkbox

import scalajs.js

case class CheckboxView[V[_]](
  id:        NonEmptyString,
  value:     V[Boolean],
  label:     TagMod,
  inputId:   js.UndefOr[String] = js.undefined, // id of the input element
  disabled:  js.UndefOr[Boolean] = js.undefined,
  clazz:     js.UndefOr[Css] = js.undefined,
  modifiers: Seq[TagMod] = Seq.empty
)(using val vl: ViewLike[V])
    extends ReactFnProps(CheckboxView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object CheckboxView {
  private type AnyF[_] = Any

  private def buildComponent[V[_]] = ScalaFnComponent[CheckboxView[V]] { props =>
    import props.given

    <.div(
      LucumaPrimeStyles.CheckboxWithLabel,
      Checkbox(
        id = props.id.value,
        checked = props.value.get.exists(identity),
        onChange = props.value.set,
        inputId = props.inputId,
        disabled = props.disabled,
        clazz = props.clazz,
        modifiers = props.modifiers
      ),
      <.label(^.htmlFor := props.id.value, props.label)
    )
  }

  private val component = buildComponent[AnyF]
}
