// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.SizePx
import lucuma.react.common.*
import lucuma.react.common.style.Css
import lucuma.react.table.*
import lucuma.typed.tanstackVirtualCore as rawVirtual
import org.scalajs.dom.HTMLElement

import scalajs.js

trait PrimeTableProps[T, TM] extends HTMLTableProps[T, TM]:
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

case class PrimeTable[T, TM](
  table:         Table[T, TM],
  hoverableRows: Boolean = true,
  striped:       Boolean = false,
  celled:        Boolean = false,
  compact:       js.UndefOr[Compact] = js.undefined,
  tableMod:      TagMod = TagMod.empty,
  headerMod:     TagMod = TagMod.empty,
  headerRowMod:  HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  headerCellMod: Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  bodyMod:       TagMod = TagMod.empty,
  rowMod:        Row[T, TM] => TagMod = (_: Row[T, TM]) => TagMod.empty,
  cellMod:       Cell[T, Any, TM, Any] => TagMod = (_: Cell[T, Any, TM, Any]) => TagMod.empty,
  footerMod:     TagMod = TagMod.empty,
  footerRowMod:  HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  footerCellMod: Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  emptyMessage:  VdomNode = EmptyVdom
) extends ReactFnProps(PrimeTable.component)
    with PrimeTableProps[T, TM]

case class PrimeVirtualizedTable[T, TM](
  table:            Table[T, TM],
  estimateSize:     Int => SizePx,
  // Table options
  hoverableRows:    Boolean = true,
  striped:          Boolean = false,
  celled:           Boolean = false,
  compact:          js.UndefOr[Compact] = js.undefined,
  containerMod:     TagMod = TagMod.empty,
  containerRef:     js.UndefOr[Ref.ToVdom[HTMLElement]] = js.undefined,
  tableMod:         TagMod = TagMod.empty,
  headerMod:        TagMod = TagMod.empty,
  headerRowMod:     HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  headerCellMod:    Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  bodyMod:          TagMod = TagMod.empty,
  rowMod:           Row[T, TM] => TagMod = (_: Row[T, TM]) => TagMod.empty,
  cellMod:          Cell[T, Any, TM, Any] => TagMod = (_: Cell[T, Any, TM, Any]) => TagMod.empty,
  footerMod:        TagMod = TagMod.empty,
  footerRowMod:     HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  footerCellMod:    Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  emptyMessage:     VdomNode = EmptyVdom,
  // Virtual options
  overscan:         js.UndefOr[Int] = js.undefined,
  getItemKey:       js.UndefOr[Int => rawVirtual.mod.Key] = js.undefined,
  onChange:         js.UndefOr[HTMLTableVirtualizer => Callback] = js.undefined,
  virtualizerRef:   js.UndefOr[NonEmptyRef.Simple[Option[HTMLTableVirtualizer]]] = js.undefined,
  debugVirtualizer: js.UndefOr[Boolean] = js.undefined
) extends ReactFnProps(PrimeVirtualizedTable.component)
    with HTMLVirtualizedTableProps[T, TM]
    with PrimeTableProps[T, TM]

case class PrimeAutoHeightVirtualizedTable[T, TM](
  table:             Table[T, TM],
  estimateSize:      Int => SizePx,
  // Table options
  hoverableRows:     Boolean = true,
  striped:           Boolean = false,
  celled:            Boolean = false,
  compact:           js.UndefOr[Compact] = js.undefined,
  containerMod:      TagMod = TagMod.empty,
  containerRef:      js.UndefOr[Ref.ToVdom[HTMLElement]] = js.undefined,
  innerContainerMod: TagMod = TagMod.empty,
  tableMod:          TagMod = TagMod.empty,
  headerMod:         TagMod = TagMod.empty,
  headerRowMod:      HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  headerCellMod:     Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  bodyMod:           TagMod = TagMod.empty,
  rowMod:            Row[T, TM] => TagMod = (_: Row[T, TM]) => TagMod.empty,
  cellMod:           Cell[T, Any, TM, Any] => TagMod = (_: Cell[T, Any, TM, Any]) => TagMod.empty,
  footerMod:         TagMod = TagMod.empty,
  footerRowMod:      HeaderGroup[T, TM] => TagMod = (_: HeaderGroup[T, TM]) => TagMod.empty,
  footerCellMod:     Header[T, Any, TM, Any] => TagMod = (_: Header[T, Any, TM, Any]) => TagMod.empty,
  emptyMessage:      VdomNode = EmptyVdom,
  // Virtual options
  overscan:          js.UndefOr[Int] = js.undefined,
  getItemKey:        js.UndefOr[Int => rawVirtual.mod.Key] = js.undefined,
  onChange:          js.UndefOr[HTMLTableVirtualizer => Callback] = js.undefined,
  virtualizerRef:    js.UndefOr[NonEmptyRef.Simple[Option[HTMLTableVirtualizer]]] = js.undefined,
  debugVirtualizer:  js.UndefOr[Boolean] = js.undefined
) extends ReactFnProps(PrimeAutoHeightVirtualizedTable.component)
    with HTMLAutoHeightVirtualizedTableProps[T, TM]
    with PrimeTableProps[T, TM]

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
    override protected val TfootTdClass: Css = Css.Empty
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
  private val component = HTMLTableRenderer.componentBuilder[Any, Any, PrimeTable](baseHTMLRenderer)

object PrimeVirtualizedTable:
  private val component =
    HTMLTableRenderer.componentBuilderVirtualized[Any, Any, PrimeVirtualizedTable](baseHTMLRenderer)

object PrimeAutoHeightVirtualizedTable:
  private val component =
    HTMLTableRenderer
      .componentBuilderAutoHeightVirtualized[Any, Any, PrimeAutoHeightVirtualizedTable](
        baseHTMLRenderer
      )
