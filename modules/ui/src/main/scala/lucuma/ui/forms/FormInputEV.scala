// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import scala.scalajs.js
import scala.scalajs.js.|

import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.MonocleReact._
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import monocle.macros.Lenses
import org.scalajs.dom.html
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormInput
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.input._
import react.semanticui.elements.label._

trait UpdateValue extends Product with Serializable
object UpdateValue {
  case object OnChange extends UpdateValue
  case object OnBlur   extends UpdateValue
}

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
  format:          InputFormat[A] = InputFormat.id,
  modifiers:       Seq[TagMod] = Seq.empty,
  onChange:        FormInputEV.ChangeCallback[A] =
    (_: A) => Callback.empty, // callback for parents of this component
  onBlur:          FormInputEV.ChangeCallback[A] = (_: A) => Callback.empty,
  changeAuditor:   ChangeAuditor[A] = ChangeAuditor.fromFormat[A],
  updateOn:        UpdateValue = UpdateValue.OnChange
)(implicit val ev: ExternalValue[EV])
    extends ReactProps[FormInputEV[Any, Any]](FormInputEV.component) {
  def valGet: String                       = ev.get(value).foldMap(format.reverseGet)
  def valSet: A => Callback                = ev.set(value)
  def onBlurC: InputEV.ChangeCallback[A]   = (a: A) => onBlur(a)
  val onChangeC: InputEV.ChangeCallback[A] = onChange

  def withMods(mods: TagMod*): FormInputEV[EV, A] = copy(modifiers = modifiers ++ mods)
}

object FormInputEV {
  type Props[EV[_], A]   = FormInputEV[EV, A]
  type ChangeCallback[A] = A => Callback
  type Scope[EV[_], A]   = RenderScope[Props[EV, A], State, Unit]

  @Lenses
  final case class State(displayValue: String, modelValue: String, cursor: Option[(Int, Int)])

  class Backend[EV[_], A]($ : BackendScope[Props[EV, A], State]) {
    private val outerRef = Ref[html.Element]

    def getInputElement(e: html.Element): html.Input =
      e.firstChild
        .asInstanceOf[html.Element]
        .childNodes(1)
        .asInstanceOf[html.Element]
        .firstChild
        .asInstanceOf[html.Input]

    def getCursor: CallbackTo[Option[(Int, Int)]] =
      outerRef.get.map(getInputElement).map(i => (i.selectionStart, i.selectionEnd)).asCallback

    def setCursor(cursor: (Int, Int)): Callback =
      outerRef.get.map(getInputElement).map(i => i.setSelectionRange(cursor._1, cursor._2))

    def setStateCursor(offset: Int): Callback =
      getCursor
        .map(oc => oc.map { case (start, end) => (start + offset, end + offset) })
        .flatMap(oc => $.setStateL(State.cursor)(oc))

    def clearStateCursor: Callback = $.setStateL(State.cursor)(None)

    def setCursorFromState: Callback =
      $.state.flatMap(s => s.cursor.map(setCursor).getOrElse(Callback.empty))

    def onTextChange: ReactEventFromInput => Callback =
      (e: ReactEventFromInput) => {
        $.props.flatMap { props =>
          // Capture the value outside setState, react reuses the events
          val v = e.target.value

          def setDisplayValue(s: String): Callback    = $.setStateL(State.displayValue)(s)
          def setModelValue(oa:  Option[A]): Callback =
            if (props.updateOn == UpdateValue.OnChange)
              oa.fold(Callback.empty)(a =>
                // Set both state.modelValue and props.value so we don't reset state when we don't want to
                $.setStateL(State.modelValue)(props.format.reverseGet(a)) *>
                  props.valSet(a) *> props.onChange(a)
              )
            else Callback.empty

          val result = props.changeAuditor(v, props.format)
          result match {
            case AuditResult.Accept(oa)                  =>
              clearStateCursor *> setDisplayValue(v) *> setModelValue(oa)
            case AuditResult.NewString(newS, oa, offset) =>
              setStateCursor(offset) *> setDisplayValue(newS) *> setModelValue(oa)
            case AuditResult.Reject()                    => setStateCursor(-1)
          }
        }
      }

    def onBlur(c: ChangeCallback[A]): Callback = for {
      s  <- $.state
      v   = s.displayValue
      p  <- $.props
      cb <- p.format.getOption(v).map(a => p.valSet(a) *> c(a)).getOrEmpty
    } yield cb

    val OuterDiv = <.div()

    def render(p: Props[EV, A], s: State) =
      OuterDiv.withRef(outerRef)(
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
          onTextChange,
          p.required,
          p.size,
          p.tabIndex,
          p.tpe,
          p.transparent,
          p.width,
          s.displayValue
        )(
          (p.modifiers :+ (^.id := p.id) :+ (^.onBlur --> onBlur(p.onBlurC)): _*)
        )
      )

  }

  protected val component =
    ScalaComponent
      .builder[Props[Any, Any]]
      .getDerivedStateFromPropsAndState[State] { (props, stateOpt) =>
        val newValue = props.valGet
        // Force new value from props if the exernal value changes externally
        // (or we are initializing).
        stateOpt match {
          case Some(state) if newValue === state.modelValue => state
          case _                                            => State(newValue, newValue, None)
        }
      }
      .renderBackend[Backend[Any, Any]]
      .componentDidUpdate(_.backend.setCursorFromState)
      .build
}
