// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.primereact.InputGroup
import lucuma.react.primereact.InputText
import lucuma.react.primereact.TooltipOptions
import lucuma.react.primereact.hooks.useDebounce

import scalajs.js

case class DebouncedInputText(
  id:             NonEmptyString,
  delayMillis:    Int,
  clazz:          js.UndefOr[Css] = js.undefined,
  value:          js.UndefOr[String] = js.undefined,
  disabled:       js.UndefOr[Boolean] = js.undefined,
  placeholder:    js.UndefOr[String] = js.undefined,
  tooltip:        js.UndefOr[String] = js.undefined,
  tooltipOptions: js.UndefOr[TooltipOptions] = js.undefined,
  onFocus:        js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onBlur:         js.UndefOr[ReactFocusEventFromInput => Callback] = js.undefined,
  onChange:       js.UndefOr[String => Callback] = js.undefined,
  onKeyDown:      js.UndefOr[ReactKeyboardEventFromInput => Callback] = js.undefined,
  showClear:      Boolean = true,
  modifiers:      Seq[TagMod] = Seq.empty
) extends ReactFnProps[DebouncedInputText](DebouncedInputText):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)

object DebouncedInputText
    extends ReactFnComponent[DebouncedInputText](props =>
      for
        inputValue    <- useDebounce(props.value.getOrElse(""), props.delayMillis)
        avoidCallback <- useRef(true)
        _             <- // Update the caller value, but don't do it until user starts typing
          useEffectWithDeps(inputValue.debouncedValue): v =>
            props.onChange.toOption.map(_(v)).getOrEmpty.when(!avoidCallback.value).void
        _             <- // Update the internal value if the caller value changes
          useEffectWithDeps(props.value.getOrElse("")): v =>
            Callback.when(v != inputValue.debouncedValue)(inputValue.set(v))
      yield
        val inputControl =
          InputText(
            id = props.id.value,
            value = inputValue.value,
            clazz = props.clazz,
            disabled = props.disabled,
            placeholder = props.placeholder,
            onFocus = props.onFocus,
            onBlur = props.onBlur,
            onChange = e => inputValue.set(e.target.value),
            onKeyDown =
              e => avoidCallback.set(false) >> props.onKeyDown.toOption.map(_(e)).getOrEmpty,
            modifiers = props.modifiers
          )

        if props.showClear then
          InputGroup(
            inputControl,
            InputGroup
              .Addon(
                ^.cursor.pointer,
                ^.onClick ==> (e =>
                  e.preventDefaultCB >> e.stopPropagationCB >>
                    inputValue.set("") >> props.onChange.toOption.map(_("")).getOrEmpty
                )
              )(LucumaPrimeStyles.IconTimes)
              .when(inputValue.value.nonEmpty)
          )
        else inputControl
    )
