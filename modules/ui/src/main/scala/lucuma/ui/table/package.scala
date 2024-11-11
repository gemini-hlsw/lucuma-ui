// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import crystal.react.View
import lucuma.react.table.Updater

def stateInViewHandler[S](view: View[S]) = (u: Updater[S]) =>
  u match
    case Updater.Set(v) => view.set(v)
    case Updater.Mod(f) => view.mod(f)
