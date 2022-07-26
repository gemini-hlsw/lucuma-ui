// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats._
import cats.data.NonEmptyChain
import cats.syntax.all._
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.validation._
import lucuma.ui.input.AuditResult
import lucuma.ui.input.ChangeAuditor
import lucuma.ui.reusability._
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
  tabIndex:        js.UndefOr[String | Double] = js.undefined,
  tpe:             js.UndefOr[String] = js.undefined,
  transparent:     js.UndefOr[Boolean] = js.undefined,
  width:           js.UndefOr[SemanticWidth] = js.undefined,
  value:           EV[A],
  validFormat:     InputValidFormat[A] = InputValidSplitEpi.id,
  changeAuditor:   ChangeAuditor = ChangeAuditor.accept,
  modifiers:       Seq[TagMod] = Seq.empty,
  onTextChange:    String => Callback = _ => Callback.empty,
  onValidChange:   FormInputEV.ChangeCallback[Boolean] = _ => Callback.empty,
  // Only use for extra actions, setting should be done through value.set
  onBlur:          FormInputEV.ChangeCallback[EitherErrors[A]] = (_: EitherErrors[A]) => Callback.empty
)(implicit val ev: ExternalValue[EV], val eq: Eq[A])
    extends ReactFnProps[FormInputEV[FormInputEV.AnyF, Any]](FormInputEV.component) {

  def valGet: String = ev.get(value).foldMap(validFormat.reverseGet)

  def valSet: InputEV.ChangeCallback[A] = ev.set(value)

  def withMods(mods: TagMod*): FormInputEV[EV, A] = copy(modifiers = modifiers ++ mods)
}

object FormInputEV {
  type AnyF[_]                     = Any
  protected type Props[EV[_], A]   = FormInputEV[EV, A]
  protected type ChangeCallback[A] = A => Callback

  // queries the dom based on id. Onus is on user to make id's unique.
  private def getInputElement(id: NonEmptyString): CallbackTo[Option[html.Input]] =
    CallbackTo(Option(document.querySelector(s"#${id.value}").asInstanceOf[html.Input]))

  protected def buildComponent[EV[_], A] = {
    def audit(
      auditor:         ChangeAuditor,
      value:           String,
      setDisplayValue: String => Callback,
      inputElement:    Option[html.Input],
      setCursor:       Option[(Int, Int)] => Callback,
      lastKeyCode:     Int
    ): CallbackTo[String] = {
      val cursor: Option[(Int, Int)] = inputElement.map(i => (i.selectionStart, i.selectionEnd))

      def setStateCursorFromInput(offset: Int): Callback =
        setCursor(cursor.map { case (start, end) => (start + offset, end + offset) })

      val cursorOffsetForReject: Int =
        lastKeyCode match {
          case KeyCode.Backspace => 1
          case KeyCode.Delete    => 0
          case _                 => -1
        }

      val c = cursor match {
        case Some((start, _)) => start
        case _                => value.length
      }

      auditor.audit(value, c) match {
        case AuditResult.Accept                  =>
          setCursor(none) >> setDisplayValue(value).as(value)
        case AuditResult.NewString(newS, offset) =>
          setStateCursorFromInput(offset) >> setDisplayValue(newS).as(newS)
        case AuditResult.Reject                  =>
          setStateCursorFromInput(cursorOffsetForReject) >> CallbackTo(value)
      }

    }

    def validate(
      displayValue:  String,
      validFormat:   InputValidFormat[A],
      onValidChange: FormInputEV.ChangeCallback[Boolean],
      cb:            EitherErrors[A] => Callback = _ => Callback.empty
    ): Callback = {
      val validated = validFormat.getValid(displayValue)
      onValidChange(validated.isRight) >> cb(validated)
    }

    ScalaFnComponent
      .withHooks[Props[EV, A]]
      .useStateBy(props => props.valGet)             // displayValue
      .useState(none[(Int, Int)])                    // cursor
      .useRef(0)                                     // lastKeyCode
      .useRef(none[html.Input])                      // inputElement
      .useState(none[NonEmptyChain[NonEmptyString]]) // errors
      .useEffectWithDepsBy((props, _, _, _, _, _) => props.valGet)(
        (_, displayValue, _, _, _, errors) =>
          newValue => displayValue.setState(newValue) >> errors.setState(none)
      )
      .useEffectOnMountBy((props, displayValue, cursor, lastKeyCode, inputElement, _) =>
        getInputElement(props.id) >>= (element =>
          inputElement.set(element) >>
            audit(
              props.changeAuditor,
              props.valGet,
              displayValue.setState,
              element,
              cursor.setState,
              lastKeyCode.value
            )
        ) >>= (value => validate(value, props.validFormat, props.onValidChange))
      )
      .useEffectBy((_, _, cursor, _, inputElement, _) =>
        (for {
          i <- inputElement.value
          c <- cursor.value
        } yield Callback(i.setSelectionRange(c._1, c._2))).orEmpty
      )
      .render { (props, displayValue, cursor, lastKeyCode, inputElement, errors) =>
        def errorLabel(errors: NonEmptyChain[NonEmptyString]): js.UndefOr[ShorthandB[Label]] = {
          val vdoms = errors.toList.map[VdomNode](_.value)
          val list  = vdoms.head +: vdoms.tail.flatMap[VdomNode](e => List(<.br, <.br, e))
          Label(
            content = React.Fragment(list: _*),
            clazz = props.errorClazz,
            pointing = props.errorPointing
          )(^.position.absolute)
        }

        val error: Option[Boolean | VdomNode | Label | Unit] = props.error.toOption
          .flatMap[Boolean | VdomNode | Label | Unit] {
            _ match {
              case b: Boolean => errors.value.map(errorLabel).getOrElse(b).toOption
              case e          => // We can't pattern match against NonEmptyString, but we know it is one.
                val nes = e.asInstanceOf[NonEmptyString]
                errors.value
                  .map(ve => errorLabel(nes +: ve))
                  .getOrElse(errorLabel(NonEmptyChain(nes)))
                  .toOption
            }
          }
          .orElse(errors.value.flatMap(x => errorLabel(x).toOption))

        val onTextChange: ReactEventFromInput => Callback =
          (e: ReactEventFromInput) => {
            // Capture the value outside setState, react reuses the events
            val v = e.target.value
            audit(
              props.changeAuditor,
              v,
              displayValue.setState,
              inputElement.value,
              cursor.setState,
              lastKeyCode.value
            ).flatMap(newS =>
              // First update the internal state, then call the outside listener
              errors.setState(none) >>
                props.onTextChange(newS) >>
                validate(displayValue.value, props.validFormat, props.onValidChange)
            )
          }

        val submit: Callback =
          validate(
            displayValue.value,
            props.validFormat,
            props.onValidChange,
            { validated =>
              val validatedCB = validated match {
                case Right(a) =>
                  implicit val eq = props.eq

                  // Only set if resulting A changed.
                  if (props.ev.get(props.value).exists(_ =!= a))
                    props.valSet(a)
                  else // A didn't change, but redisplay formatted string.
                    displayValue.setState(props.valGet)
                case Left(e)  =>
                  errors.setState(e.some)
              }
              validatedCB >> props.onBlur(validated)
            }
          )

        val onKeyDown: ReactKeyboardEventFromInput => Callback = e =>
          // TODO keyCode can be undefined (despite the facade). This happens when selecting a value in form auto-fill.
          if (e.keyCode === KeyCode.Enter)
            submit
          else
            lastKeyCode.set(e.keyCode) >> cursor.setState(none)

        FormInput(
          action = props.action,
          actionPosition = props.actionPosition,
          as = props.as,
          className = props.className,
          clazz = props.clazz,
          content = props.content,
          control = props.control,
          disabled = props.disabled,
          error = error.orUndefined,
          fluid = props.fluid,
          focus = props.focus,
          icon = props.icon,
          iconPosition = props.iconPosition,
          inline = props.inline,
          input = props.input,
          inverted = props.inverted,
          label = props.label,
          labelPosition = props.labelPosition,
          loading = props.loading,
          onChangeE = onTextChange,
          required = props.required,
          size = props.size,
          tabIndex = props.tabIndex,
          tpe = props.tpe,
          transparent = props.transparent,
          width = props.width,
          value = displayValue.value
        )(
          props.modifiers :+
            (^.id := props.id.value) :+
            (^.onKeyDown ==> onKeyDown) :+
            (^.onBlur --> submit): _*
        )
      }
  }

  protected val component = buildComponent[AnyF, Any]
}
