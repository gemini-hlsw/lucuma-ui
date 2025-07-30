// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.primereact.MultiSelect
import lucuma.react.primereact.PrimeStyles
import lucuma.react.primereact.SelectItem
import lucuma.react.primereact.SelectItemGroup
import lucuma.react.primereact.SelectItemGroups
import lucuma.react.primereact.TooltipOptions

import scala.scalajs.js

import scalajs.js.JSConverters.*

/**
 * A multi-select for Enumerated values with grouping.
 *
 * @param groupFunctions
 *   A non-empty list of (String, A => Boolean) tuples, where the String is the group label and the
 *   function is used to filter the values for that group.
 * @param error
 *   If defined, the multi-select will have an error border, and error will be shown as a tooltip.
 *   The error tooltip will override the tooltip parameter.
 */
final case class EnumGroupedMultiSelectView[V[_], A](
  id:                 NonEmptyString,
  value:              V[List[A]],
  groupFunctions:     NonEmptyList[(String, A => Boolean)],
  exclude:            Set[A] = Set.empty[A],
  disabledItems:      Set[A] = Set.empty[A],
  clazz:              js.UndefOr[Css] = js.undefined,
  panelClass:         js.UndefOr[Css] = js.undefined,
  filter:             js.UndefOr[Boolean] = js.undefined,
  maxSelectedLabels:  js.UndefOr[Int] = js.undefined,
  selectedItemsLabel: js.UndefOr[String] = js.undefined, // if exceeds maxSelectedLabels
  showClear:          js.UndefOr[Boolean] = js.undefined,
  showSelectAll:      js.UndefOr[Boolean] = js.undefined,
  displayStyle:       js.UndefOr[MultiSelect.Display] = js.undefined,
  disabled:           js.UndefOr[Boolean] = js.undefined,
  placeholder:        js.UndefOr[String] = js.undefined,
  tooltip:            js.UndefOr[String] = js.undefined,
  tooltipOptions:     js.UndefOr[TooltipOptions] = js.undefined,
  error:              js.UndefOr[String] = js.undefined,
  size:               js.UndefOr[PlSize] = js.undefined,
  itemTemplate:       js.UndefOr[SelectItem[A] => VdomNode] = js.undefined,
  onChangeE:          js.UndefOr[(List[A], ReactEvent) => Callback] =
    js.undefined, // called after the view is set
  modifiers:          Seq[TagMod] = Seq.empty
)(using
  val enumerated:     Enumerated[A],
  val display:        Display[A],
  val vl:             ViewLike[V]
) extends ReactFnProps(EnumGroupedMultiSelectView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)
  def apply(mods:             TagMod*)     = addModifiers(mods)

object EnumGroupedMultiSelectView:
  private type AnyF[_] = Any

  private def buildComponent[V[_], A] = ScalaFnComponent[EnumGroupedMultiSelectView[V, A]]: props =>
    import props.given

    val groups = props.groupFunctions.toList.map { case (label, f) =>
      SelectItemGroup(
        label = label,
        options = props.enumerated.all
          .filterNot(props.exclude.contains(_))
          .filter(f)
          .map(e =>
            SelectItem(
              value = e,
              label = props.display.shortName(e),
              disabled = props.disabledItems.contains(e)
            )
          )
      )
    }

    val sizeCls  = props.size.toOption.map(_.cls).orEmpty
    val errorCls = props.error.toOption.map(_ => PrimeStyles.Invalid).orEmpty
    val tooltip  = props.error.toOption.orElse(props.tooltip.toOption).orUndefined

    MultiSelect(
      id = props.id.value,
      value = props.value.get.orEmpty,
      options = SelectItemGroups(groups = groups),
      clazz = props.clazz.toOption.orEmpty |+| sizeCls |+| errorCls,
      panelClass = props.panelClass.toOption.orEmpty |+| sizeCls,
      filter = props.filter,
      maxSelectedLabels = props.maxSelectedLabels,
      selectedItemsLabel = props.selectedItemsLabel,
      showSelectAll = props.showSelectAll,
      showClear = props.showClear,
      display = props.displayStyle,
      disabled = props.disabled,
      placeholder = props.placeholder,
      tooltip = tooltip,
      tooltipOptions = props.tooltipOptions,
      itemTemplate = props.itemTemplate,
      onChange = props.value.set,
      onChangeE = props.onChangeE,
      modifiers = props.modifiers
    )

  private val component = buildComponent[AnyF, Any]
