// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.floatingui
import lucuma.react.floatingui.syntax.*
import lucuma.react.primereact.InputTextarea

import scalajs.js

case class FormInputTextArea(
  id:               NonEmptyString,
  value:            String,
  label:            js.UndefOr[TagMod] = js.undefined,
  size:             js.UndefOr[PlSize] = js.undefined,
  autoResize:       js.UndefOr[Boolean] = js.undefined,
  tooltip:          js.UndefOr[VdomNode] = js.undefined,
  tooltipPlacement: floatingui.Placement = floatingui.Placement.Top,
  modifiers:        Seq[TagMod] = Seq.empty
) extends ReactFnProps(FormInputTextArea.component):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object FormInputTextArea:
  private val component = ScalaFnComponent[FormInputTextArea] { props =>

    val group = <.div(
      LucumaPrimeStyles.FormField,
      InputTextarea(autoResize = props.autoResize)(^.id := props.id.value, ^.value := props.value)(
        props.modifiers*
      )
    )

    val input = props.tooltip.fold(group)(tt => group.withTooltip(tt, props.tooltipPlacement))

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id, size = props.size)(l)),
      input
    )
  }
