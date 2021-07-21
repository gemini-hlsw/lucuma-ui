// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats._
import cats.data.NonEmptyChain
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNec
import cats.syntax.all._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.ReactCats._
import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.facade.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.ui.optics.AuditResult
import lucuma.ui.optics.ChangeAuditor
import lucuma.ui.optics.ValidFormatInput
import lucuma.ui.reusability._
import monocle.macros.Lenses
import org.scalajs.dom.document
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormInput
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.input._
import react.semanticui.elements.label._

import scala.scalajs.js
import scala.scalajs.js.|

import scalajs.js.JSConverters._

/**
 * FormInput component that uses an ExternalValue to share the content of the field
 */
final case class FormInputEV[EV[_], A](
  id:              NonEmptyString,
  action:          js.UndefOr[ShorthandSB[VdomNode]] = js.undefined,
  actionPosition:  js.UndefOr[ActionPosition] = js.undefined,
  as:              js.UndefOr[AsC] = js.undefined,
  className:       js.UndefOr[String] = js.undefined,
  clazz:           js.UndefOr[Css] = js.undefined,
  content:         js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  control:         js.UndefOr[String] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  error:           js.UndefOr[ShorthandB[NonEmptyString]] = js.undefined,
  errorClazz:      js.UndefOr[Css] = js.undefined,
  errorPointing:   js.UndefOr[LabelPointing] = js.undefined,
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
  validFormat:     ValidFormatInput[A] = ValidFormatInput.id,
  changeAuditor:   ChangeAuditor[A] = ChangeAuditor.accept[A],
  modifiers:       Seq[TagMod] = Seq.empty,
  onTextChange:    String => Callback = _ => Callback.empty,
  onValidChange:   FormInputEV.ChangeCallback[Boolean] = _ => Callback.empty,
  onBlur:          FormInputEV.ChangeCallback[ValidatedNec[NonEmptyString, A]] =
    // Only use for extra actions, setting should be done through value.set
    (_: ValidatedNec[NonEmptyString, A]) => Callback.empty
)(implicit val ev: ExternalValue[EV], val eq: Eq[A])
    extends ReactProps[FormInputEV[Any, Any]](FormInputEV.component) {

  def valGet: String = ev.get(value).foldMap(validFormat.reverseGet)

  def valSet: InputEV.ChangeCallback[A] = ev.set(value)

  def withMods(mods: TagMod*): FormInputEV[EV, A] = copy(modifiers = modifiers ++ mods)
}

object FormInputEV {
  type Props[EV[_], A]   = FormInputEV[EV, A]
  type ChangeCallback[A] = A => Callback

  @Lenses
  final case class State(
    displayValue: String,
    modelValue:   String,
    cursor:       Option[(Int, Int)],
    lastKeyCode:  Int,
    inputElement: Option[html.Input],
    errors:       Option[NonEmptyChain[NonEmptyString]]
  )

  implicit val neChainReuse: Reusability[NonEmptyChain[NonEmptyString]] = Reusability.byEq
  implicit val stateReuse: Reusability[State]                           = Reusability.by(s => (s.displayValue, s.errors))

  class Backend[EV[_], A]($ : BackendScope[Props[EV, A], State]) {

    def validate(
      props: Props[EV, A],
      value: String,
      cb:    ValidatedNec[NonEmptyString, A] => Callback = _ => Callback.empty
    ): Callback = {
      val validated = props.validFormat.getValidated(value)
      props.onValidChange(validated.isValid) >> cb(validated)
    }

    // queries the dom based on id. Onus is on user to make id's unique.
    def getInputElement(id: NonEmptyString): Option[html.Input] =
      Option(document.querySelector(s"#${id.value}").asInstanceOf[html.Input])

    def getInputFromState: CallbackOption[html.Input] =
      $.state.map(_.inputElement).asCBO

    def getCursor: CallbackTo[Option[(Int, Int)]] =
      getInputFromState.map(i => (i.selectionStart, i.selectionEnd)).asCallback

    def setCursor(cursor: (Int, Int)): Callback =
      getInputFromState.map(i => i.setSelectionRange(cursor._1, cursor._2))

    def setStateCursorFromInput(offset: Int): Callback =
      getCursor
        .map(oc => oc.map { case (start, end) => (start + offset, end + offset) })
        .flatMap(oc => $.setStateL(State.cursor)(oc))

    def clearStateCursor: Callback = $.setStateL(State.cursor)(None)

    def setCursorFromState: Callback =
      $.state.flatMap(s => s.cursor.map(setCursor).getOrEmpty)

    def audit(auditor: ChangeAuditor[A], value: String): CallbackTo[String] = {
      def setDisplayValue(s: String): CallbackTo[String] =
        $.setStateL(State.displayValue)(s).map(_ => s)

      def cursorOffsetForReject: CallbackTo[Int] =
        $.state.map(_.lastKeyCode match {
          case KeyCode.Backspace => 1
          case KeyCode.Delete    => 0
          case _                 => -1
        })

      getCursor
        .map {
          case Some(c) => c._1
          case _       => value.length
        }
        .flatMap { c =>
          auditor.audit(value, c) match {
            case AuditResult.Accept                  => clearStateCursor *> setDisplayValue(value)
            case AuditResult.NewString(newS, offset) =>
              setStateCursorFromInput(offset) *> setDisplayValue(newS)
            case AuditResult.Reject                  =>
              cursorOffsetForReject.flatMap(setStateCursorFromInput _) *> CallbackTo(value)
          }
        }
    }

    def onTextChange(props: Props[EV, A]): ReactEventFromInput => Callback =
      (e: ReactEventFromInput) => {
        // Capture the value outside setState, react reuses the events
        val v = e.target.value
        audit(props.changeAuditor, v).flatMap(newS =>
          // First update the internal state, then call the outside listener
          $.setStateL(State.errors)(none) *> props.onTextChange(newS) *> validate(props, newS)
        )
      }

    def submit(props: Props[EV, A], state: State): Callback =
      validate(
        props,
        state.displayValue,
        { validated =>
          val validatedCB = validated match {
            case Valid(a)   =>
              implicit val eq = props.eq
              if (props.ev.get(props.value).exists(_ =!= a)) // Only set if resulting A changed.
                props.valSet(a)
              else                                           // A didn't change, but redisplay formatted string.
                $.setStateL(State.displayValue)(props.valGet)
            case Invalid(e) =>
              $.setStateL(State.errors)(e.some)
          }
          validatedCB >> props.onBlur(validated)
        }
      )

    def onKeyDown(props: Props[EV, A], state: State): ReactKeyboardEventFromInput => Callback = e =>
      if (e.keyCode === KeyCode.Enter)
        submit(props, state)
      else
        $.setStateL(State.lastKeyCode)(e.keyCode) *> clearStateCursor

    def render(p: Props[EV, A], s: State): VdomNode = {

      def errorLabel(errors: NonEmptyChain[NonEmptyString]): js.UndefOr[ShorthandB[Label]] = {
        val vdoms = errors.toList.map[VdomNode](_.value)
        val list  = vdoms.head +: vdoms.tail.flatMap[VdomNode](e => List(<.br, <.br, e))
        Label(
          content = React.Fragment(list: _*),
          clazz = p.errorClazz,
          pointing = p.errorPointing
        )(
          ^.position.absolute
        )
      }

      val error: js.UndefOr[ShorthandB[Label]] = p.error
        .flatMap[ShorthandB[Label]] {
          (_: Any) match {
            case b: Boolean => s.errors.map(errorLabel).getOrElse(b)
            case e          => // We can't pattern match against NonEmptyString, but we know it is one.
              val nes = e.asInstanceOf[NonEmptyString]
              s.errors.map(ve => errorLabel(nes +: ve)).getOrElse(errorLabel(NonEmptyChain(nes)))
          }
        }
        .orElse(s.errors.orUndefined.flatMap(errorLabel))

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
        s.displayValue
      )(
        (p.modifiers :+
          (^.id := p.id.value) :+
          (^.onKeyDown ==> onKeyDown(p, s)) :+
          (^.onBlur --> submit(p, s)): _*)
      )
    }
  }

  protected def buildComponent[EV[_], A] = {
    implicit val propsReuse: Reusability[Props[EV, A]] = Reusability.never

    ScalaComponent
      .builder[Props[EV, A]]
      .getDerivedStateFromPropsAndState[State] { (props, stateOpt) =>
        val newValue = props.valGet
        // Force new value from props if the prop changes (or we are initializing).
        stateOpt match {
          case Some(state) if newValue === state.modelValue => state
          case _                                            => State(newValue, newValue, none, 0, none, none)
        }
      }
      .renderBackend[Backend[EV, A]]
      .componentDidMount($ =>
        $.setStateL(State.inputElement)($.backend.getInputElement($.props.id))
          *>
            $.backend
              .audit($.props.changeAuditor, $.props.valGet)
              .flatMap($.backend.validate($.props, _))
      )
      .componentDidUpdate(_.backend.setCursorFromState)
      .configure(Reusability.shouldComponentUpdate)
      .build
  }

  protected val component = buildComponent[Any, Any]
}
