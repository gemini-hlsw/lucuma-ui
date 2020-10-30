// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import scala.scalajs.js
import scala.scalajs.js.|

import cats.syntax.all._
import japgolly.scalajs.react.BackendScope
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
import cats.data.NonEmptyList
import scalajs.js.JSConverters._
import japgolly.scalajs.react.React

/**
 * FormInput component that uses an ExternalValue to share the content of the field
 */
final case class FormInputEV[EV[_], A](
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
  validate:        InputValidate[A] = InputValidate.id,
  modifiers:       Seq[TagMod] = Seq.empty,
  onChange:        FormInputEV.ChangeCallback[A] =
    (_: A) => Callback.empty, // callback for parents of this component
  onBlur:          FormInputEV.ChangeCallback[A] = (_: A) => Callback.empty
)(implicit val ev: ExternalValue[EV])
    extends ReactProps[FormInputEV[Any, Any]](FormInputEV.component) {

  def valGet: String = ev.get(value).foldMap(validate.reverseGet)

  def valSet(s: String): Callback =
    validate
      .getValidated(s)
      .fold(_ => Callback.empty, ev.set(value))

  val onChangeC: InputEV.ChangeCallback[String] =
    (s: String) =>
      validate
        .getValidated(s)
        .fold(_ => Callback.empty, onChange)

  def onBlurC(onError: NonEmptyList[String] => Callback): InputEV.ChangeCallback[String] =
    (s: String) =>
      validate
        .getValidated(s)
        .fold(onError, onBlur)

  def withMods(mods: TagMod*): FormInputEV[EV, A] = copy(modifiers = modifiers ++ mods)
}

object FormInputEV {
  type Props[EV[_], A]   = FormInputEV[EV, A]
  type ChangeCallback[A] = A => Callback
  type Scope[EV[_], A]   = RenderScope[Props[EV, A], State, Unit]

  @Lenses
  final case class State(curValue: String, prevValue: String, errors: Option[NonEmptyList[String]])

  class Backend[EV[_], A]($ : BackendScope[Props[EV, A], State]) {

    def onTextChange(props: Props[EV, A]): ReactEventFromInput => Callback =
      (e: ReactEventFromInput) => {
        // Capture the value outside setState, react reuses the events
        val v = e.target.value
        // First update the internal state, then call the outside listener
        $.setStateL(State.curValue)(v) *>
          // Next 2 might not be called if the InputFormat returns None
          props.valSet(v) *>
          props.onChangeC(v)
      }

    def onBlur(state: State, c: ChangeCallback[String]): Callback =
      c(state.curValue)

    def render(p: Props[EV, A], s: State): VdomNode = {

      val validationError: js.UndefOr[Label] =
        s.errors.map(e => Label(e.toList.mkString(","))).orUndefined

      val error: js.UndefOr[ShorthandB[Label]] = p.error
        .flatMap[ShorthandB[Label]] {
          (_: Any) match {
            case b: Boolean => validationError.map(_.asInstanceOf[ShorthandB[Label]]).orElse(b)
            case l: Label   =>
              validationError
                .map(vel => Label(content = React.Fragment(l, vel)).asInstanceOf[ShorthandB[Label]])
                .orElse(l)
          }
        }
        .orElse(validationError)

      FormInput(
        p.action,
        p.actionPosition,
        p.as,
        p.className,
        p.clazz,
        p.content,
        p.control,
        p.disabled,
        error,
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
        onTextChange(p),
        p.required,
        p.size,
        p.tabIndex,
        p.tpe,
        p.transparent,
        p.width,
        s.curValue
      )(
        (p.modifiers :+ (^.id := p.id) :+ (^.onBlur --> onBlur(
          s,
          p.onBlurC(e => $.setStateL(State.errors)(e.some))
        )): _*)
      )
    }
  }

  protected def buildComponent[EV[_], A] =
    ScalaComponent
      .builder[Props[EV, A]]
      .getDerivedStateFromPropsAndState[State] { (props, stateOpt) =>
        val newValue = props.valGet
        // Force new value from props if the prop changes (or we are initializing).
        stateOpt match {
          case Some(state) if newValue === state.prevValue => state
          case _                                           => State(newValue, newValue, none)
        }
      }
      .renderBackend[Backend[EV, A]]
      .build

  protected val component = buildComponent[Any, Any]
}
