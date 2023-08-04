// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.floatingui
import lucuma.react.primereact.Dropdown
import lucuma.react.primereact.SelectItem

import scalajs.js

final case class FormDropdown[V[_], A](
  id:               NonEmptyString,
  value:            A,
  options:          List[SelectItem[A]],
  label:            js.UndefOr[TagMod] = js.undefined,
  size:             js.UndefOr[PlSize] = js.undefined,
  clazz:            js.UndefOr[Css] = js.undefined,
  panelClass:       js.UndefOr[Css] = js.undefined,
  filter:           js.UndefOr[Boolean] = js.undefined,
  showFilterClear:  js.UndefOr[Boolean] = js.undefined,
  placeholder:      js.UndefOr[String] = js.undefined,
  disabled:         js.UndefOr[Boolean] = js.undefined,
  dropdownIcon:     js.UndefOr[String] = js.undefined,
  tooltip:          js.UndefOr[VdomNode] = js.undefined,
  tooltipPlacement: floatingui.Placement = floatingui.Placement.Top,
  onChange:         js.UndefOr[A => Callback] = js.undefined,
  onChangeE:        js.UndefOr[(A, ReactEvent) => Callback] = js.undefined, // called after onChange
  modifiers:        Seq[TagMod] = Seq.empty
)(using val eqAA: Eq[A])
    extends ReactFnProps(FormDropdown.component):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object FormDropdown {
  private type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[FormDropdown[V, A]] { props =>
    import props.given

    React.Fragment(
      props.label.map(l => FormLabel(htmlFor = props.id, size = props.size)(l)),
      Dropdown(
        id = props.id.value,
        value = props.value,
        options = props.options,
        clazz = LucumaPrimeStyles.FormField |+| props.clazz.toOption.orEmpty,
        panelClass = props.panelClass,
        filter = props.filter,
        showFilterClear = props.showFilterClear,
        placeholder = props.placeholder,
        disabled = props.disabled,
        dropdownIcon = props.dropdownIcon,
        onChange = props.onChange,
        onChangeE = props.onChangeE,
        modifiers = props.modifiers
      )
    )
  }

  private val component = buildComponent[AnyF, Any]
}
