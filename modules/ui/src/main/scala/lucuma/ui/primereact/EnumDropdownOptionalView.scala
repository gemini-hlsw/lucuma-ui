// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.*
import react.primereact.Dropdown
import react.primereact.SelectItem

import scalajs.js
import scalajs.js.JSConverters.*

final case class EnumDropdownOptionalView[A](
  id:              NonEmptyString,
  value:           View[Option[A]],
  exclude:         Set[A] = Set.empty[A],
  className:       js.UndefOr[String] = js.undefined,
  clazz:           js.UndefOr[Css] = js.undefined,
  showClear:       Boolean = true,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined
)(using
  val enumerated:  Enumerated[A],
  val display:     Display[A]
) extends ReactFnProps[EnumDropdownOptionalView[Any]](EnumDropdownOptionalView.component)

object EnumDropdownOptionalView {
  private def buildComponent[A] = ScalaFnComponent[EnumDropdownOptionalView[A]] { props =>
    Dropdown(
      id = props.id.value,
      value = props.value.get.orUndefined,
      options = props.enumerated.all
        .filter(v => !props.exclude.contains(v))
        .map(e => SelectItem(label = props.display.shortName(e), value = e)),
      showClear = props.showClear,
      className = props.className,
      clazz = props.clazz,
      filter = props.filter,
      showFilterClear = props.showFilterClear,
      placeholder = props.placeholder,
      disabled = props.disabled,
      onChange = v => props.value.set(if (js.isUndefined(v)) none else v.asInstanceOf[A].some)
    )
  }

  private val component = buildComponent[Any]
}
