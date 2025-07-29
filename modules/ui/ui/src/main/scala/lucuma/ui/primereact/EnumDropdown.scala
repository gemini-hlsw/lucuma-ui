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
import lucuma.react.primereact.Dropdown
import lucuma.react.primereact.SelectItem

import scalajs.js

case class EnumDropdown[A](
  id:              NonEmptyString,
  value:           A,
  exclude:         Set[A] = Set.empty[A],
  disabledItems:   Set[A] = Set.empty[A],
  clazz:           js.UndefOr[Css] = js.undefined,
  panelClass:      js.UndefOr[Css] = js.undefined,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  size:            js.UndefOr[PlSize] = js.undefined,
  itemTemplate:    js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  valueTemplate:   js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  onChange:        js.UndefOr[A => Callback] = js.undefined,
  onChangeE:       js.UndefOr[(A, ReactEvent) => Callback] = js.undefined, // called after onChange
  placeholder:     js.UndefOr[String] = js.undefined,
  modifiers:       Seq[TagMod] = Seq.empty
)(using val enumerated: Enumerated[A], val display: Display[A])
    extends ReactFnProps(EnumDropdown.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object EnumDropdown {
  private def buildComponent[A] = ScalaFnComponent[EnumDropdown[A]] { props =>
    import props.given

    val sizeCls = props.size.toOption.map(_.cls).orEmpty

    Dropdown(
      id = props.id.value,
      value = props.value,
      options = props.enumerated.all
        .filter(v => !props.exclude.contains(v))
        .map(e =>
          SelectItem(
            label = props.display.shortName(e),
            value = e,
            disabled = props.disabledItems.contains(e)
          )
        ),
      clazz = props.clazz.toOption.orEmpty |+| sizeCls,
      panelClass = props.panelClass.toOption.orEmpty |+| sizeCls,
      filter = props.filter,
      showFilterClear = props.showFilterClear,
      disabled = props.disabled,
      placeholder = props.placeholder,
      itemTemplate = props.itemTemplate,
      valueTemplate = props.valueTemplate,
      onChange = props.onChange,
      onChangeE = props.onChangeE,
      modifiers = props.modifiers
    )
  }

  private val component = buildComponent[Any]
}
