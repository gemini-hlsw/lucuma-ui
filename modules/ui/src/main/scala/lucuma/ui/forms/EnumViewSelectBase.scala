// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.syntax.all._
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common._
import react.semanticui._
import react.semanticui.collections.form.FormDropdown
import react.semanticui.collections.form.FormSelect
import react.semanticui.elements.icon.Icon
import react.semanticui.elements.label.Label
import react.semanticui.modules.dropdown.Dropdown._
import react.semanticui.modules.dropdown._

import scalajs.js
import scalajs.js.|

// /**
//  * Produces a dropdown menu, similar to a combobox. This is a base trait for various styles.
//  */
// trait EnumViewSelectBase[EV[_]] {
//   type AA
//   type GG[_]
//
//   val id: String
//   val value: EV[GG[AA]]
//   val as: js.UndefOr[AsC]
//   val basic: js.UndefOr[Boolean]
//   val button: js.UndefOr[Boolean]
//   val className: js.UndefOr[String]
//   val clazz: js.UndefOr[Css]
//   val clearable: js.UndefOr[Boolean]
//   val closeOnBlur: js.UndefOr[Boolean]
//   val closeOnEscape: js.UndefOr[Boolean]
//   val closeOnChange: js.UndefOr[Boolean]
//   val compact: js.UndefOr[Boolean]
//   val deburr: js.UndefOr[Boolean]
//   val defaultOpen: js.UndefOr[Boolean]
//   val defaultSearchQuery: js.UndefOr[String]
//   val defaultSelectedLabel: js.UndefOr[Double | String]
//   val defaultUpward: js.UndefOr[Boolean]
//   val direction: js.UndefOr[Direction]
//   val disabled: js.UndefOr[Boolean]
//   val error: js.UndefOr[Boolean]
//   val floating: js.UndefOr[Boolean]
//   val fluid: js.UndefOr[Boolean]
//   val header: js.UndefOr[ShorthandS[VdomNode]]
//   val icon: js.UndefOr[ShorthandS[Icon]]
//   val inline: js.UndefOr[Boolean]
//   val item: js.UndefOr[Boolean]
//   val label: js.UndefOr[ShorthandS[Label]]
//   val labeled: js.UndefOr[Boolean]
//   val loading: js.UndefOr[Boolean]
//   val minCharacters: js.UndefOr[Int]
//   val multiple: js.UndefOr[Boolean]
//   val noResultsMessage: js.UndefOr[ShorthandS[VdomNode]]
//   val onBlur: js.UndefOr[Callback]
//   val onBlurE: js.UndefOr[OnBlur]
//   val onClick: js.UndefOr[Callback]
//   val onClickE: js.UndefOr[OnClick]
//   val onChange: js.UndefOr[FormDropdown.OnChange]
//   val onChangeE: js.UndefOr[FormDropdown.OnChangeE]
//   val onClose: js.UndefOr[Callback]
//   val onCloseE: js.UndefOr[OnClose]
//   val onFocus: js.UndefOr[Callback]
//   val onFocusE: js.UndefOr[OnFocus]
//   val onLabelClick: js.UndefOr[Callback]
//   val onLabelClickE: js.UndefOr[OnLabelClick]
//   val onMouseDown: js.UndefOr[Callback]
//   val onMouseDownE: js.UndefOr[OnMouseDown]
//   val onOpen: js.UndefOr[Callback]
//   val onOpenE: js.UndefOr[OnOpen]
//   val onSearchChange: js.UndefOr[OnSearchChange]
//   val onSearchChangeE: js.UndefOr[OnSearchChangeE]
//   val open: js.UndefOr[Boolean]
//   val openOnFocus: js.UndefOr[Boolean]
//   val placeholder: js.UndefOr[String]
//   val pointing: js.UndefOr[Pointing]
//   val renderLabel: js.UndefOr[RenderLabel]
//   val required: js.UndefOr[Boolean]
//   val scrolling: js.UndefOr[Boolean]
//   val search: js.UndefOr[Boolean | SearchFunction]
//   val searchInput: js.UndefOr[ShorthandS[VdomNode]]
//   val searchQuery: js.UndefOr[String]
//   val selectOnBlur: js.UndefOr[Boolean]
//   val selectOnNavigation: js.UndefOr[Boolean]
//   val selectedLabel: js.UndefOr[Double | String]
//   val simple: js.UndefOr[Boolean]
//   val tabIndex: js.UndefOr[String | Int]
//   val text: js.UndefOr[String]
//   val tpe: js.UndefOr[String]
//   val trigger: js.UndefOr[VdomNode]
//   val upward: js.UndefOr[Boolean]
//   val width: js.UndefOr[SemanticWidth]
//   val wrapSelection: js.UndefOr[Boolean]
//   val exclude: Set[AA]
//   val modifiers: Seq[TagMod]
//   val enumerated: Enumerated[AA]
//   val display: Display[AA]
//
//   // set the value in the View from the Select
//   def setter(ddp: FormDropdown.FormDropdownProps): Callback
//
//   // get the value from the View for setting the Select
//   def getter: js.UndefOr[Dropdown.Value]
//
//   val ev: ExternalValue[EV]
// }
//
// object EnumViewSelectBase {
//   type Props[EV[_]] = EnumViewSelectBase[EV]
//
//   private[forms] val component = buildComponent[Any]
//
//   private[forms] def buildComponent[EV[_]] =
//     ScalaComponent
//       .builder[Props[EV]]
//       .stateless
//       .render_P { p =>
//         implicit val display = p.display
//
//         FormSelect(
//           additionLabel = js.undefined,
//           additionPosition = js.undefined,
//           allowAdditions = js.undefined,
//           p.as,
//           p.basic,
//           p.button,
//           p.className,
//           p.clazz,
//           p.clearable,
//           p.closeOnBlur,
//           p.closeOnEscape,
//           p.closeOnChange,
//           p.compact,
//           content = js.undefined,
//           control = js.undefined,
//           p.deburr,
//           p.defaultOpen,
//           p.defaultSearchQuery,
//           p.defaultSelectedLabel,
//           p.defaultUpward,
//           defaultValue = js.undefined,
//           p.direction,
//           p.disabled,
//           p.error,
//           p.floating,
//           p.fluid,
//           p.header,
//           p.icon,
//           p.inline,
//           p.item,
//           p.label,
//           p.labeled,
//           lazyLoad = false,
//           p.loading,
//           p.minCharacters,
//           p.multiple,
//           p.noResultsMessage,
//           onAddItem = js.undefined,
//           p.onBlur,
//           p.onBlurE,
//           onChange = js.undefined,
//           (e: ReactEvent, ddp: FormDropdown.FormDropdownProps) =>
//             p.setter(ddp)
//               >> p.onChangeE
//                 .map(_(e, ddp))
//                 .toOption
//                 .orElse(p.onChange.map(_(ddp)).toOption)
//                 .getOrEmpty,
//           p.onClick,
//           p.onClickE,
//           p.onClose,
//           p.onCloseE,
//           p.onFocus,
//           p.onFocusE,
//           p.onLabelClick,
//           p.onLabelClickE,
//           p.onMouseDown,
//           p.onMouseDownE,
//           p.onOpen,
//           p.onOpenE,
//           p.onSearchChange,
//           p.onSearchChangeE,
//           p.open,
//           p.openOnFocus,
//           options = p.enumerated.all
//             .filter(v => !p.exclude.contains(v))
//             .map(i => DropdownItem(text = i.shortName, value = p.enumerated.tag(i))),
//           p.placeholder,
//           p.pointing,
//           p.renderLabel,
//           p.required,
//           p.scrolling,
//           p.search,
//           p.searchInput,
//           p.searchQuery,
//           p.selectOnBlur,
//           p.selectOnNavigation,
//           p.selectedLabel,
//           p.simple,
//           p.tabIndex,
//           p.text,
//           p.tpe,
//           p.trigger,
//           p.upward,
//           value = p.getter.getOrElse(null),
//           p.width,
//           p.wrapSelection,
//           p.modifiers :+ (^.id := p.id)
//         )
//       }
//       .build
// }
