// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import scala.scalajs.js
import scala.scalajs.js.|

import cats.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.MonocleReact._
import japgolly.scalajs.react.ReactEventFromInput
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import monocle.macros.Lenses
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormInput
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.input._
import react.semanticui.elements.label._

/**
  * FormInput component that uses an ExternalValue to share the content of the field
  */
final case class FormInputEV[EV[_], A](
  name:            String,
  id:              String,
  action:          js.UndefOr[ShorthandSB[VdomNode]] = js.undefined,
  actionPosition:  js.UndefOr[ActionPosition] = js.undefined,
  as:              js.UndefOr[AsC] = js.undefined,
  className:       js.UndefOr[String] = js.undefined,
  clazz:           js.UndefOr[Css] = js.undefined,
  content:         js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  control:         js.UndefOr[String] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  error:           js.UndefOr[ShorthandB[Label]] = js.undefined,
  fluid:           js.UndefOr[Boolean] = js.undefined,
  focus:           js.UndefOr[Boolean] = js.undefined,
  icon:            js.UndefOr[ShorthandSB[Icon]] = js.undefined,
  iconPosition:    js.UndefOr[IconPosition] = js.undefined,
  inline:          js.UndefOr[Boolean] = js.undefined,
  input:           js.UndefOr[VdomNode] = js.undefined,
  inverted:        js.UndefOr[Boolean] = js.undefined,
  label:           js.UndefOr[ShorthandS[Label]] = js.undefined,
  labelPosition:   js.UndefOr[LabelPosition] = js.undefined,
  loading:         js.UndefOr[Boolean] = js.undefined,
  required:        js.UndefOr[Boolean] = js.undefined,
  size:            js.UndefOr[SemanticSize] = js.undefined,
  tabIndex:        js.UndefOr[String | JsNumber] = js.undefined,
  tpe:             js.UndefOr[String] = js.undefined,
  transparent:     js.UndefOr[Boolean] = js.undefined,
  width:           js.UndefOr[SemanticWidth] = js.undefined,
  value:           EV[A],
  format:          InputFormat[A] = InputFormat.id,
  onChange:        FormInputEV.ChangeCallback[A] =
    (_: A) => Callback.empty, // callback for parents of this component
  onBlur:          FormInputEV.ChangeCallback[A] = (_: A) => Callback.empty
)(implicit val ev: ExternalValue[EV])
    extends ReactProps[FormInputEV[Any, Any]](FormInputEV.component) {
  def valGet: String = format.reverseGet(ev.get(value))
  def valSet(s: String): Callback =
    format.getOption(s).map(ev.set(value)).getOrEmpty
  val onBlurC: InputEV.ChangeCallback[String]   =
    (s: String) => format.getOption(s).map(onBlur).getOrEmpty
  val onChangeC: InputEV.ChangeCallback[String] =
    (s: String) => format.getOption(s).map(onChange).getOrEmpty
}

object FormInputEV {
  type Props[EV[_], A]   = FormInputEV[EV, A]
  type ChangeCallback[A] = A => Callback
  type Scope[EV[_], A]   = RenderScope[Props[EV, A], State, Unit]

  @Lenses
  final case class State(curValue: String, prevValue: String)

  def onTextChange[EV[_], A]($ : Scope[EV, A]): ReactEventFromInput => Callback =
    (e: ReactEventFromInput) => {
      // Capture the value outside setState, react reuses the events
      val v = e.target.value
      // First update the internal state, then call the outside listener
      $.setStateL(State.curValue)(v) *>
        // Next 2 might not be called if the InputFormat returns None
        $.props.valSet(v) *>
        $.props.onChangeC(v)
    }

  def onBlur[EV[_], A]($ : Scope[EV, A], c: ChangeCallback[String]): Callback =
    c($.state.curValue)

  protected val component =
    ScalaComponent
      .builder[Props[Any, Any]]
      .getDerivedStateFromPropsAndState[State] { (props, stateOpt) =>
        val newValue = props.valGet
        // Force new value from props if the prop changes (or we are initializing).
        stateOpt match {
          case Some(state) if newValue === state.prevValue => state
          case _                                           => State(newValue, newValue)
        }
      }
      .render { $ =>
        val p = $.props
        val s = $.state
        FormInput(
          p.action,
          p.actionPosition,
          p.as,
          p.className,
          p.clazz,
          p.content,
          p.control,
          p.disabled,
          p.error,
          p.fluid,
          p.focus,
          p.icon,
          p.iconPosition,
          p.inline,
          p.input,
          p.inverted,
          p.label,
          p.labelPosition,
          p.loading,
          js.undefined,
          onTextChange($),
          p.required,
          p.size,
          p.tabIndex,
          p.tpe,
          p.transparent,
          p.width,
          s.curValue
        )(^.id := p.id, ^.onBlur --> onBlur($, p.onBlurC))
      }
      .build
}
