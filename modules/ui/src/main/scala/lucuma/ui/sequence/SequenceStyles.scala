// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.common.Css

object SequenceStyles:
  val RightAligned: Css = Css("lucuma-sequence-right-aligned")
  val LeftAligned: Css  = Css("lucuma-sequence-left-aligned")
  val StepGuided: Css   = Css("lucuma-sequence-step-guided")

  object StepType:
    val Bias: Css   = Css("lucuma-sequence-step-type-bias")
    val Dark: Css   = Css("lucuma-sequence-step-type-dark")
    val Arc: Css    = Css("lucuma-sequence-step-type-arc")
    val Flat: Css   = Css("lucuma-sequence-step-type-flat")
    val Object: Css = Css("lucuma-sequence-step-type-object")
