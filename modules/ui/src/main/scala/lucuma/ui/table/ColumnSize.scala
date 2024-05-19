// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import lucuma.react.SizePx
import lucuma.react.table.ColumnDef

import scalajs.js.JSConverters.*

enum ColumnSize(
  val initial:      SizePx,
  val minSize:      Option[SizePx] = None,
  val maxSize:      Option[SizePx] = None,
  val enableResize: Boolean
):
  case FixedSize(size: SizePx) extends ColumnSize(size, size.some, size.some, false)
  case Resizable(size: SizePx, min: Option[SizePx] = None, max: Option[SizePx] = None)
      extends ColumnSize(size, min, max, true)

object ColumnSize:
  object Resizable:
    def apply(initial: SizePx, min: SizePx, max: SizePx): Resizable =
      Resizable(initial, min.some, max.some)

    def apply(initial: SizePx, min: SizePx): Resizable =
      Resizable(initial, min.some, none)

  extension [T, V, TM, CM](col: ColumnDef.Single[T, V, TM, CM])
    def setColumnSize(size: ColumnSize): ColumnDef.Single[T, V, TM, CM] =
      col
        .setSize(size.initial)
        .setMinSize(size.minSize.orUndefined)
        .setMaxSize(size.maxSize.orUndefined)
        .setEnableResizing(size.enableResize)

  extension [T, TM, CM](col: ColumnDef.Group[T, TM, CM])
    def setColumnSize(size: ColumnSize): ColumnDef.Group[T, TM, CM] =
      col
        .setSize(size.initial)
        .setMinSize(size.minSize.orUndefined)
        .setMaxSize(size.maxSize.orUndefined)
        .setEnableResizing(size.enableResize)
