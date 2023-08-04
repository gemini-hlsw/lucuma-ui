// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.vdom.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css

trait css:
  given Conversion[Css, TagMod] =
    ^.className := _.htmlClass

object css extends css
