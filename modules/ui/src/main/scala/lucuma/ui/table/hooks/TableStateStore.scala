// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table.hooks

import cats.Endo
import lucuma.react.table.TableState

trait TableStateStore[F[_]]:
  def load(): F[Endo[TableState]]
  def save(state: TableState): F[Unit]
