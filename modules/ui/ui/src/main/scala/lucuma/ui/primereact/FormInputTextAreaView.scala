// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.*
import cats.syntax.all.*
import crystal.react.View
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.TooltipOptions

import scala.scalajs.js

/**
 * FormInput component that uses a crystal View to share the content of the field
 */
final case class FormInputTextAreaView[V[_]](
  id:             NonEmptyString,
  value:          V[String],
  label:          js.UndefOr[TagMod] = js.undefined,
  size:           js.UndefOr[PlSize] = js.undefined,
  error:          js.UndefOr[NonEmptyString] = js.undefined,
  autoResize:     js.UndefOr[Boolean] = js.undefined,
  tooltip:        js.UndefOr[String] = js.undefined,
  tooltipOptions: js.UndefOr[TooltipOptions] = js.undefined,
  onTextChange:   String => Callback = _ => Callback.empty,
  clazz:          js.UndefOr[Css] = js.undefined,
  labelClass:     js.UndefOr[Css] = js.undefined,
  modifiers:      Seq[TagMod] = Seq.empty
)(using val vl: ViewLike[V])
    extends ReactFnProps(FormInputTextAreaView.component):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

  private def valGet: String             = vl.get(value).orEmpty
  private def valSet: String => Callback = vl.set(value)

object FormInputTextAreaView:
  private type AnyF[_] = Any

  private type Props[V[_]] = FormInputTextAreaView[V]

  private def onChange[V[_]](
    valueView:    View[String],
    onTextChange: String => Callback
  ): ReactEventFromTextArea => Callback =
    (e: ReactEventFromTextArea) => {
      val v = e.target.value
      valueView.set(v) *> onTextChange(v)
    }

  private def onBlur[V[_]](props: Props[V], valueView: View[String]): Callback =
    props.valSet(valueView.get)

  private def buildComponent[V[_]] = ScalaFnComponent
    .withHooks[Props[V]]
    .useStateViewBy(props => props.valGet)
    .useEffectWithDepsBy((props, _) => props.valGet)((_, valueView) =>
      newValue => valueView.set(newValue)
    )
    .render((props, valueView) =>
      FormInputTextArea(
        props.id,
        valueView.get,
        props.label,
        props.size,
        props.autoResize,
        props.tooltip,
        props.tooltipOptions,
        props.labelClass,
        props.clazz,
        props.modifiers
      )(
        ^.onChange ==> onChange[V](valueView, props.onTextChange),
        ^.onBlur --> onBlur(props, valueView)
      )
    )

  private val component = buildComponent[AnyF]
