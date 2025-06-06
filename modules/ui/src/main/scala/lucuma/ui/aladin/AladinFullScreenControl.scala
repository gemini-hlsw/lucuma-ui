// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import cats.syntax.all.*
import crystal.react.View
import japgolly.scalajs.react.*
import lucuma.core.util.NewBoolean
import lucuma.react.common.ReactFnProps
import lucuma.react.common.style.Css
import lucuma.react.primereact.Button
import lucuma.ui.LucumaIcons
import lucuma.ui.aladin.*
import lucuma.ui.primereact.*
import lucuma.ui.syntax.all.given

object AladinFullScreen extends NewBoolean:
  inline def FullScreen = True; inline def Normal = False

type AladinFullScreen = AladinFullScreen.Type

case class AladinFullScreenControl(
  fullScreen: View[AladinFullScreen]
) extends ReactFnProps(AladinFullScreenControl.component)

object AladinFullScreenControl {
  private type Props = AladinFullScreenControl

  private val component =
    ScalaFnComponent[Props](p =>
      Button(onClick = p.fullScreen.mod(_.flip))
        .withMods(
          AladinStyles.ButtonOnAladin |+| AladinStyles.AladinFullScreenButton,
          LucumaIcons.ExpandDiagonal.unless(p.fullScreen.get.value),
          LucumaIcons.ContractDiagonal.when(p.fullScreen.get.value)
        )
        .small
    )
}
