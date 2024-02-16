// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.effect.Concurrent
import cats.effect.std.MapRef
import cats.syntax.all.*
import cats.effect.Fiber
import cats.effect.implicits.*
import cats.Applicative

final class KeyedSingleEffect[F[_]: Concurrent, K] private (
  fibers: MapRef[F, K, Option[Fiber[F, Throwable, Unit]]]
) {
  def submit(key: K, task: F[Unit]): F[Unit] =
    def execute(update: Option[Fiber[F, Throwable, Unit]] => F[Boolean]): F[Unit] =
      (task >> update(none).void).start.flatMap(fiber => update(fiber.some)).flatMap {
        case false => submit(key, task)
        case true  => Applicative[F].unit
      }

    fibers(key).access.flatMap {
      case (Some(fiber), update) => fiber.cancel >> execute(update)
      case (None, update)        => execute(update)
    }

}

object KeyedSingleEffect {
  def apply[F[_]: Concurrent, K]: F[KeyedSingleEffect[F, K]] =
    MapRef.ofSingleImmutableMap[F, K, Fiber[F, Throwable, Unit]](Map.empty).map { fibers =>
      new KeyedSingleEffect(fibers)
    }
}
