// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.effect.Resource
import cats.effect.Temporal
import cats.syntax.all.*
import crystal.*

import scala.concurrent.duration.*

trait effect {
  extension [F[_], A](self: F[A])
    /**
     * Materializes exceptions into values as `Pot.Error`, and return values as `Pot.Ready`.
     */
    def attemptPot(using MonadThrow[F]): F[Pot[A]] = self.attempt.map(_.toTry.some.toPot)

    /**
     * Given an effect producing an A and a signal stream, runs the effect and then re-runs it
     * whenver a signal is received, producing a Stream[A].
     */
    def reRunOnSignal(
      signal:   fs2.Stream[F, Unit],
      debounce: Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): fs2.Stream[F, A] = {
      val debouncedSignal = debounce.fold(signal)(signal.debounce)
      fs2.Stream.eval(self) ++ debouncedSignal.evalMap(_ => self)
    }

    /**
     * Given an effect producing an A and a bunch of signal streams, runs the effect and then
     * re-runs it whenver a signal is received, producing a Stream[A].
     */
    def reRunOnSignals(
      signals:  NonEmptyList[fs2.Stream[F, Unit]],
      debounce: Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): fs2.Stream[F, A] =
      reRunOnSignal(signals.reduceLeft(_ merge _), debounce)

    /**
     * Given an effect producing an A and a bunch of `Resource`s providing signal streams, runs the
     * effect and then re-runs it whenver a signal is received, producing a Stream[A].
     */
    def reRunOnResourceSignals(
      subscriptions: NonEmptyList[Resource[F, fs2.Stream[F, ?]]],
      debounce:      Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): Resource[F, fs2.Stream[F, A]] =
      subscriptions.sequence
        .map(ss => reRunOnSignals(ss.map(_.void), debounce))

    /**
     * Given an effect producing an A and a bunch of `Resource`s providing signal streams, runs the
     * effect and then re-runs it whenver a signal is received, producing a Stream[A].
     */
    def reRunOnResourceSignals(
      head: Resource[F, fs2.Stream[F, ?]],
      tail: Resource[F, fs2.Stream[F, ?]]*
    )(using Temporal[F]): Resource[F, fs2.Stream[F, A]] =
      reRunOnResourceSignals(
        NonEmptyList.of(head, tail*),
        2.seconds.some // For some reason, compiler can't resolve default parameter value here
      )

    /**
     * Given an effect producing an A and a bunch of `Resource`s providing signal streams, runs the
     * effect and then re-runs it whenver a signal is received, producing a Stream[A].
     */
    def reRunOnResourceSignals(
      debounce: FiniteDuration,
      head:     Resource[F, fs2.Stream[F, ?]],
      tail:     Resource[F, fs2.Stream[F, ?]]*
    )(using Temporal[F]): Resource[F, fs2.Stream[F, A]] =
      reRunOnResourceSignals(NonEmptyList.of(head, tail*), debounce.some)

  extension [F[_], A](f: F[Pot[A]])
    /**
     * Given an effect `f` producing an A and a bunch of signal streams, returns a `Stream` that
     * starts with the result of `f` and then upon each signal, produces `Pending` followed by the
     * result of re-evaluating `f`.
     */
    def resetOnSignal(
      signal:   fs2.Stream[F, Unit],
      debounce: Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): fs2.Stream[F, Pot[A]] = {
      val debouncedSignal = debounce.fold(signal)(signal.debounce)
      fs2.Stream.eval(f) ++ debouncedSignal.flatMap(_ =>
        fs2.Stream(Pot.pending) ++ fs2.Stream.eval(f)
      )
    }

    /**
     * Given an effect `f` producing an A and a bunch of signal streams, returns a `Stream` that
     * starts with the result of `f` and then upon each signal, produces `Pending` followed by the
     * result of re-evaluating `f`.
     */
    def resetOnSignals(
      signals:  NonEmptyList[fs2.Stream[F, Unit]],
      debounce: Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): fs2.Stream[F, Pot[A]] =
      resetOnSignal(signals.reduceLeft(_ merge _), debounce)

    private def resetOnResourceSignalsB(
      subscriptions: NonEmptyList[Resource[F, fs2.Stream[F, ?]]],
      debounce:      Option[FiniteDuration] = 2.seconds.some
    )(using Temporal[F]): Resource[F, fs2.Stream[F, Pot[A]]] =
      subscriptions.sequence
        .map(ss => resetOnSignals(ss.map(_.void), debounce))

    /**
     * Given an effect `f` producing an A and a bunch of `Resource`s providing signal streams,
     * returns a `Stream` that starts with the result of `f` and then upon each signal, produces
     * `Pending` followed by the result of re-evaluating `f`.
     */
    def resetOnResourceSignals(
      head: Resource[F, fs2.Stream[F, ?]],
      tail: Resource[F, fs2.Stream[F, ?]]*
    )(using Temporal[F]): Resource[F, fs2.Stream[F, Pot[A]]] =
      resetOnResourceSignalsB(NonEmptyList.of(head, tail*))

    /**
     * Given an effect `f` producing an A and a bunch of `Resource`s providing signal streams,
     * returns a `Stream` that starts with the result of `f` and then upon each signal, produces
     * `Pending` followed by the result of re-evaluating `f`.
     */
    def resetOnResourceSignals(
      debounce: FiniteDuration,
      head:     Resource[F, fs2.Stream[F, ?]],
      tail:     Resource[F, fs2.Stream[F, ?]]*
    )(using Temporal[F]): Resource[F, fs2.Stream[F, Pot[A]]] =
      resetOnResourceSignalsB(NonEmptyList.of(head, tail*), debounce.some)
}

object effect extends effect
