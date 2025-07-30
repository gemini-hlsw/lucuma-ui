// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.Dropdown
import lucuma.react.primereact.SelectItem
import lucuma.react.primereact.TooltipOptions

import scalajs.js

final case class FormDropdown[A](
  id:              NonEmptyString,
  value:           A,
  options:         List[SelectItem[A]],
  label:           js.UndefOr[TagMod] = js.undefined,
  size:            js.UndefOr[PlSize] = js.undefined,
  clazz:           js.UndefOr[Css] = js.undefined,
  panelClass:      js.UndefOr[Css] = js.undefined,
  labelClass:      js.UndefOr[Css] = js.undefined,
  filter:          js.UndefOr[Boolean] = js.undefined,
  showFilterClear: js.UndefOr[Boolean] = js.undefined,
  placeholder:     js.UndefOr[String] = js.undefined,
  disabled:        js.UndefOr[Boolean] = js.undefined,
  dropdownIcon:    js.UndefOr[String] = js.undefined,
  tooltip:         js.UndefOr[String] = js.undefined,
  tooltipOptions:  js.UndefOr[TooltipOptions] = js.undefined,
  itemTemplate:    js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  valueTemplate:   js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  onChange:        js.UndefOr[A => Callback] = js.undefined,
  onChangeE:       js.UndefOr[(A, ReactEvent) => Callback] = js.undefined, // called after onChange
  modifiers:       Seq[TagMod] = Seq.empty
)(using val eqAA: Eq[A])
    extends ReactFnProps(FormDropdown.component):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object FormDropdown:
  private def buildComponent[A] = ScalaFnComponent[FormDropdown[A]]: props =>
    import props.given

    React.Fragment(
      props.label.map(l =>
        FormLabel(htmlFor = props.id, size = props.size, clazz = props.labelClass)(l)
      ),
      Dropdown(
        id = props.id.value,
        value = props.value,
        options = props.options,
        clazz = LucumaPrimeStyles.FormField |+| props.clazz.toOption.orEmpty,
        panelClass = props.panelClass,
        filter = props.filter,
        showFilterClear = props.showFilterClear,
        placeholder = props.placeholder,
        tooltip = props.tooltip,
        tooltipOptions = props.tooltipOptions,
        disabled = props.disabled,
        dropdownIcon = props.dropdownIcon,
        itemTemplate = props.itemTemplate,
        valueTemplate = props.valueTemplate,
        onChange = props.onChange,
        onChangeE = props.onChangeE,
        modifiers = props.modifiers
      )
    )

  private val component = buildComponent[Any]
