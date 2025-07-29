// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import cats.Endo
import lucuma.react.table.TableState

trait TableStateStore[F[_], TF]:
  def load(): F[Endo[TableState[TF]]]
  def save(state: TableState[TF]): F[Unit]
