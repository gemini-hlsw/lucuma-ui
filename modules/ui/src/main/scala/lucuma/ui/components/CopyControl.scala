// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.NewType
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.tooltip.*
import lucuma.ui.syntax.all.given

import scala.concurrent.duration.*

case class CopyControl(
  label:      String,
  textToCopy: String,
  onCopy:     Boolean => Callback = _ => Callback.empty,
  delay:      FiniteDuration = 1800.milliseconds
) extends ReactFnProps(CopyControl.component)

object CopyControl:
  private type Props = CopyControl

  private object Copied extends NewType[Boolean]

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .useState(Copied(false))
      .render: (props, copied) =>
        <.div(
          <.span(
            LoginStyles.CopyControlIcon,
            LoginStyles.Uncopied.unless(copied.value.value)
          )(
            props.label,
            CopyTextToClipboard(
              text = props.textToCopy,
              onCopy = (_, copiedCallback) =>
                props.onCopy(copiedCallback) *>
                  copied.setState(Copied(copiedCallback)) *>
                  copied.setState(Copied(false)).delay(props.delay).toCallback
            )(
              <.div(
                <.span(LoginIcons.Clipboard)
                  .withTooltip(content = "Copy to clipboard")
                  .unless(copied.value.value),
                <.span(LoginIcons.ClipboardCheck)
                  .withTooltip(content = "Copied!", hideDelay = 5000)
                  .when(copied.value.value)
              )
            )
          )
        )
