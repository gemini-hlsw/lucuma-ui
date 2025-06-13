// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.effect.Sync
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
      self.withOnMod: (oldA, newA) =>
        F.delay:
          println(s"CHANGED VIEW FROM OLD VALUE [$oldA] TO NEW VALUE [$newA]")
          dom.console.trace()

  extension [F[_], A](self: ViewOptF[F, A])
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewOptF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)

  extension [F[_], A](self: Reuse[ViewF[F, A]])
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): Reuse[ViewF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)

  extension [F[_], A](self: Reuse[ViewOptF[F, A]])
    @targetName("zoomSplitEpiOpt")
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): Reuse[ViewOptF[F, B]] =
      self.zoom(splitEpi.get)(splitEpi.modify)

object view extends view
