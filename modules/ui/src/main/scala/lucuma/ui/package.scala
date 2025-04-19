// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import japgolly.scalajs.react.vdom.VdomNode
import lucuma.react.common.*
import lucuma.react.primereact.Message
import lucuma.ui.components.SolarProgress

val DefaultPendingRender: VdomNode = SolarProgress()

val DefaultErrorRender: Throwable => VdomNode =
  t => Message(text = t.getMessage, severity = Message.Severity.Error)
