// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.ui.forms

import scala.scalajs.js
import scala.scalajs.js.|

import cats.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.ReactEventFromInput
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import monocle.Iso
import monocle.Prism
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormInput
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.input._
import react.semanticui.elements.label._

/**
  * FormInput component that uses a EVar to share the content of the field
  */
final case class FormInputEV[A](
  name:           String,
  id:             String,
  action:         js.UndefOr[ShorthandSB[VdomNode]] = js.undefined,
  actionPosition: js.UndefOr[ActionPosition] = js.undefined,
  as:             js.UndefOr[AsC] = js.undefined,
  className:      js.UndefOr[String] = js.undefined,
  clazz:          js.UndefOr[Css] = js.undefined,
  content:        js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  control:        js.UndefOr[String] = js.undefined,
  disabled:       js.UndefOr[Boolean] = js.undefined,
  error:          js.UndefOr[ShorthandB[Label]] = js.undefined,
  fluid:          js.UndefOr[Boolean] = js.undefined,
  focus:          js.UndefOr[Boolean] = js.undefined,
  icon:           js.UndefOr[ShorthandSB[Icon]] = js.undefined,
  iconPosition:   js.UndefOr[IconPosition] = js.undefined,
  inline:         js.UndefOr[Boolean] = js.undefined,
  input:          js.UndefOr[VdomNode] = js.undefined,
  inverted:       js.UndefOr[Boolean] = js.undefined,
  label:          js.UndefOr[ShorthandS[Label]] = js.undefined,
  labelPosition:  js.UndefOr[LabelPosition] = js.undefined,
  loading:        js.UndefOr[Boolean] = js.undefined,
  required:       js.UndefOr[Boolean] = js.undefined,
  size:           js.UndefOr[SemanticSize] = js.undefined,
  tabIndex:       js.UndefOr[String | JsNumber] = js.undefined,
  tpe:            js.UndefOr[String] = js.undefined,
  transparent:    js.UndefOr[Boolean] = js.undefined,
  width:          js.UndefOr[SemanticWidth] = js.undefined,
  snapshot:       StateSnapshot[A],
  prism:          Prism[A, String] = Iso.id[String].asPrism,
  onChange:       FormInputEV.ChangeCallback[A] = (_: A) => Callback.empty, // callback for parents of this component
  onBlur:         FormInputEV.ChangeCallback[A] = (_: A) => Callback.empty
) extends ReactProps {
  @inline def render: VdomElement = FormInputEV.component(this)
  def valGet: String              = prism.getOption(snapshot.value).orEmpty
  def valSet(s: String): Callback = snapshot.setState(prism.reverseGet(s))
  val onBlurC: FormInputEV.ChangeCallback[String] =
    (s: String) => onBlur(prism.reverseGet(s))
  val onChangeC: FormInputEV.ChangeCallback[String] =
    (s: String) => onChange(prism.reverseGet(s))
}

object FormInputEV {
  type Props             = FormInputEV[_]
  type ChangeCallback[A] = A => Callback
  type Backend           = RenderScope[Props, State, Unit]

  final case class State(curValue: Option[String], changed: Boolean = false)

  def onTextChange(b: Backend): ReactEventFromInput => Callback = (e: ReactEventFromInput) => {
    // Capture the value outside setState, react reuses the events
    val v = e.target.value
    // First update the internal state, then call the outside listener
    b.setState(State(v.some, changed = true)) *>
      b.props.valSet(v) *>
      b.props.onChangeC(v)
  }

  def onBlur(b: Backend, c: ChangeCallback[String]): Callback =
    c(b.state.curValue.orEmpty)

  protected val component =
    ScalaComponent
      .builder[Props]("FormInputEV")
      .initialState(State(None))
      .render { b =>
        val p = b.props
        val s = b.state
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
          onTextChange(b),
          p.required,
          p.size,
          p.tabIndex,
          p.tpe,
          p.transparent,
          p.width,
          s.curValue.orEmpty
        )(^.id := p.id, ^.onBlur --> onBlur(b, p.onBlurC))
      }
      .componentWillMount { ctx =>
        // Update state of the input if the property has changed
        ctx
          .setState(State(ctx.props.valGet.some))
          .when_((ctx.props.valGet.some =!= ctx.state.curValue) && !ctx.state.changed)
      }
      .componentWillReceiveProps { ctx =>
        // Update state of the input if the property has changed
        // TBD Should check if the state has changed?
        ctx
          .setState(State(ctx.nextProps.valGet.some))
          .when_(ctx.nextProps.valGet.some =!= ctx.state.curValue)
      }
      .build
}
