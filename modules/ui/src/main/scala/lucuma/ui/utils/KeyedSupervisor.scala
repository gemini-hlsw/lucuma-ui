// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.effect.Concurrent
import cats.effect.Resource
import cats.effect.std.MapRef
import cats.effect.std.Supervisor
import cats.syntax.all.*
import fs2.Stream
import fs2.concurrent.Channel

final class KeyedSupervisor[F[_]: Concurrent, K] private (
  supervisor: Supervisor[F],
  channels:   MapRef[F, K, Option[Channel[F, F[Unit]]]]
) {

  def supervise(key: K, task: F[Unit]): F[Unit] =
    channels(key).access.flatMap {
      case (Some(ch), _)  => ch.send(task).void
      case (None, update) =>
        Channel.unbounded[F, F[Unit]].flatMap { ch =>
          update(Some(ch)).flatMap {
            case true  => // start stream and submit task
              val stream = ch.stream.switchMap(task => Stream.exec(task))
              supervisor.supervise(stream.compile.drain) *> ch.send(task).void
            case false => // try again
              supervise(key, task)
          }
        }
    }

}

object KeyedSupervisor {
  def apply[F[_]: Concurrent, K]: Resource[F, KeyedSupervisor[F, K]] =
    Supervisor[F].evalMap { supervisor =>
      MapRef.ofSingleImmutableMap[F, K, Channel[F, F[Unit]]](Map.empty).map { tasks =>
        new KeyedSupervisor(supervisor, tasks)
      }
    }
}
