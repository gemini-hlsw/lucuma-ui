// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.effect.Concurrent
import cats.effect.std.MapRef
import cats.syntax.all.*
import cats.effect.Fiber
import cats.Applicative
import cats.effect.std.Supervisor
import cats.effect.Resource

final class KeyedSingleEffect[F[_]: Concurrent, K] private (
  supervisor: Supervisor[F],
  fibers:     MapRef[F, K, Option[Fiber[F, Throwable, Unit]]]
) {
  def submit(key: K, task: F[Unit]): F[Unit] =
    def execute(update: Option[Fiber[F, Throwable, Unit]] => F[Boolean]): F[Unit] =
      supervisor.supervise(task >> update(none).void).flatMap(fiber => update(fiber.some)).flatMap {
        case false => submit(key, task)
        case true  => Applicative[F].unit
      }

    fibers(key).access.flatMap {
      case (Some(fiber), update) => fiber.cancel >> execute(update)
      case (None, update)        => execute(update)
    }

}

object KeyedSingleEffect {
  def apply[F[_]: Concurrent, K]: Resource[F, KeyedSingleEffect[F, K]] =
    Supervisor[F].evalMap { supervisor =>
      MapRef.ofSingleImmutableMap[F, K, Fiber[F, Throwable, Unit]](Map.empty).map { fibers =>
        new KeyedSingleEffect(supervisor, fibers)
      }
    }
}
