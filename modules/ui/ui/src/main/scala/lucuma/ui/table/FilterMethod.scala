// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.Eq
import cats.syntax.all.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css
import lucuma.react.table.Column
import lucuma.react.table.ColumnDef
import lucuma.react.table.ColumnId
import lucuma.react.table.FilterFn
import lucuma.ui.react.given

enum FilterMethod[A, F](compare: (A, F) => Boolean):
  case Text[A](
    convert:         A => String,
    val delayMillis: Int = 250,
    val placeholder: String = "<Filter>",
    val clazz:       Css = Css.Empty
  )                      extends FilterMethod[A, String]((a, b) => convert(a).toLowerCase.contains(b.toLowerCase))
  case Select[A](
    val display:     A => String,
    val placeholder: String = "<Filter>",
    val showCount:   Boolean = true,
    val clazz:       Css = Css.Empty
  )(using val eq: Eq[A]) extends FilterMethod[A, A](_ === _)

  protected[table] def filterFn[T, TM, TF]: FilterFn.Type[T, TM, WithFilterMethod, TF, F, Nothing] =
    (row, columnId, filterValue, _) => compare(row.getValue(columnId), filterValue)

  def stringCompare(a: A, f: String): Boolean =
    this match
      case Text(_, _, _, _)         => compare(a, f)
      case Select(display, _, _, _) => display(a).toLowerCase.contains(f.toLowerCase)

  protected[table] def render[T, TM, CM <: WithFilterMethod, TF](
    col: Column[T, Any, TM, CM, TF, Any, Any]
  ): VdomNode =
    this match
      case Text(_, delayMillis, placeholder, clazz) =>
        ColumnFilter.Text(
          col.asInstanceOf[Column[T, A, TM, CM, TF, String, Any]],
          delayMillis,
          placeholder,
          clazz
        )
      case other                                    =>
        val s: Select[A]                                   = other.asInstanceOf[Select[A]] // This avoids unchecked warnings on A
        val Select(display, placeholder, showCount, clazz) = s
        import s.eq

        ColumnFilter.Select(
          col.asInstanceOf[Column[T, A, TM, CM, TF, A, Any]],
          display,
          placeholder,
          showCount,
          clazz
        )

object FilterMethod:
  def StringText(
    delayMillis: Int = 250,
    placeholder: String = "<Filter>",
    clazz:       Css = Css.Empty
  ): Text[String] =
    Text(identity(_), delayMillis, placeholder, clazz)

  def StringSelect(
    placeholder: String = "<Filter>",
    showCount:   Boolean = true,
    clazz:       Css = Css.Empty
  ): Select[String] =
    Select(identity(_), placeholder, showCount, clazz)

  def globalFilterFn[T, TM, CM <: WithFilterMethod](
    colDefs: List[ColumnDef[T, ?, TM, CM, String, ?, ?]]
  ): FilterFn.Type[T, TM, CM, String, String, Nothing] =
    (row, _, value, _) =>
      val columnFilterFns: List[(ColumnId, (?, String) => Boolean)] =
        colDefs
          .map(c => c.meta.flatMap(_.filterMethod.map(fm => c.id -> fm.stringCompare)))
          .flattenOption
      columnFilterFns.exists((colId, fn) => fn(row.getValue(colId), value))

  def render[T, TM, CM <: WithFilterMethod, TF](
    col: Column[T, Any, TM, CM, TF, Any, Any]
  ): VdomNode =
    col.columnDef.meta.flatMap(_.filterMethod).foldMap(_.render(col))

// This allows composition into other column metas, if need be
trait WithFilterMethod:
  def filterMethod: Option[FilterMethod[?, ?]]

object WithFilterMethod:
  def apply[A, F](fm: FilterMethod[A, F]): WithFilterMethod =
    new WithFilterMethod:
      val filterMethod = fm.some

extension [T, A, TM, TF](col: ColumnDef.Applied[T, TM, Nothing, TF])
  def WithColumnFilters: ColumnDef.Applied[T, TM, WithFilterMethod, TF] =
    col.WithColumnMeta[WithFilterMethod]

extension [T, A, TM, TF](col: ColumnDef.Single[T, A, TM, WithFilterMethod, TF, Nothing, Nothing])
  def withFilterMethod[CF](
    filterMethod: FilterMethod[A, CF]
  ): ColumnDef.Single[T, A, TM, WithFilterMethod, TF, CF, Nothing] =
    col
      .withMeta(WithFilterMethod(filterMethod))
      .withFilterFn(filterMethod.filterFn)
