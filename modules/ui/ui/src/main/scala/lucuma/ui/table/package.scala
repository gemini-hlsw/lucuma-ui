// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import lucuma.react.table.Updater

def stateInViewHandler[F[_], S](mod: (S => S) => F[Unit]): Updater[S] => F[Unit] =
  (u: Updater[S]) =>
    u match
      case Updater.Set(v) => mod(_ => v)
      case Updater.Mod(f) => mod(f)
