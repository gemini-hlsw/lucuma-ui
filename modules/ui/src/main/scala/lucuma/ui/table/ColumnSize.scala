// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import lucuma.react.SizePx
import lucuma.react.table.ColumnDef

import scalajs.js.JSConverters.*

enum ColumnSize:
  case FixedSize(size: SizePx) extends ColumnSize
  case Resizeable(initial: SizePx, min: Option[SizePx] = None, max: Option[SizePx] = None)
      extends ColumnSize

object ColumnSize:
  extension [T, V](col: ColumnDef.Single[T, V])
    def withSize(size: ColumnSize): ColumnDef.Single[T, V] = size match
      case FixedSize(size)               =>
        col.copy(size = size, enableResizing = false)
      case Resizeable(initial, min, max) =>
        col.copy(
          size = initial,
          minSize = min.orUndefined,
          maxSize = max.orUndefined,
          enableResizing = true
        )

  extension [T, V](col: ColumnDef.Group[T])
    def withSize(size: ColumnSize): ColumnDef.Group[T] = size match
      case FixedSize(size)               =>
        col.copy(size = size, enableResizing = false)
      case Resizeable(initial, min, max) =>
        col.copy(
          size = initial,
          minSize = min.orUndefined,
          maxSize = max.orUndefined,
          enableResizing = true
        )
