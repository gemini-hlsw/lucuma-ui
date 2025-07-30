// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnProps
import lucuma.react.common.style.Css
import lucuma.react.primereact.Button
import lucuma.ui.LucumaIcons
import lucuma.ui.aladin.*
import lucuma.ui.primereact.*
import lucuma.ui.syntax.all.given

case class AladinZoomControl(
  aladinRef: Aladin,
  clazz:     Css = Css.Empty
) extends ReactFnProps(AladinZoomControl.component)

object AladinZoomControl {
  private type Props = AladinZoomControl

  val factor = 1.5

  private val component =
    ScalaFnComponent[Props](p =>
      <.div(
        AladinStyles.AladinZoomControl |+| p.clazz,
        Button(
          clazz = AladinStyles.ButtonOnAladin,
          icon = LucumaIcons.ThinPlus,
          onClick = p.aladinRef.increaseZoomCB
        ).small,
        Button(
          clazz = AladinStyles.ButtonOnAladin,
          icon = LucumaIcons.ThinMinus,
          onClick = p.aladinRef.decreaseZoomCB
        ).small
      )
    )
}
