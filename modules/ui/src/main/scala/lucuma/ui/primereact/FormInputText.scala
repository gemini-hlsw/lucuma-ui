// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.floatingui
import lucuma.react.floatingui.syntax.*
import lucuma.react.primereact.InputText
import lucuma.react.primereact.PrimeStyles
import lucuma.typed.primereact.components.Button as CButton

import scalajs.js

case class FormInputText(
  id:               NonEmptyString,
  value:            js.UndefOr[String] = js.undefined,
  label:            js.UndefOr[TagMod] = js.undefined,
  units:            js.UndefOr[String] = js.undefined,
  preAddons:        List[TagMod | CButton.Builder] = List.empty,
  postAddons:       List[TagMod | CButton.Builder] = List.empty,
  size:             js.UndefOr[PlSize] = js.undefined,
  groupClass:       js.UndefOr[Css] = js.undefined,
  inputClass:       js.UndefOr[Css] = js.undefined,
  labelClass:       js.UndefOr[Css] = js.undefined,
  disabled:         js.UndefOr[Boolean] = js.undefined,
  placeholder:      js.UndefOr[String] = js.undefined,
  tooltip:          js.UndefOr[VdomNode] = js.undefined,
  tooltipPlacement: floatingui.Placement = floatingui.Placement.Top,
  onFocus:          js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onBlur:           js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onChange:         js.UndefOr[ReactEventFromInput => Callback] = js.undefined,
  onKeyDown:        js.UndefOr[ReactKeyboardEventFromInput => Callback] = js.undefined,
  modifiers:        Seq[TagMod] = Seq.empty
) extends ReactFnProps(FormInputText.component):
  def addModifiers(modifiers: Seq[TagMod])                  = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)                      = addModifiers(mods)
  def apply(mods:             TagMod*)                      = addModifiers(mods)
  def addPostAddons(addons: List[TagMod | CButton.Builder]) =
    copy(postAddons = this.postAddons ++ addons)
  def withPostAddons(addons:  (TagMod | CButton.Builder)*)  = addPostAddons(addons.toList)

object FormInputText:
  private val component = ScalaFnComponent[FormInputText]: props =>
    val sizeCls = props.size.toOption.map(_.cls).orEmpty

    // units are always first
    val postAddons = props.units.fold(props.postAddons.build(props.size)) { units =>
      (<.span(^.cls := LucumaPrimeStyles.BlendedAddon.htmlClass, units) :: props.postAddons)
        .build(props.size)
    }

    val group = <.div(
      PrimeStyles.InputGroup |+| LucumaPrimeStyles.FormField |+| props.groupClass.toOption.orEmpty,
      props.preAddons.build(props.size),
      InputText(
        id = props.id.value,
        value = props.value,
        clazz = props.inputClass.toOption.orEmpty |+| sizeCls,
        disabled = props.disabled,
        placeholder = props.placeholder,
        onFocus = props.onFocus,
        onBlur = props.onBlur,
        onChange = props.onChange,
        onKeyDown = props.onKeyDown,
        modifiers = props.modifiers
      ),
      postAddons
    )

    val input = props.tooltip.fold(group)(tt => group.withTooltip(tt, props.tooltipPlacement))

    React.Fragment(
      props.label.map(l =>
        FormLabel(htmlFor = props.id, size = props.size, clazz = props.labelClass)(l)
      ),
      input
    )
