// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.TopNode
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.Element
import org.scalajs.dom.HTMLInputElement
import react.common.*
import react.floatingui
import react.floatingui.syntax.*
import react.primereact.InputText
import react.primereact.PrimeStyles
import reactST.primereact.components.{Button => CButton}

import scalajs.js

case class FormInputText(
  id:               NonEmptyString,
  value:            js.UndefOr[String] = js.undefined,
  label:            js.UndefOr[TagMod] = js.undefined,
  preAddons:        List[TagMod | CButton.Builder] = List.empty,
  postAddons:       List[TagMod | CButton.Builder] = List.empty,
  size:             js.UndefOr[PlSize] = js.undefined,
  groupClass:       js.UndefOr[Css] = js.undefined,
  inputClass:       js.UndefOr[Css] = js.undefined,
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
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object FormInputText {
  private val component = ScalaFnComponent[FormInputText] { props =>
    val sizeCls = props.size.toOption.map(_.cls).orEmpty

    def buildAddons(addons: List[TagMod | CButton.Builder]) =
      addons.toTagMod(p =>
        (p: Any) match {
          case b: CButton.Builder => b.build
          case t: TagMod          =>
            <.span(t, PrimeStyles.InputGroupAddon |+| sizeCls)
        }
      )

    val group = <.div(
      PrimeStyles.InputGroup |+| LucumaStyles.FormField |+| props.groupClass.toOption.orEmpty,
      buildAddons(props.preAddons),
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
      buildAddons(props.postAddons)
    )

    val input = props.tooltip.fold(group)(tt => group.withTooltip(tt, props.tooltipPlacement))

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id, size = props.size)(l)),
      input
    )
  }
}
