// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.Slider

import scalajs.js

case class SliderView(
  id:         NonEmptyString,
  value:      View[Double],
  label:      String,
  disabled:   js.UndefOr[Boolean] = js.undefined,
  clazz:      js.UndefOr[Css] = js.undefined,
  labelClass: js.UndefOr[Css] = js.undefined,
  modifiers:  Seq[TagMod] = Seq.empty
) extends ReactFnProps(SliderView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object SliderView {
  private val component = ScalaFnComponent[SliderView] { props =>
    <.div(
      props.clazz.getOrElse(Css.Empty),
      <.label(^.htmlFor := props.id.value, props.label, props.labelClass.toOption.orEmpty),
      Slider(id = props.id.value,
             value = props.value.get,
             onChange = props.value.set,
             disabled = props.disabled,
             clazz = props.clazz,
             modifiers = props.modifiers
      )
    )
  }
}
