// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.effect.std.Semaphore
import cats.effect.Concurrent
import cats.effect.Deferred
import cats.effect.Resource.ExitCase
import fs2.Stream
import cats.syntax.all.*

extension [F[_], O](stream: fs2.Stream[F, O])
  def switchMapWithDiscriminator[F2[x] >: F[x], O2, D](
    d: O => D,
    f: O => Stream[F2, O2]
  )(implicit F: Concurrent[F2]): Stream[F2, O2] = {
    val fstream = for {
      haltLatchRef <- F.ref[Map[D, (Deferred[F2, Unit], Deferred[F2, Unit])]](Map.empty)
    } yield {

      def runInner(
        o:        O,
        oldLatch: Option[Deferred[F2, Unit]],
        newLatch: Deferred[F2, Unit],
        halt:     Deferred[F2, Unit],
        cleanup:  F2[Unit]
      ): Stream[F2, O2] =
        Stream.bracketFull[F2, Unit] {
          poll => // guard inner with a latch to prevent parallel inner streams
            poll(oldLatch.fold(F.unit)(_.get))
        } {
          case (_, ExitCase.Errored(_)) => F.unit // if there's an error, don't start next stream
          case _                        => newLatch.complete(()).void
        } >> f(o).interruptWhen(halt.get.attempt)

      def haltedF(o: O): F2[Stream[F2, O2]] =
        for {
          halt  <- F.deferred[Unit]
          latch <- F.deferred[Unit]
          disc   = d(o)
          prev  <- haltLatchRef.getAndUpdate(_.updated(disc, (halt, latch)))
          _     <- prev.get(disc).map(_._1).traverse_(_.complete(())) // interrupt previous one if any
        } yield runInner(
          o,
          prev.get(disc).map(_._2),
          latch,
          halt,
          haltLatchRef.update(_.removed(disc))
        )

      stream.evalMap(haltedF).parJoinUnbounded
    }
    Stream.force(fstream)
  }
