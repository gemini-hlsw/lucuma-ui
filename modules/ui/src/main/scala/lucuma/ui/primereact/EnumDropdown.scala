// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.*
import react.primereact.Dropdown
import react.primereact.SelectItem

import scalajs.js
import scalajs.js.JSConverters.*

case class EnumDropdown[A](
  id:                   NonEmptyString,
  value:                A,
  exclude:              Set[A] = Set.empty[A],
  disabledItems:        Set[A] = Set.empty[A],
  clazz:                js.UndefOr[Css] = js.undefined,
  filter:               js.UndefOr[Boolean] = js.undefined,
  showFilterClear:      js.UndefOr[Boolean] = js.undefined,
  disabled:             js.UndefOr[Boolean] = js.undefined,
  onChange:             js.UndefOr[A => Callback] = js.undefined,
  placeholder:          js.UndefOr[String] = js.undefined,
  modifiers:            Seq[TagMod] = Seq.empty
)(using val enumerated: Enumerated[A], val display: Display[A])
    extends ReactFnProps(EnumDropdown.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object EnumDropdown {
  private def buildComponent[A] = ScalaFnComponent[EnumDropdown[A]] { props =>
    import props.given

    Dropdown(
      id = props.id.value,
      value = props.value,
      options = props.enumerated.all
        .filter(v => !props.exclude.contains(v))
        .map(e =>
          SelectItem(label = props.display.shortName(e),
                     value = e,
                     disabled = props.disabledItems.contains(e)
          )
        ),
      clazz = props.clazz,
      filter = props.filter,
      showFilterClear = props.showFilterClear,
      placeholder = props.placeholder,
      onChange = props.onChange,
      modifiers = props.modifiers
    )
  }

  private val component = buildComponent[Any]
}
