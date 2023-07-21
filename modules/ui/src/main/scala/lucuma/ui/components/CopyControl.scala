// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.NewType
import lucuma.ui.syntax.all.given
import react.clipboard.CopyToClipboard
import react.common.ReactFnProps

case class CopyControl(label: String, textToCopy: String)
    extends ReactFnProps(CopyControl.component)

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
            CopyToClipboard(
              text = props.textToCopy,
              onCopy = (_, copiedCallback) =>
                copied.setState(Copied(copiedCallback)) *>
                  copied.setState(Copied(false)).delayMs(1500).toCallback
            )(
              <.span(
                LoginIcons.Clipboard.unless(copied.value.value),
                LoginIcons.ClipboardCheck.when(copied.value.value)
              )
            )
          )
        )
