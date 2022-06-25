// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
import crystal.react.View
import crystal.react.hooks._
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import react.common._
import react.semanticui._
import react.semanticui.addons.textarea.TextArea
import react.semanticui.collections.form.FormTextArea
import react.semanticui.elements.label._

import scala.scalajs.js
import scala.scalajs.js.|

// final case class FormTextAreaEV[EV[_]](
//   id:              NonEmptyString,
//   as:              js.UndefOr[AsC] = js.undefined,
//   className:       js.UndefOr[String] = js.undefined,
//   clazz:           js.UndefOr[Css] = js.undefined,
//   content:         js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
//   control:         js.UndefOr[String] = js.undefined,
//   disabled:        js.UndefOr[Boolean] = js.undefined,
//   error:           js.UndefOr[ShorthandB[Label]] = js.undefined,
//   inline:          js.UndefOr[Boolean] = js.undefined,
//   label:           js.UndefOr[ShorthandS[Label]] = js.undefined,
//   onTextChange:    String => Callback = _ => Callback.empty,
//   required:        js.UndefOr[Boolean] = js.undefined,
//   rows:            js.UndefOr[Int | String] = js.undefined,
//   tpe:             js.UndefOr[String] = js.undefined,
//   value:           EV[String],
//   width:           js.UndefOr[SemanticWidth] = js.undefined,
//   modifiers:       Seq[TagMod] = Seq.empty
// )(implicit val ev: ExternalValue[EV])
//     extends ReactFnProps[FormTextAreaEV[Any]](FormTextAreaEV.component) {
//   def valGet: String = ev.get(value).orEmpty
//
//   def valSet: String => Callback = ev.set(value)
// }
//
// object FormTextAreaEV {
//   type Props[EV[_]] = FormTextAreaEV[EV]
//
//   private def onChange[EV[_]](props: Props[EV], valueView: View[String]): TextArea.Event =
//     (_: TextArea.ReactChangeEvent, tap: TextArea.TextAreaProps) => {
//       val v = tap.value.asInstanceOf[String]
//       valueView.set(v) >> props.onTextChange(v)
//     }
//
//   private def onBlur[EV[_]](props: Props[EV], valueView: View[String]): Callback =
//     props.valSet(valueView.get)
//
//   private def buildComponent[EV[_]] = ScalaFnComponent
//     .withHooks[Props[EV]]
//     .useStateViewBy(props => props.valGet)
//     .useEffectWithDepsBy((props, _) => props.valGet)((_, valueView) =>
//       newValue => valueView.set(newValue)
//     )
//     .render((props, valueView) =>
//       FormTextArea(
//         as = props.as,
//         className = props.className,
//         clazz = props.clazz,
//         content = props.content,
//         control = props.control,
//         disabled = props.disabled,
//         error = props.error,
//         inline = props.inline,
//         label = props.label,
//         onChangeE = onChange[EV](props, valueView),
//         required = props.required,
//         rows = props.rows,
//         tpe = props.tpe,
//         value = valueView.get,
//         width = props.width,
//         modifiers =
//           props.modifiers :+ (^.id := props.id.value) :+ (^.onBlur --> onBlur(props, valueView))
//       )
//     )
//
//   protected val component = buildComponent[Any]
// }
