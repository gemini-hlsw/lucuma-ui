// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import lucuma.react.SizePx
import lucuma.react.table.ColumnDef

import scalajs.js.JSConverters.*

enum ColumnSize:
  case FixedSize(size: SizePx) extends ColumnSize
  case Resizable(initial: SizePx, min: Option[SizePx] = None, max: Option[SizePx] = None)
      extends ColumnSize

object ColumnSize:
  object Resizable:
    def apply(initial: SizePx, min: SizePx, max: SizePx): Resizable =
      Resizable(initial, min.some, max.some)

  extension [T, V](col: ColumnDef.Single[T, V])
    def setColumnSize(size: ColumnSize): ColumnDef.Single[T, V] = size match
      case FixedSize(size)              =>
        col.setSize(size).setEnableResizing(false)
      case Resizable(initial, min, max) =>
        col
          .setSize(initial)
          .setMinSize(min.orUndefined)
          .setMaxSize(max.orUndefined)
          .setEnableResizing(true)

  extension [T, V](col: ColumnDef.Group[T])
    def setColumnSize(size: ColumnSize): ColumnDef.Group[T] = size match
      case FixedSize(size)              =>
        col.setSize(size).setEnableResizing(false)
      case Resizable(initial, min, max) =>
        col
          .setSize(initial)
          .setMinSize(min.orUndefined)
          .setMaxSize(max.orUndefined)
          .setEnableResizing(true)
