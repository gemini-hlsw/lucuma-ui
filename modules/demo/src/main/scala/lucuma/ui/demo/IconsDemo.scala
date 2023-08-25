// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.ui.sequence.*
import lucuma.react.common.*
import lucuma.react.fa.*

object IconsDemo:
  val component = ScalaFnComponent[Unit]: _ =>
    <.div(
      <.h2("Icons!"),
      <.p(
        SequenceIcons.StepType.Bias.withSize(IconSize.XL),
        SequenceIcons.StepType.Dark.withSize(IconSize.XL),
        SequenceIcons.StepType.Arc.withSize(IconSize.XL),
        SequenceIcons.StepType.Flat.withSize(IconSize.XL),
        SequenceIcons.StepType.Object.withSize(IconSize.XL)
      )
    )
