// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import japgolly.scalajs.react.vdom.VdomNode
import react.common.*
import react.primereact.Message
import react.primereact.ProgressSpinner

val DefaultPendingRender: VdomNode = ProgressSpinner(clazz = Css("pending-loader"))

val DefaultErrorRender: Throwable => VdomNode =
  t => Message(text = t.getMessage, severity = Message.Severity.Error)
