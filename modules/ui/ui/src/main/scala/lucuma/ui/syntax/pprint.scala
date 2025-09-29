// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.FlatMap
import cats.effect.IO
import japgolly.scalajs.react.Callback
import lucuma.ui.pprint.PPrinter
import org.scalajs.dom
import org.typelevel.log4cats.Logger

trait pprint:
  extension (c: Callback.type) def plog(x: Any): Callback = Callback.log(PPrinter(x))

  extension (c: IO.type) def pprintln(x: Any): IO[Unit] = IO.println(PPrinter(x))

  extension [F[_]](logger: Logger[F])
    def perror(x: Any): F[Unit] =
      PPrinter.error(x)(using logger)

    def perror(message: String, x: Any)(using F: FlatMap[F]): F[Unit] =
      PPrinter.error(message, x)(using F, logger)

    def pwarn(x: Any): F[Unit] =
      PPrinter.warn(x)(using logger)

    def pwarn(message: String, x: Any)(using F: FlatMap[F]): F[Unit] =
      PPrinter.warn(message, x)(using F, logger)

    def pinfo(x: Any): F[Unit] =
      PPrinter.info(x)(using logger)

    def pinfo(message: String, x: Any)(using F: FlatMap[F]): F[Unit] =
      PPrinter.info(message, x)(using F, logger)

    def pdebug(x: Any): F[Unit] =
      PPrinter.debug(x)(using logger)

    def pdebug(message: String, x: Any)(using F: FlatMap[F]): F[Unit] =
      PPrinter.debug(message, x)(using F, logger)

    def ptrace(x: Any): F[Unit] =
      PPrinter.trace(x)(using logger)

    def ptrace(message: String, x: Any)(using F: FlatMap[F]): F[Unit] =
      PPrinter.trace(message, x)(using F, logger)

object pprint extends pprint

trait console:
  extension (c: Callback.type)
    def clog(x:   Any): Callback = Callback(dom.console.log(x))
    def cerror(x: Any): Callback = Callback(dom.console.error(x))

object console extends console
