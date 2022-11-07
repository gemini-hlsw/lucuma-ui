// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all._
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^._
import react.common.*
import react.primereact.Checkbox

import scalajs.js

case class CheckboxView(
  id:        NonEmptyString,
  value:     View[Boolean],
  label:     String,
  inputId:   js.UndefOr[String] = js.undefined, // id of the input element
  disabled:  js.UndefOr[Boolean] = js.undefined,
  clazz:     js.UndefOr[Css] = js.undefined,
  modifiers: Seq[TagMod] = Seq.empty
) extends ReactFnProps(CheckboxView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object CheckboxView {
  private val component = ScalaFnComponent[CheckboxView] { props =>
    <.div(
      LucumaStyles.CheckboxWithLabel,
      Checkbox(
        id = props.id.value,
        checked = props.value.get,
        onChange = props.value.set,
        inputId = props.inputId,
        disabled = props.disabled,
        clazz = props.clazz,
        modifiers = props.modifiers
      ),
      <.label(^.htmlFor := props.id.value, props.label)
    )
  }
}
