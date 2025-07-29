// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.syntax.all.*
import lucuma.react.SizePx
import lucuma.react.table.ColumnDef

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

  extension [T, V, TM, CM, TF, CF, FM](col: ColumnDef.Single[T, V, TM, CM, TF, CF, FM])
    def withColumnSize(size: ColumnSize): ColumnDef.Single[T, V, TM, CM, TF, CF, FM] =
      col
        .withSize(size.initial)
        .withEnableResizing(size.enableResize)
        .setMinSize(size.minSize)
        .setMaxSize(size.maxSize)

  extension [T, TM, CM, TF, CF, FM](col: ColumnDef.Group[T, TM, CM, TF, CF, FM])
    def withColumnSize(size: ColumnSize): ColumnDef.Group[T, TM, CM, TF, CF, FM] =
      col
        .withSize(size.initial)
        .withEnableResizing(size.enableResize)
        .setMinSize(size.minSize)
        .setMaxSize(size.maxSize)
