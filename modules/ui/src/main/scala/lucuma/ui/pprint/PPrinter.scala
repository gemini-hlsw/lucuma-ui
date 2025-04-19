// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.pprint

import cats.FlatMap
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import pprint.PPrinter as BasePPrinter

import scala.scalajs.LinkingInfo

sealed trait PPrinter:
  def apply(x: Any): String

class DevPPrinter extends PPrinter:
  private val PPrinterInstance: BasePPrinter =
    BasePPrinter(defaultHeight = 200, colorApplyPrefix = fansi.Color.Blue)

  def apply(x: Any): String =
    PPrinterInstance(x, initialOffset = 4).toString

class ProdPPrinter extends PPrinter:
  def apply(x: Any): String = x.toString

object PPrinter {
  private val printer: PPrinter = // Whole thing is shaken off in prod.
    if (LinkingInfo.developmentMode) new DevPPrinter else new ProdPPrinter

  def apply(x: Any): String = printer.apply(x)

  def error[F[_]](x: Any)(using logger: Logger[F]): F[Unit] =
    logger.error(apply(x))

  def error[F[_]: FlatMap](message: String, x: Any)(using logger: Logger[F]): F[Unit] =
    logger.error(message) >> logger.error(apply(x))

  def warn[F[_]](x: Any)(using logger: Logger[F]): F[Unit] =
    logger.warn(apply(x))

  def warn[F[_]: FlatMap](message: String, x: Any)(using logger: Logger[F]): F[Unit] =
    logger.warn(message) >> logger.warn(apply(x))

  def info[F[_]](x: Any)(using logger: Logger[F]): F[Unit] =
    logger.info(apply(x))

  def info[F[_]: FlatMap](message: String, x: Any)(using logger: Logger[F]): F[Unit] =
    logger.info(message) >> logger.info(apply(x))

  def debug[F[_]](x: Any)(using logger: Logger[F]): F[Unit] =
    logger.debug(apply(x))

  def debug[F[_]: FlatMap](message: String, x: Any)(using logger: Logger[F]): F[Unit] =
    logger.debug(message) >> logger.debug(apply(x))

  def trace[F[_]](x: Any)(using logger: Logger[F]): F[Unit] =
    logger.trace(apply(x))

  def trace[F[_]: FlatMap](message: String, x: Any)(using logger: Logger[F]): F[Unit] =
    logger.trace(message) >> logger.trace(apply(x))
}
