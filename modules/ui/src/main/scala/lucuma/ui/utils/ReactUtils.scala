// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.effect.Effect
import crystal.react.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.ReactMouseEvent

trait ReactUtils {
  def linkOverride[F[_]: Effect](f: => F[Unit]): ReactMouseEvent => Callback =
    e => linkOverride[F, Unit](f)(Effect[F])(e, ())

  def linkOverride[F[_]: Effect, A](f: => F[Unit]): (ReactMouseEvent, A) => Callback =
    (e: ReactMouseEvent, _: A) => {
      (e.preventDefaultCB *> f.runAsyncCB)
        .unless_(e.ctrlKey || e.metaKey)
    }
}
