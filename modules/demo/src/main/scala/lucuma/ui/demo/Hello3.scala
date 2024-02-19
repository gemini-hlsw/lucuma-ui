// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.effect.syntax.all.*
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.effect.Fiber
import cats.Applicative
import fs2.Pull
import fs2.Stream
import fs2.Pipe
import fs2.concurrent.Channel

def keyedSwitchEvalMap[F[_]: Concurrent, I, O, K](
  key: I => K,
  f:   I => F[O]
): Pipe[F, I, O] = {
  def go(
    stream: Stream[F, Either[Option[I], (K, O)]], // input? | key -> output
    fibers: Map[K, Fiber[F, Throwable, Unit]],
    ended:  Boolean,
    emit:   ((K, O)) => F[Unit]
  ): Pull[F, O, Unit] =
    stream.pull.uncons1.flatMap:
      // Element arrives on input stream. Run the effect and store the fiber. Cancel previous effect for the same key, if any.
      case Some(Left(Some(i)), tail) =>
        val k: K = key(i)

        def run(preF: F[Unit]): Pull[F, O, Unit] =
          val finalF = preF >> f(i) >>= (o => emit(k -> o))
          Pull
            .eval(finalF.start)
            .flatMap: fiber =>
              go(tail, fibers.updated(k, fiber), ended, emit)

        fibers.get(k) match
          case Some(fiber) => run(fiber.cancel)
          case None        => run(Applicative[F].unit)
      // An effect completed! Output it. If input stream ended and there no more running fibers, we're done.
      case Some(Right((k, o)), tail) =>
        Pull.output1(o) >> {
          val newFibers = fibers - k
          if (ended && newFibers.isEmpty) Pull.done
          else go(tail, newFibers, ended, emit)
        }
      // Input stream ended! Just take note and wait for all fibers to complete, or end if there are no fibers.
      case Some(Left(None), tail)    =>
        if (fibers.isEmpty) Pull.done
        else go(tail, fibers, true, emit)
      // Will never happen. Right stream will not end on its own.
      case None                      => Pull.done

  in =>
    Stream
      .eval(Channel.unbounded[F, (K, O)])
      .flatMap: out =>
        go(in.noneTerminate.either(out.stream), Map.empty, false, out.send(_).void).stream
          .onFinalize(out.close.void)
}
