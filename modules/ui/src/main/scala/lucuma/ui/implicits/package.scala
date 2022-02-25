// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import crystal.ViewF
import lucuma.core.optics.SplitEpi
import lucuma.core.util.Display
import lucuma.core.util.Enumerated

package object implicits {
  implicit class ViewFOps[F[_], A](val self: ViewF[F, A]) extends AnyVal {
    def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewF[F, B] =
      self.zoom(splitEpi.get)(splitEpi.modify)
  }

  implicit def displayEnumByTag[A: Enumerated]: Display[A] =
    Display.byShortName(Enumerated[A].tag)
}
