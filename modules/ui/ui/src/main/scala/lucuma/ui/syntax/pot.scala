// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import crystal.Pot
import crystal.PotOption
import crystal.react.View
import japgolly.scalajs.react.vdom.*
import lucuma.ui.DefaultErrorRender
import lucuma.ui.DefaultPendingRender

trait pot:
  // Pot and friends convenience rendering methods
  extension [A](pot: Pot[A])
    inline def renderPot(
      valueRender:   A => VdomNode,
      pendingRender: => VdomNode = DefaultPendingRender,
      errorRender:   Throwable => VdomNode = DefaultErrorRender
    ): VdomNode =
      pot.fold(pendingRender, errorRender, valueRender)

  extension [A](potView: View[Pot[A]])
    inline def renderPotView(
      valueRender:   A => VdomNode,
      pendingRender: => VdomNode = DefaultPendingRender,
      errorRender:   Throwable => VdomNode = DefaultErrorRender
    ): VdomNode =
      potView.get.renderPot(valueRender, pendingRender, errorRender)

  extension [A](po: PotOption[A])
    inline def renderPotOption(
      valueRender:   A => VdomNode,
      pendingRender: => VdomNode = DefaultPendingRender,
      errorRender:   Throwable => VdomNode = DefaultErrorRender
    ): VdomNode = po.toPot.renderPot(valueRender, pendingRender, errorRender)

object pot extends pot
