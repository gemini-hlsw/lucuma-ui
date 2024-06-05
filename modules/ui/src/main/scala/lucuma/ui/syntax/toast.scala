// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css
import lucuma.react.fa.IconSize
import lucuma.react.primereact.Message
import lucuma.react.primereact.MessageItem
import lucuma.react.primereact.ToastRef
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles

trait toast:
  extension (toastRef: ToastRef)
    def show(
      text:     String,
      severity: Message.Severity = Message.Severity.Info,
      sticky:   Boolean = false
    ): Callback =
      toastRef.show(
        MessageItem(
          content = <.span(LucumaIcons.CircleInfo.withSize(IconSize.LG), text),
          severity = severity,
          sticky = sticky,
          clazz = LucumaStyles.Toast
        )
      )

object toast extends toast
