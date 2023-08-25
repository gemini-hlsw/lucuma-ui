// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.common.Css

object SequenceStyles:
  val RightAligned: Css = Css("lucuma-sequece-right-aligned")
  val LeftAligned: Css  = Css("lucuma-sequece-left-aligned")
  val StepGuided: Css   = Css("lucuma-sequece-step-guided")

  object StepType:
    val Bias: Css   = Css("lucuma-sequece-step-type-bias")
    val Dark: Css   = Css("lucuma-sequece-step-type-dark")
    val Arc: Css    = Css("lucuma-sequece-step-type-arc")
    val Flat: Css   = Css("lucuma-sequece-step-type-flat")
    val Object: Css = Css("lucuma-sequece-step-type-object")
