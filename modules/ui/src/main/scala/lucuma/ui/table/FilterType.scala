// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import lucuma.react.table.ColumnDef
import lucuma.react.table.Column
import lucuma.react.table.BuiltInFilter
import japgolly.scalajs.react.vdom.html_<^.*
import cats.syntax.option.*

enum FilterType:
  case Text, Select

object FilterType:
  def render[T, TM, CM <: WithFilterType, TF](col: Column[T, Any, TM, CM, TF, Any, Any]): VdomNode =
    col.columnDef.meta.flatMap(_.filterType) match
      case Some(FilterType.Text)   =>
        ColumnFilter.Text(col.asInstanceOf[Column[T, Any, TM, CM, TF, String, Any]])
      case Some(FilterType.Select) =>
        ColumnFilter.Select(col.asInstanceOf[Column[T, Any, TM, CM, TF, String, Any]])
      case _                       => EmptyVdom

// This allows composition into other column metas, if need be
trait WithFilterType:
  def filterType: Option[FilterType]

object WithFilterType:
  def apply(ft: FilterType): WithFilterType =
    new WithFilterType:
      val filterType = ft.some

extension [T, A, TM, TF](col: ColumnDef.Applied[T, TM, Nothing, TF])
  def WithColumnFilters: ColumnDef.Applied[T, TM, WithFilterType, TF] =
    col.WithColumnMeta[WithFilterType]

extension [T, A, TM, TF](col: ColumnDef.Single[T, A, TM, WithFilterType, TF, Nothing, Nothing])
  def withFilterType(
    ft: FilterType
  ): ColumnDef.Single[T, A, TM, WithFilterType, TF, String, Nothing] =
    col
      .withMeta(WithFilterType(ft))
      .withFilterFn(BuiltInFilter.IncludesString)
