// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.vdom.*
import lucuma.ui.utils.Render

trait render:
  // Render typeclass
  extension [A: Render](self: A) inline def renderVdom: VdomNode = Render[A].renderVdom(self)

object render extends render
