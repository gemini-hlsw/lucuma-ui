// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.*
import react.primereact.DropdownOptional
import react.primereact.SelectItem

import scalajs.js
import scalajs.js.JSConverters.*

final case class EnumDropdownOptionalView[A](
  id:              NonEmptyString,
  value:           View[Option[A]],
  exclude:         Set[A] = Set.empty[A],
  clazz:           js.UndefOr[Css] = js.undefined,
  showClear:       Boolean = true,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined,
  modifiers:       Seq[TagMod] = Seq.empty
)(using
  val enumerated:  Enumerated[A],
  val display:     Display[A]
) extends ReactFnProps[EnumDropdownOptionalView[Any]](EnumDropdownOptionalView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object EnumDropdownOptionalView {
  private def buildComponent[A] = ScalaFnComponent[EnumDropdownOptionalView[A]] { props =>
    import props.given

    DropdownOptional(
      id = props.id.value,
      value = props.value.get,
      options = props.enumerated.all
        .filter(v => !props.exclude.contains(v))
        .map(e => SelectItem(label = props.display.shortName(e), value = e)),
      showClear = props.showClear,
      clazz = props.clazz,
      filter = props.filter,
      showFilterClear = props.showFilterClear,
      placeholder = props.placeholder,
      disabled = props.disabled,
      onChange = v => props.value.set(v),
      modifiers = props.modifiers
    )
  }

  private val component = buildComponent[Any]
}
