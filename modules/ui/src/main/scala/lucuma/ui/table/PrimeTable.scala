// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.Monoid
import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.SizePx
import lucuma.react.table.*
import org.scalajs.dom.HTMLDivElement
import react.common.*
import react.common.style.Css
import reactST.{tanstackTableCore => raw}
import reactST.{tanstackVirtualCore => rawVirtual}

import scalajs.js

trait PrimeTableProps[T] extends HTMLTableProps[T]:
  def hoverableRows: Boolean
  def striped: Boolean
  def celled: Boolean
  def compact: js.UndefOr[Compact]

  override val extraTableClasses: Css =
    Css("p-datatable-hoverable-rows").when_(hoverableRows) |+|
      Css("pl-striped-table").when_(striped) |+|
      Css("pl-celled-table").when_(celled) |+|
      compact.toOption
        .map(_ match
          case Compact.Very => Css("pl-very-compact")
          case Compact      => Css("pl-compact")
        )
        .orEmpty

case class PrimeTable[T](
  table:              Table[T],
  hoverableRows:      Boolean = true,
  striped:            Boolean = false,
  celled:             Boolean = false,
  compact:            js.UndefOr[Compact] = js.undefined,
  tableMod:           TagMod = TagMod.empty,
  headerMod:          TagMod = TagMod.empty,
  headerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  headerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  bodyMod:            TagMod = TagMod.empty,
  rowMod:             raw.mod.Row[T] => TagMod = (_: raw.mod.Row[T]) => TagMod.empty,
  cellMod:            raw.mod.Cell[T, Any] => TagMod = (_: raw.mod.Cell[T, Any]) => TagMod.empty,
  footerMod:          TagMod = TagMod.empty,
  footerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  footerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  emptyMessage:       VdomNode = EmptyVdom,
  renderSubComponent: raw.mod.Row[T] => Option[VdomNode] = (_: raw.mod.Row[T]) => none
) extends ReactFnProps(PrimeTable.component)
    with PrimeTableProps[T]

case class PrimeVirtualizedTable[T](
  table:              Table[T],
  estimateSize:       Int => SizePx,
  // Table options
  hoverableRows:      Boolean = true,
  striped:            Boolean = false,
  celled:             Boolean = false,
  compact:            js.UndefOr[Compact] = js.undefined,
  containerMod:       TagMod = TagMod.empty,
  containerRef:       js.UndefOr[Ref.Simple[HTMLDivElement]] = js.undefined,
  tableMod:           TagMod = TagMod.empty,
  headerMod:          TagMod = TagMod.empty,
  headerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  headerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  bodyMod:            TagMod = TagMod.empty,
  rowMod:             raw.mod.Row[T] => TagMod = (_: raw.mod.Row[T]) => TagMod.empty,
  cellMod:            raw.mod.Cell[T, Any] => TagMod = (_: raw.mod.Cell[T, Any]) => TagMod.empty,
  footerMod:          TagMod = TagMod.empty,
  footerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  footerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  emptyMessage:       VdomNode = EmptyVdom,
  renderSubComponent: raw.mod.Row[T] => Option[VdomNode] = (_: raw.mod.Row[T]) => none,
  // Virtual options
  overscan:           js.UndefOr[Int] = js.undefined,
  getItemKey:         js.UndefOr[Int => rawVirtual.mod.Key] = js.undefined,
  onChange:           js.UndefOr[HTMLTableVirtualizer => Callback] = js.undefined,
  virtualizerRef:     js.UndefOr[NonEmptyRef.Simple[Option[HTMLTableVirtualizer]]] = js.undefined,
  debugVirtualizer:   js.UndefOr[Boolean] = js.undefined
) extends ReactFnProps(PrimeVirtualizedTable.component)
    with HTMLVirtualizedTableProps[T]
    with PrimeTableProps[T]

case class PrimeAutoHeightVirtualizedTable[T](
  table:              Table[T],
  estimateSize:       Int => SizePx,
  // Table options
  hoverableRows:      Boolean = true,
  striped:            Boolean = false,
  celled:             Boolean = false,
  compact:            js.UndefOr[Compact] = js.undefined,
  containerMod:       TagMod = TagMod.empty,
  containerRef:       js.UndefOr[Ref.Simple[HTMLDivElement]] = js.undefined,
  innerContainerMod:  TagMod = TagMod.empty,
  tableMod:           TagMod = TagMod.empty,
  headerMod:          TagMod = TagMod.empty,
  headerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  headerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  bodyMod:            TagMod = TagMod.empty,
  rowMod:             raw.mod.Row[T] => TagMod = (_: raw.mod.Row[T]) => TagMod.empty,
  cellMod:            raw.mod.Cell[T, Any] => TagMod = (_: raw.mod.Cell[T, Any]) => TagMod.empty,
  footerMod:          TagMod = TagMod.empty,
  footerRowMod:       raw.mod.CoreHeaderGroup[T] => TagMod = (_: raw.mod.CoreHeaderGroup[T]) =>
    TagMod.empty,
  footerCellMod:      raw.mod.Header[T, Any] => TagMod = (_: raw.mod.Header[T, Any]) => TagMod.empty,
  emptyMessage:       VdomNode = EmptyVdom,
  renderSubComponent: raw.mod.Row[T] => Option[VdomNode] = (_: raw.mod.Row[T]) => none,
  // Virtual options
  overscan:           js.UndefOr[Int] = js.undefined,
  getItemKey:         js.UndefOr[Int => rawVirtual.mod.Key] = js.undefined,
  onChange:           js.UndefOr[HTMLTableVirtualizer => Callback] = js.undefined,
  virtualizerRef:     js.UndefOr[NonEmptyRef.Simple[Option[HTMLTableVirtualizer]]] = js.undefined,
  debugVirtualizer:   js.UndefOr[Boolean] = js.undefined
) extends ReactFnProps(PrimeAutoHeightVirtualizedTable.component)
    with HTMLAutoHeightVirtualizedTableProps[T]
    with PrimeTableProps[T]

private val baseHTMLRenderer: HTMLTableRenderer[Any] =
  new HTMLTableRenderer[Any]:
    // Responsive-scroll means "unstackable". We are forcing this in all tables.
    // If we ever want a table that stacks on small devices, we can turn this into a parameter.
    override protected val TableClass: Css   = Css(
      "pl-react-table p-component p-datatable p-datatable-table p-datatable-responsive-scroll"
    )
    override protected val TheadClass: Css   = Css("p-datatable-thead")
    override protected val TheadTrClass: Css = Css.Empty
    override protected val TheadThClass: Css = Css("p-column-title")
    override protected val TbodyClass: Css   = Css("p-datatable-tbody")
    override protected val TbodyTrClass: Css = Css.Empty
    override protected val TbodyTdClass: Css = Css.Empty
    override protected val TfootClass: Css   = Css("p-datatable-tfoot")
    override protected val TfootTrClass: Css = Css.Empty
    override protected val TfootThClass: Css = Css.Empty
    override protected val EmptyMessage: Css = Css("p-datatable-emptymessage")

    override protected val ResizerClass: Css         = Css("pl-resizer")
    override protected val IsResizingTHeadClass: Css = Css("pl-head-resizing")
    override protected val IsResizingColClass: Css   = Css("pl-col-resizing")
    override protected val ResizerContent: VdomNode  = "⋮"

    override protected val SortableColClass: Css       = Css("pl-sortable-col")
    override protected val SortableIndicator: VdomNode = TableIcons.Sort.withFixedWidth()
    override protected val SortAscIndicator: VdomNode  = TableIcons.SortUp.withFixedWidth()
    override protected val SortDescIndicator: VdomNode = TableIcons.SortDown.withFixedWidth()

object PrimeTable:
  private val component = HTMLTableRenderer.componentBuilder[Any, PrimeTable](baseHTMLRenderer)

object PrimeVirtualizedTable:
  private val component =
    HTMLTableRenderer.componentBuilderVirtualized[Any, PrimeVirtualizedTable](baseHTMLRenderer)

object PrimeAutoHeightVirtualizedTable:
  private val component =
    HTMLTableRenderer.componentBuilderAutoHeightVirtualized[Any, PrimeAutoHeightVirtualizedTable](
      baseHTMLRenderer
    )
