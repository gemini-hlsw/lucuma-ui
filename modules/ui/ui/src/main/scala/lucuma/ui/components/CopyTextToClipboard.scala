// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import org.scalajs.dom.window.navigator

type OnCopy = (String, Boolean) => Callback

/**
 * Text to be copied to clipboard You can pass an optional callback, will be called when text is
 * copied
 */
case class CopyTextToClipboard(
  text:   String,
  onCopy: OnCopy = (_, _) => Callback.empty
) extends ReactFnPropsWithChildren[CopyTextToClipboard](CopyTextToClipboard.component)

object CopyTextToClipboard:
  private type Props = CopyTextToClipboard

  private def copy(p: CopyTextToClipboard): Callback =
    AsyncCallback.fromJsPromise(navigator.clipboard.writeText(p.text)).toCallback.attempt.flatMap {
      case Right(_) => p.onCopy(p.text, true)
      case Left(_)  => p.onCopy(p.text, false)
    }

  private val component = ScalaFnComponent
    .withChildren[Props]((p, c) => <.div(^.onClick --> copy(p), c))
