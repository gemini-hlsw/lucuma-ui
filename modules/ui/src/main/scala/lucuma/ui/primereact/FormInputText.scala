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
  groupClass:       js.UndefOr[Css] = js.undefined,
  inputClass:       js.UndefOr[Css] = js.undefined,
  disabled:         js.UndefOr[Boolean] = js.undefined,
  placeholder:      js.UndefOr[String] = js.undefined,
  tooltip:          js.UndefOr[VdomNode] = js.undefined,
  tooltipPlacement: floatingui.Placement = floatingui.Placement.Top,
  onBlur:           js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onChange:         js.UndefOr[ReactEventFromInput => Callback] = js.undefined,
  onKeyDown:        js.UndefOr[ReactKeyboardEventFromInput => Callback] = js.undefined
) extends ReactFnProps(FormInputText.component)

object FormInputText {
  private val component = ScalaFnComponent[FormInputText] { props =>
    val group = <.div(
      PrimeStyles.InputGroup |+| LucumaStyles.FormField |+| props.groupClass.toOption.orEmpty,
      props.preAddons.toVdomArray(p =>
        (p: Any) match {
          case b: CButton.Builder => b.build
          case t: TagMod          =>
            <.span(t, Css("p-inputgroup-addon"))
        }
      ),
      InputText(
        id = props.id.value,
        value = props.value,
        clazz = props.inputClass.toOption.orEmpty,
        disabled = props.disabled,
        placeholder = props.placeholder,
        onBlur = props.onBlur,
        onChange = props.onChange,
        onKeyDown = props.onKeyDown
      ),
      props.postAddons.zipWithIndex.toVdomArray { (p, i) =>
        val key = s"${props.id.value}-post-add-on-$i"
        (p: Any) match {
          case b: CButton.Builder => b.withKey(key).build
          case t: TagMod          =>
            <.span(^.key := key, t, Css("p-inputgroup-addon"))
        }
      }
    )

    val input = props.tooltip.fold(group)(tt => group.withTooltip(tt, props.tooltipPlacement))

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id)(l)),
      input
    )
  }
}
