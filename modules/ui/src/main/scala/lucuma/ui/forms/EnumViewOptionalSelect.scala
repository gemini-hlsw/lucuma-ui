// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.Monoid
import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.util.DefaultEffects.{ Sync => DefaultS }
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.ReactProps
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormDropdown
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.label.Label
import react.semanticui.modules.dropdown.Dropdown._
import react.semanticui.modules.dropdown._

import scalajs.js.JSConverters._
import scalajs.js
import scalajs.js.|

/**
 * Produces a dropdown menu, similar to a combobox, for which the value is optional.
 */
final case class EnumViewOptionalSelect[EV[_], A](
  id:                   String,
  value:                EV[Option[A]],
  as:                   js.UndefOr[AsC] = js.undefined,
  basic:                js.UndefOr[Boolean] = js.undefined,
  button:               js.UndefOr[Boolean] = js.undefined,
  className:            js.UndefOr[String] = js.undefined,
  clazz:                js.UndefOr[Css] = js.undefined,
  clearable:            js.UndefOr[Boolean] = js.undefined,
  closeOnBlur:          js.UndefOr[Boolean] = js.undefined,
  closeOnEscape:        js.UndefOr[Boolean] = js.undefined,
  closeOnChange:        js.UndefOr[Boolean] = js.undefined,
  compact:              js.UndefOr[Boolean] = js.undefined,
  deburr:               js.UndefOr[Boolean] = js.undefined,
  defaultOpen:          js.UndefOr[Boolean] = js.undefined,
  defaultSearchQuery:   js.UndefOr[String] = js.undefined,
  defaultSelectedLabel: js.UndefOr[Double | String] = js.undefined,
  defaultUpward:        js.UndefOr[Boolean] = js.undefined,
  direction:            js.UndefOr[Direction] = js.undefined,
  disabled:             js.UndefOr[Boolean] = js.undefined,
  error:                js.UndefOr[Boolean] = js.undefined,
  floating:             js.UndefOr[Boolean] = js.undefined,
  fluid:                js.UndefOr[Boolean] = js.undefined,
  header:               js.UndefOr[ShorthandS[VdomNode]] = js.undefined,
  icon:                 js.UndefOr[ShorthandS[Icon]] = js.undefined,
  inline:               js.UndefOr[Boolean] = js.undefined,
  item:                 js.UndefOr[Boolean] = js.undefined,
  label:                js.UndefOr[ShorthandS[Label]] = js.undefined,
  labeled:              js.UndefOr[Boolean] = js.undefined,
  loading:              js.UndefOr[Boolean] = js.undefined,
  minCharacters:        js.UndefOr[Int] = js.undefined,
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
  selectedLabel:        js.UndefOr[Double | String] = js.undefined,
  simple:               js.UndefOr[Boolean] = js.undefined,
  tabIndex:             js.UndefOr[String | Int] = js.undefined,
  text:                 js.UndefOr[String] = js.undefined,
  tpe:                  js.UndefOr[String] = js.undefined,
  trigger:              js.UndefOr[VdomNode] = js.undefined,
  upward:               js.UndefOr[Boolean] = js.undefined,
  width:                js.UndefOr[SemanticWidth] = js.undefined,
  wrapSelection:        js.UndefOr[Boolean] = js.undefined,
  exclude:              Set[A] = Set.empty[A],
  modifiers:            Seq[TagMod] = Seq.empty
)(implicit
  val enumerated:       Enumerated[A],
  val display:          Display[A],
  val monoid:           Monoid[DefaultS[Unit]],
  val ev:               ExternalValue[EV]
) extends ReactProps[EnumViewSelectBase[EV]](EnumViewSelectBase.buildComponent[EV])
    with EnumViewSelectBase[EV] {

  type AA    = A
  type GG[X] = Option[X]
  type FF[X] = DefaultS[X]

  override val multiple = false

  def withMods(mods: TagMod*): EnumViewOptionalSelect[EV, A] = copy(modifiers = modifiers ++ mods)

  override def setter(ddp: FormDropdown.FormDropdownProps): Callback =
    ddp.value.toOption
      .map(v => ev.set(value)(enumerated.fromTag(v.asInstanceOf[String])))
      .orEmpty

  override def getter =
    ev.get(value).flatMap(_.map(enumerated.tag)).orUndefined

}
