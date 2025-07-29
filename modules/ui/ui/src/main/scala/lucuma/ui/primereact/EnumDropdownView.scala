// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.primereact.DropdownOptional
import lucuma.react.primereact.SelectItem

import scalajs.js

final case class EnumDropdownView[V[_], A](
  id:              NonEmptyString,
  value:           V[A],
  exclude:         Set[A] = Set.empty[A],
  disabledItems:   Set[A] = Set.empty[A],
  clazz:           js.UndefOr[Css] = js.undefined,
  panelClass:      js.UndefOr[Css] = js.undefined,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined,
  size:            js.UndefOr[PlSize] = js.undefined,
  itemTemplate:    js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  valueTemplate:   js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  onChangeE:       js.UndefOr[(Option[A], ReactEvent) => Callback] =
    js.undefined, // called after the view is set
  modifiers:       Seq[TagMod] = Seq.empty
)(using
  val enumerated:  Enumerated[A],
  val display:     Display[A],
  val vl:          ViewLike[V]
) extends ReactFnProps(EnumDropdownView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object EnumDropdownView {
  private type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[EnumDropdownView[V, A]] { props =>
    import props.given

    val sizeCls = props.size.toOption.map(_.cls).orEmpty

    DropdownOptional(
      value = props.value.get,
      options = props.enumerated.all
        .filter(v => !props.exclude.contains(v))
        .map(e =>
          SelectItem(
            label = props.display.shortName(e),
            value = e,
            disabled = props.disabledItems.contains(e)
          )
        ),
      id = props.id.value,
      clazz = props.clazz.toOption.orEmpty |+| sizeCls,
      panelClass = props.panelClass.toOption.orEmpty |+| sizeCls,
      filter = props.filter,
      showFilterClear = props.showFilterClear,
      disabled = props.disabled,
      placeholder = props.placeholder,
      itemTemplate = props.itemTemplate,
      valueTemplate = props.valueTemplate,
      onChange = v => props.value.set(v.get),
      onChangeE = props.onChangeE,
      modifiers = props.modifiers
    )
  }

  private val component = buildComponent[AnyF, Any]
}
