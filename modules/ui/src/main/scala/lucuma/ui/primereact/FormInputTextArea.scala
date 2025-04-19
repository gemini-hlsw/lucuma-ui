// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.InputTextarea
import lucuma.react.primereact.TooltipOptions

import scalajs.js

case class FormInputTextArea(
  id:             NonEmptyString,
  value:          String,
  label:          js.UndefOr[TagMod] = js.undefined,
  size:           js.UndefOr[PlSize] = js.undefined,
  autoResize:     js.UndefOr[Boolean] = js.undefined,
  tooltip:        js.UndefOr[String] = js.undefined,
  tooltipOptions: js.UndefOr[TooltipOptions] = js.undefined,
  clazz:          js.UndefOr[Css] = js.undefined,
  labelClass:     js.UndefOr[Css] = js.undefined,
  modifiers:      Seq[TagMod] = Seq.empty
) extends ReactFnProps(FormInputTextArea.component):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object FormInputTextArea:
  private val component = ScalaFnComponent[FormInputTextArea]: props =>

    val input = <.div(
      LucumaPrimeStyles.FormField |+| props.clazz.getOrElse(Css.Empty),
      InputTextarea(
        autoResize = props.autoResize,
        tooltip = props.tooltip,
        tooltipOptions = props.tooltipOptions
      )(^.id := props.id.value, ^.value := props.value)(
        props.modifiers*
      )
    )

    React.Fragment(
      props.label.map(l =>
        FormLabel(htmlFor = props.id, size = props.size, clazz = props.labelClass)(l)
      ),
      input
    )
