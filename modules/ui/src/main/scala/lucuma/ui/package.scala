// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import japgolly.scalajs.react.vdom.VdomNode
import lucuma.ui.components.SolarProgress
import react.common.*
import react.primereact.Message

val DefaultPendingRender: VdomNode = SolarProgress()

val DefaultErrorRender: Throwable => VdomNode =
  t => Message(text = t.getMessage, severity = Message.Severity.Error)
