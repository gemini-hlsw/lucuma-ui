// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import scala.scalajs.js
import scala.scalajs.js.|

import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.MonocleReact._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import monocle.macros.Lenses
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormInput
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.input._
import react.semanticui.elements.label._
import cats.data.NonEmptyChain
import scalajs.js.JSConverters._
import japgolly.scalajs.react.React
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.data.ValidatedNec

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
  onValidChange:   FormInputEV.ChangeCallback[Boolean] = _ => Callback.empty,
  onBlur:          FormInputEV.ChangeCallback[ValidatedNec[String, A]] = (_: ValidatedNec[String, A]) =>
    Callback.empty // for extra actions
)(implicit val ev: ExternalValue[EV])
    extends ReactProps[FormInputEV[Any, Any]](FormInputEV.component) {

  def valGet: String = ev.get(value).foldMap(validate.reverseGet)

  def valSet: InputEV.ChangeCallback[A] = ev.set(value)

  def onBlurC(onError: NonEmptyChain[String] => Callback): InputEV.ChangeCallback[String] =
    (s: String) => {
      val validated = validate.getValidated(s)
      validated.swap.toOption.map(onError).getOrEmpty >> onBlur(validated)
    }

  def withMods(mods: TagMod*): FormInputEV[EV, A] = copy(modifiers = modifiers ++ mods)
}

object FormInputEV {
  type Props[EV[_], A]   = FormInputEV[EV, A]
  type ChangeCallback[A] = A => Callback

  @Lenses
  final case class State(curValue: String, prevValue: String, errors: Option[NonEmptyChain[String]])

  class Backend[EV[_], A]($ : BackendScope[Props[EV, A], State]) {

    def validate(
      props: Props[EV, A],
      value: String,
      cb:    ValidatedNec[String, A] => Callback = _ => Callback.empty
    ): Callback = {
      val validated = props.validate.getValidated(value)
      props.onValidChange(validated.isValid) >> cb(validated)
    }

    def onTextChange(props: Props[EV, A]): ReactEventFromInput => Callback =
      (e: ReactEventFromInput) => {
        // Capture the value outside setState, react reuses the events
        val v = e.target.value
        // First update the internal state, then call the outside listener
        $.setStateL(State.curValue)(v) >> $.setStateL(State.errors)(none) >>
          validate(props, v)
      }

    def onBlur(props: Props[EV, A], state: State): Callback =
      validate(
        props,
        state.curValue,
        { validated =>
          val validatedCB = validated match {
            case Valid(a)   => props.valSet(a)
            case Invalid(e) => $.setStateL(State.errors)(e.some)
          }
          validatedCB >> props.onBlur(validated)
        }
      )

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
        (p.modifiers :+ (^.id := p.id) :+ (^.onBlur --> onBlur(p, s)): _*)
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
      .componentDidMount($ => $.backend.validate($.props, $.props.valGet))
      .build

  protected val component = buildComponent[Any, Any]
}
