// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.ui.input.InputFormat
import react.common._

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
    extends ReactFnProps[InputEV[InputEV.AnyF, Any]](InputEV.component) {
  def valGet: String                            = ev.get(value).foldMap(format.reverseGet)
  def valSet(s: String): Callback               = format.getOption(s).map(ev.set(value)).getOrEmpty
  val onBlurC: InputEV.ChangeCallback[String]   =
    (s: String) => format.getOption(s).map(onBlur).getOrEmpty
  val onChangeC: InputEV.ChangeCallback[String] =
    (s: String) => format.getOption(s).map(onChange).getOrEmpty
}

object InputEV {
  type AnyF[_]                            = Any
  protected type Props[EV[_], A]          = InputEV[EV, A]
  protected[forms] type ChangeCallback[A] = A => Callback

  sealed trait InputType    extends Product with Serializable
  case object TextInput     extends InputType
  case object PasswordInput extends InputType

  protected val component =
    ScalaFnComponent
      .withHooks[Props[AnyF, Any]]
      // final protected case class State(curValue: Option[String], prevValue: String)
      .useState(none[String]) // value
      .useEffectWithDepsBy((props, _) => props.valGet)((_, value) =>
        newValue => value.setState(newValue.some)
      )
      .render { (props, value) =>
        def onTextChange(e: ReactEventFromInput): Callback = {
          // Capture the value outside setState, react reuses the events
          val v = e.target.value
          // First update the internal state, then call the outside listener
          value.setState(v.some) >>
            // Next 2 might not be called if the InputFormat returns None
            props.valSet(v) >>
            props.onChangeC(v)
        }

        def onBlur(cc: ChangeCallback[String]): Callback =
          cc(value.value.orEmpty)

        <.input(
          ^.`type`      := (props.inputType match {
            case TextInput     => "text"
            case PasswordInput => "password"
          }),
          ^.placeholder := props.placeholder,
          ^.name        := props.name,
          ^.id          := props.id,
          ^.value       := value.value.orEmpty,
          ^.disabled    := props.disabled,
          ^.onChange ==> onTextChange,
          ^.onBlur --> onBlur(props.onBlurC)
        )
      }
}
