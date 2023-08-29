// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.primereact.SelectButton
import lucuma.react.primereact.SelectItem

import scalajs.js

case class SelectButtonEnumView[V[_], A](
  id:             NonEmptyString,
  view:           V[A],
  label:          js.UndefOr[TagMod] = js.undefined,
  groupClass:     js.UndefOr[Css] = js.undefined,
  buttonClass:    js.UndefOr[Css] = js.undefined,
  size:           js.UndefOr[PlSize] = js.undefined,
  itemTemplate:   js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  onChange:       A => Callback = (_: A) => Callback.empty,
  modifiers:      Seq[TagMod] = Seq.empty
)(using
  val enumerated: Enumerated[A],
  val display:    Display[A],
  val vl:         ViewLike[V]
) extends ReactFnProps(SelectButtonEnumView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object SelectButtonEnumView {
  private type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[SelectButtonEnumView[V, A]] { props =>
    import props.given

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id, size = props.size)(l)),
      props.view.get.map { value =>
        SelectButton[A](
          value = value,
          Enumerated[A].all.map { s =>
            SelectItem(s, label = props.display.shortName(s), clazz = props.buttonClass)
          },
          id = props.id.value,
          clazz = props.groupClass,
          itemTemplate = props.itemTemplate,
          onChange = (a: A) => props.view.set(a) *> props.onChange(a)
        )
      }
    )
  }

  private val component = buildComponent[AnyF, Any]
}
