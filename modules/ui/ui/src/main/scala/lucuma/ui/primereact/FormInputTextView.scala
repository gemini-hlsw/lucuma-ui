// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.*
import cats.data.NonEmptyChain
import cats.syntax.all.*
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.validation.*
import lucuma.react.common.*
import lucuma.react.primereact.PrimeStyles
import lucuma.react.primereact.TooltipOptions
import lucuma.ui.input.AuditResult
import lucuma.ui.input.ChangeAuditor
import org.scalajs.dom.Element
import org.scalajs.dom.HTMLInputElement
import org.scalajs.dom.KeyCode
import org.scalajs.dom.document
import org.scalajs.dom.html

import scala.scalajs.js

import scalajs.js.JSConverters.*

/**
 * FormInput component that uses a crystal View to share the content of the field
 */
final case class FormInputTextView[V[_], A](
  id:              NonEmptyString,
  label:           js.UndefOr[TagMod] = js.undefined,
  units:           js.UndefOr[String] = js.undefined,
  preAddons:       List[TagMod] = List.empty,
  postAddons:      List[TagMod] = List.empty,
  size:            js.UndefOr[PlSize] = js.undefined,
  groupClass:      js.UndefOr[Css] = js.undefined,
  inputClass:      js.UndefOr[Css] = js.undefined,
  labelClass:      js.UndefOr[Css] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  error:           js.UndefOr[NonEmptyString] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined,
  tooltipOptions:  js.UndefOr[TooltipOptions] = js.undefined,
  value:           V[A],
  validFormat:     InputValidFormat[A] = InputValidSplitEpi.id,
  changeAuditor:   ChangeAuditor = ChangeAuditor.accept,
  onTextChange:    String => Callback = _ => Callback.empty,
  onValidChange:   FormInputTextView.ChangeCallback[Boolean] = _ => Callback.empty,
  onFocus:         js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onBlur:          FormInputTextView.ChangeCallback[EitherErrors[A]] = (_: EitherErrors[A]) =>
    Callback.empty,
  validateOnPaste: Boolean = true,
  modifiers:       Seq[TagMod] = Seq.empty
)(using val eq: Eq[A], val vl: ViewLike[V])
    extends ReactFnProps(FormInputTextView.component):
  def stringValue: String                  = value.get.foldMap(validFormat.reverseGet)
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)
  def addPostAddons(addons: List[TagMod])  =
    copy(postAddons = this.postAddons ++ addons)
  def withPostAddons(addons:  TagMod*)     = addPostAddons(addons.toList)

object FormInputTextView {
  type AnyF[_] = Any

  protected type Props[V[_], A]    = FormInputTextView[V, A]
  protected type ChangeCallback[A] = A => Callback

  // queries the dom based on id. Onus is on user to make id's unique.
  private def getInputElement(id: NonEmptyString): CallbackTo[Option[html.Input]] =
    CallbackTo(Option(document.querySelector(s"#${id.value}").asInstanceOf[html.Input]))

  protected def buildComponent[V[_], A] = {
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
      onValidChange: FormInputTextView.ChangeCallback[Boolean],
      cb:            EitherErrors[A] => Callback = _ => Callback.empty
    ): Callback = {
      val validated = validFormat.getValid(displayValue)
      onValidChange(validated.isRight) >> cb(validated)
    }

    ScalaFnComponent
      .withHooks[Props[V, A]]
      .useStateBy(props => props.stringValue) // displayValue
      .useState(none[(Int, Int)]) // cursor
      .useRef(0) // lastKeyCode
      .useRef(none[html.Input]) // inputElement
      .useState(none[NonEmptyChain[NonEmptyString]]) // errors
      .useEffectWithDepsBy((props, _, _, _, _, _) => props.stringValue)(
        (_, displayValue, _, _, _, errors) =>
          newValue => displayValue.setState(newValue) >> errors.setState(none)
      )
      .useEffectOnMountBy((props, displayValue, cursor, lastKeyCode, inputElement, _) =>
        getInputElement(props.id) >>= (element =>
          inputElement.set(element) >>
            audit(
              props.changeAuditor,
              props.stringValue,
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
        import props.given

        val errorChain: Option[NonEmptyChain[NonEmptyString]] =
          (props.error.toOption, errors.value) match {
            case (Some(a), Some(b)) => (a +: b).some
            case (None, Some(b))    => b.some
            case (Some(a), None)    => NonEmptyChain(a).some
            case (None, None)       => none
          }

        val error: Option[String] = errorChain.map(_.mkString_(", "))

        def handleTextChange(text: String): Callback =
          audit(
            props.changeAuditor,
            text,
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

        val onTextChange: ReactEventFrom[HTMLInputElement & Element] => Callback =
          (e: ReactEventFrom[HTMLInputElement & Element]) => handleTextChange(e.target.value)

        val submit: Callback =
          validate(
            displayValue.value,
            props.validFormat,
            props.onValidChange,
            { validated =>
              val validatedCB = validated match {
                case Right(a) =>
                  // Only set if resulting A changed.
                  if (props.value.get.exists(_ =!= a))
                    props.value.set(a)
                  else // A didn't change, but redisplay formatted string.
                    displayValue.setState(props.stringValue)
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

        val onPaste: ReactClipboardEvent => Callback = e =>
          val input      = e.target.asInstanceOf[org.scalajs.dom.HTMLInputElement]
          val current    = input.value
          val start      = input.selectionStart
          val end        = input.selectionEnd
          val prefix     = current.substring(0, start)
          val suffix     = current.substring(end)
          val paste      = e.clipboardData.getData("text")
          val result     = s"$prefix$paste$suffix"
          val normalized =
            props.validFormat.getValid(result).map(props.validFormat.reverseGet).toOption
          val update     = normalized.foldMap(handleTextChange)
          e.preventDefaultCB >> update

        // In many cases it is desireable to validate and normalize pasted text. Numeric fields, for example.
        // However, in some cases we want to allow pasting of anything and just show the error state. See
        // https://app.shortcut.com/lucuma/story/5817/allow-pasting-into-the-invitation-email-field
        val allModifiers =
          if props.validateOnPaste then (^.onPaste ==> onPaste) +: props.modifiers
          else props.modifiers

        FormInputText(
          id = props.id,
          label = props.label,
          units = props.units,
          size = props.size,
          groupClass = props.groupClass,
          inputClass =
            error.map(_ => PrimeStyles.Invalid).orEmpty |+| props.inputClass.toOption.orEmpty,
          labelClass = props.labelClass,
          tooltip = error.orUndefined,
          tooltipOptions = props.tooltipOptions,
          disabled = props.disabled,
          preAddons = props.preAddons,
          postAddons = props.postAddons,
          onFocus = props.onFocus,
          onBlur = _ => submit,
          onChange = onTextChange,
          onKeyDown = onKeyDown,
          placeholder = props.placeholder,
          value = displayValue.value,
          modifiers = allModifiers
        )
      }
  }

  protected val component = buildComponent[AnyF, Any]
}
