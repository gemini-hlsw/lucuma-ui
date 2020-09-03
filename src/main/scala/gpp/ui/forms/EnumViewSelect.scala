// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.Show
import cats.effect.Effect
import cats.implicits._
import crystal.ViewOptF
import crystal.react.implicits._
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.util.Enumerated
import react.common.ReactProps
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormDropdown
import react.semanticui.collections.form.FormSelect
import react.semanticui.elements.icon.Icon
import react.semanticui.modules.dropdown.Dropdown._
import react.semanticui.modules.dropdown._

import scalajs.js.JSConverters._
import scalajs.js
import scalajs.js.|

/**
 * Produces a dropdown menu, similar to a combobox
 */
final case class EnumViewSelect[F[_], A](
  value:                ViewOptF[F, A],
  as:                   js.UndefOr[AsC] = js.undefined,
  basic:                js.UndefOr[Boolean] = js.undefined,
  button:               js.UndefOr[Boolean] = js.undefined,
  className:            js.UndefOr[String] = js.undefined,
  clazz:                js.UndefOr[Css] = js.undefined,
  closeOnBlur:          js.UndefOr[Boolean] = js.undefined,
  closeOnEscape:        js.UndefOr[Boolean] = js.undefined,
  closeOnChange:        js.UndefOr[Boolean] = js.undefined,
  compact:              js.UndefOr[Boolean] = js.undefined,
  deburr:               js.UndefOr[Boolean] = js.undefined,
  defaultOpen:          js.UndefOr[Boolean] = js.undefined,
  defaultSearchQuery:   js.UndefOr[String] = js.undefined,
  defaultSelectedLabel: js.UndefOr[JsNumber | String] = js.undefined,
  defaultUpward:        js.UndefOr[Boolean] = js.undefined,
  defaultValue:         js.UndefOr[A] = js.undefined,
  direction:            js.UndefOr[Direction] = js.undefined,
  disabled:             js.UndefOr[Boolean] = js.undefined,
  error:                js.UndefOr[Boolean] = js.undefined,
  floating:             js.UndefOr[Boolean] = js.undefined,
  fluid:                js.UndefOr[Boolean] = js.undefined,
  header:               js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  icon:                 js.UndefOr[ShorthandS[Icon]] = js.undefined,
  inline:               js.UndefOr[Boolean] = js.undefined,
  item:                 js.UndefOr[Boolean] = js.undefined,
  label:                js.UndefOr[ShorthandS[String]] = js.undefined,
  labeled:              js.UndefOr[Boolean] = js.undefined,
  loading:              js.UndefOr[Boolean] = js.undefined,
  minCharacters:        js.UndefOr[JsNumber] = js.undefined,
  noResultsMessage:     js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  onBlur:               js.UndefOr[Callback] = js.undefined,
  onBlurE:              js.UndefOr[OnBlur] = js.undefined,
  onClick:              js.UndefOr[Callback] = js.undefined,
  onClickE:             js.UndefOr[OnClick] = js.undefined,
  onChange:             js.UndefOr[FormDropdown.OnChange] = js.undefined,
  onChangeE:            js.UndefOr[FormDropdown.OnChangeE] = js.undefined,
  onClose:              js.UndefOr[Callback] = js.undefined,
  onCloseE:             js.UndefOr[OnClose] = js.undefined,
  onFocus:              js.UndefOr[Callback] = js.undefined,
  onFocusE:             js.UndefOr[OnFocus] = js.undefined,
  onLabelClick:         js.UndefOr[Callback] = js.undefined,
  onLabelClickE:        js.UndefOr[OnLabelClick] = js.undefined,
  onMouseDown:          js.UndefOr[Callback] = js.undefined,
  onMouseDownE:         js.UndefOr[OnMouseDown] = js.undefined,
  onOpen:               js.UndefOr[Callback] = js.undefined,
  onOpenE:              js.UndefOr[OnOpen] = js.undefined,
  onSearchChange:       js.UndefOr[OnSearchChange] = js.undefined,
  onSearchChangeE:      js.UndefOr[OnSearchChangeE] = js.undefined,
  open:                 js.UndefOr[Boolean] = js.undefined,
  openOnFocus:          js.UndefOr[Boolean] = js.undefined,
  placeholder:          js.UndefOr[String] = js.undefined,
  pointing:             js.UndefOr[Pointing] = js.undefined,
  renderLabel:          js.UndefOr[RenderLabel] = js.undefined,
  required:             js.UndefOr[Boolean] = js.undefined,
  scrolling:            js.UndefOr[Boolean] = js.undefined,
  search:               js.UndefOr[Boolean | SearchFunction] = js.undefined,
  searchInput:          js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  searchQuery:          js.UndefOr[String] = js.undefined,
  selectOnBlur:         js.UndefOr[Boolean] = js.undefined,
  selectOnNavigation:   js.UndefOr[Boolean] = js.undefined,
  selectedLabel:        js.UndefOr[JsNumber | String] = js.undefined,
  simple:               js.UndefOr[Boolean] = js.undefined,
  tabIndex:             js.UndefOr[String | JsNumber] = js.undefined,
  text:                 js.UndefOr[String] = js.undefined,
  tpe:                  js.UndefOr[String] = js.undefined,
  trigger:              js.UndefOr[VdomNode] = js.undefined,
  upward:               js.UndefOr[Boolean] = js.undefined,
  width:                js.UndefOr[SemanticWidth] = js.undefined,
  wrapSelection:        js.UndefOr[Boolean] = js.undefined,
  modifiers:            Seq[TagMod] = Seq.empty
)(implicit
  val enum:             Enumerated[A],
  val show:             Show[A],
  val effect:           Effect[F]
) extends ReactProps[EnumViewSelect[Any, Any]](EnumViewSelect.component) {
  def apply(mods: TagMod*): EnumViewSelect[F, A] = copy(modifiers = modifiers ++ mods)
}

object EnumViewSelect {
  type Props[F[_], A] = EnumViewSelect[F, A]

  protected val component =
    ScalaComponent
      .builder[Props[Any, Any]]
      .stateless
      .render_P { p =>
        implicit val show   = p.show
        implicit val effect = p.effect

        FormSelect(
          additionLabel = js.undefined,
          additionPosition = js.undefined,
          allowAdditions = js.undefined,
          p.as,
          p.basic,
          p.button,
          p.className,
          p.clazz,
          clearable = false,
          p.closeOnBlur,
          p.closeOnEscape,
          p.closeOnChange,
          p.compact,
          content = js.undefined,
          control = js.undefined,
          p.deburr,
          p.defaultOpen,
          p.defaultSearchQuery,
          p.defaultSelectedLabel,
          p.defaultUpward,
          p.defaultValue.map(i => p.enum.tag(i)),
          p.direction,
          p.disabled,
          p.error,
          p.floating,
          p.fluid,
          p.header,
          p.icon,
          p.inline,
          p.item,
          p.label,
          p.labeled,
          lazyLoad = false,
          p.loading,
          p.minCharacters,
          multiple = false,
          p.noResultsMessage,
          onAddItem = js.undefined,
          p.onBlur,
          p.onBlurE,
          onChange = js.undefined,
          (e: ReactEvent, ddp: FormDropdown.FormDropdownProps) =>
            ddp.value.toOption
              .flatMap(v => p.enum.fromTag(v.asInstanceOf[String]))
              .map(v => p.value.set(v).runInCB)
              .getOrEmpty
              >> p.onChangeE
                .map(_(e, ddp))
                .toOption
                .orElse(p.onChange.map(_(ddp)).toOption)
                .getOrEmpty,
          p.onClick,
          p.onClickE,
          p.onClose,
          p.onCloseE,
          p.onFocus,
          p.onFocusE,
          p.onLabelClick,
          p.onLabelClickE,
          p.onMouseDown,
          p.onMouseDownE,
          p.onOpen,
          p.onOpenE,
          p.onSearchChange,
          p.onSearchChangeE,
          p.open,
          p.openOnFocus,
          options = p.enum.all.map(i => DropdownItem(text = i.show, value = p.enum.tag(i))),
          p.placeholder,
          p.pointing,
          p.renderLabel,
          p.required,
          p.scrolling,
          p.search,
          p.searchInput,
          p.searchQuery,
          p.selectOnBlur,
          p.selectOnNavigation,
          p.selectedLabel,
          p.simple,
          p.tabIndex,
          p.text,
          p.tpe,
          p.trigger,
          p.upward,
          p.value.get.map(i => p.enum.tag(i)).orUndefined,
          p.width,
          p.wrapSelection,
          p.modifiers
        )
      }
      .build
}
