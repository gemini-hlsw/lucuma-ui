// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.ReactEventFromInput
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.MonocleReact._
import monocle.macros.Lenses
import react.common.ReactProps

/**
  * Input component that uses an ExternalValue to share the content of the field
  */
final case class InputEV[EV[_], A](
  name:            String,
  id:              String,
  value:           EV[A],
  format:          InputFormat[A] = InputFormat.id,
  inputType:       InputEV.InputType = InputEV.TextInput,
  placeholder:     String = "",
  disabled:        Boolean = false,
  onChange:        InputEV.ChangeCallback[A] =
    (_: A) => Callback.empty, // callback for parents of this component
  onBlur:          InputEV.ChangeCallback[A] = (_: A) => Callback.empty
)(implicit val ev: ExternalValue[EV])
    extends ReactProps[InputEV[Any, Any]](InputEV.component) {
  def valGet: String = format.reverseGet(ev.get(value))
  def valSet(s: String): Callback = format.getOption(s).map(ev.set(value)).getOrEmpty
  val onBlurC: InputEV.ChangeCallback[String]   =
    (s: String) => format.getOption(s).map(onBlur).getOrEmpty
  val onChangeC: InputEV.ChangeCallback[String] =
    (s: String) => format.getOption(s).map(onChange).getOrEmpty
}

object InputEV {
  type Props[EV[_], A]   = InputEV[EV, A]
  type ChangeCallback[A] = A => Callback
  type Scope[EV[_], A]   = RenderScope[Props[EV, A], State, Unit]

  @Lenses
  final case class State(curValue: Option[String], prevValue: String)

  sealed trait InputType    extends Product with Serializable
  case object TextInput     extends InputType
  case object PasswordInput extends InputType

  def onTextChange[EV[_], A]($ : Scope[EV, A])(e: ReactEventFromInput): Callback = {
    // Capture the value outside setState, react reuses the events
    val v = e.target.value
    // First update the internal state, then call the outside listener
    $.setStateL(State.curValue)(v.some) *>
      // Next 2 might not be called if the InputFormat returns None
      $.props.valSet(v) *>
      $.props.onChangeC(v)
  }

  def onBlur[EV[_], A]($ : Scope[EV, A], c: ChangeCallback[String]): Callback =
    c($.state.curValue.orEmpty)

  protected val component =
    ScalaComponent
      .builder[Props[Any, Any]]
      .getDerivedStateFromPropsAndState[State] { (props, stateOpt) =>
        val newValue = props.valGet
        // Force new value from props if the prop changes (or we are initializing).
        stateOpt match {
          case Some(state) if newValue === state.prevValue => state
          case _                                           => State(newValue.some, newValue)
        }
      }
      .render { b =>
        val p = b.props
        val s = b.state
        <.input(
          ^.`type` := (p.inputType match {
            case TextInput     => "text"
            case PasswordInput => "password"
          }),
          ^.placeholder := p.placeholder,
          ^.name := p.name,
          ^.id := p.id,
          ^.value := s.curValue.orEmpty,
          ^.disabled := p.disabled,
          ^.onChange ==> onTextChange(b),
          ^.onBlur --> onBlur(b, p.onBlurC)
        )
      }
      .build
}
