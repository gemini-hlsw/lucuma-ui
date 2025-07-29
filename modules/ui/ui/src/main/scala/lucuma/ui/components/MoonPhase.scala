// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnPropsWithChildren
import lucuma.react.common.style.Css
import lucuma.ui.syntax.all.given

object MoonStyles:
  val MoonSphere: Css  = Css("moon-sphere")
  val MoonLight: Css   = Css("moon-light")
  val MoonDark: Css    = Css("moon-dark")
  val MoonDivider: Css = Css("moon-divider")
  val MoonPhase: Css   = Css("moon-phase")

case class MoonPhase(moonPhase: Double, clazz: Css = Css.Empty)
    extends ReactFnPropsWithChildren[MoonPhase](MoonPhase.component)

object MoonPhase:
  private type Props = MoonPhase

  private val component = ScalaFnComponent.withChildren[Props]((p, c) =>
    <.div(MoonStyles.MoonPhase)(
      // Adapted from https://dev.to/thormeier/use-your-i-moon-gination-lets-build-a-moon-phase-visualizer-with-css-and-js-aih
      <.div(MoonStyles.MoonSphere)(
        <.div(MoonStyles.MoonDark).when(p.moonPhase < 0.5),
        <.div(MoonStyles.MoonLight).when(p.moonPhase > 0.5),
        <.div(MoonStyles.MoonDark).when(p.moonPhase > 0.5),
        <.div(MoonStyles.MoonLight).when(p.moonPhase < 0.5),
        <.div(MoonStyles.MoonDivider, ^.transform := s"rotate3d(0, 1, 0, ${360 * p.moonPhase}deg")
      ),
      c
    )
  )
