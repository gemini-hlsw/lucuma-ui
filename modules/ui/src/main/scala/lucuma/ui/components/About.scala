// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.ui.primereact.LucumaPrimeStyles
import lucuma.ui.syntax.all.given
import react.common.Css
import react.common.ReactFnProps
import react.primereact.Dialog

case class About(
  systemName:      NonEmptyString,
  systemNameStyle: Css,
  version:         NonEmptyString,
  isOpen:          View[Boolean]
) extends ReactFnProps(About.component)

object About:
  private type Props = About

  private val component =
    ScalaFnComponent[Props]: props =>
      Dialog(
        visible = props.isOpen.get,
        onHide = props.isOpen.set(false),
        dismissableMask = true,
        clazz = LucumaPrimeStyles.Dialog.Small,
        resizable = false,
        header = Logo(props.systemName, props.systemNameStyle)
      )(
        CopyControl(s"Version: ${props.version}", props.version.value)
      )
