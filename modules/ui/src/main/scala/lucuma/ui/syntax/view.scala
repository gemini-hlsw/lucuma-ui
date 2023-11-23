// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.Monad
import cats.effect.Sync
import cats.syntax.all.*
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.reuse.*
import lucuma.core.optics.SplitEpi
import org.scalajs.dom

import scala.annotation.targetName

trait view:
  extension [F[_], A](self: ViewF[F, A])
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)

    /**
     * Prints a message to the console when the View is changed, including current stack trace.
     * Particularly useful to identify where a View is being changed from.
     */
    def debug(using F: Sync[F]): ViewF[F, A] =
      ViewF(
        self.get,
        (f, cb) =>
          F.delay {
            println(s"CHANGING VIEW FROM OLD VALUE [${self.get}] - INVOKED FROM:")
            dom.console.trace()
          } >> self.modCB(
            f,
            a => F.delay(println(s"CHANGED VIEW TO NEW VALUE [$a]")) >> cb(a)
          )
      )

  extension [F[_], A](self: ViewOptF[F, A])
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewOptF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)

  extension [F[_], A](self: Reuse[ViewF[F, A]])
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)

  extension [F[_], A](self: Reuse[ViewOptF[F, A]])
    @targetName("zoomSplitEpiOpt")
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewOptF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)

object views extends view
