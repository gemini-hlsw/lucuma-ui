// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.Half.A
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.*
import reactST.primereact.selectitemMod.SelectItem

import scalajs.js
import scalajs.js.JSConverters.*

final case class FormEnumDropdownView[A](
  id:              NonEmptyString,
  value:           View[A],
  label:           js.UndefOr[String] = js.undefined,
  exclude:         Set[A] = Set.empty[A],
  clazz:           js.UndefOr[Css] = js.undefined,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined,
  modifiers:       Seq[TagMod] = Seq.empty
)(using
  val enumerated:  Enumerated[A],
  val display:     Display[A]
) extends ReactFnProps(FormEnumDropdownView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object FormEnumDropdownView {
  private def buildComponent[A] = ScalaFnComponent[FormEnumDropdownView[A]] { props =>
    import props.given

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id)(l)),
      EnumDropdownView(
        id = props.id,
        value = props.value,
        exclude = props.exclude,
        clazz = LucumaStyles.FormField |+| props.clazz.toOption.orEmpty,
        filter = props.filter,
        showFilterClear = props.showFilterClear,
        disabled = props.disabled,
        placeholder = props.placeholder,
        modifiers = props.modifiers
      )
    )
  }

  private val component = buildComponent[Any]
}
