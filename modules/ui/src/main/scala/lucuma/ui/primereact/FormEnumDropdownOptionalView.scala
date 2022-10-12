// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.*
import reactST.primereact.selectitemMod.SelectItem

import scalajs.js
import scalajs.js.JSConverters.*

final case class FormEnumDropdownOptionalView[A](
  id:        NonEmptyString,
  value:     View[Option[A]],
  label:     js.UndefOr[TagMod] = js.undefined,
  exclude:   Set[A] = Set.empty[A],
  clazz:     js.UndefOr[Css] = js.undefined,
  showClear: Boolean =
    true, // The default in `Dropdown` is false, but in this case we usually want true.
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined
)(using
  val enumerated:  Enumerated[A],
  val display:     Display[A]
) extends ReactFnProps[FormEnumDropdownOptionalView[Any]](
      FormEnumDropdownOptionalView.component
    )

object FormEnumDropdownOptionalView {
  private def buildComponent[A] = ScalaFnComponent[FormEnumDropdownOptionalView[A]] { props =>
    import props.given

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id)(l)),
      EnumDropdownOptionalView(
        id = props.id,
        value = props.value,
        exclude = props.exclude,
        clazz = LucumaStyles.FormField |+| props.clazz.toOption.orEmpty,
        showClear = props.showClear,
        filter = props.filter,
        showFilterClear = props.showFilterClear,
        disabled = props.disabled,
        placeholder = props.placeholder
      )
    )
  }

  private val component = buildComponent[Any]
}
