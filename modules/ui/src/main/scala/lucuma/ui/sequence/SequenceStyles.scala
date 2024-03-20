// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.common.Css

object SequenceStyles:
  val SequenceTable: Css = Css("lucuma-sequence-table")
  val StepGuided: Css    = Css("lucuma-sequence-step-guided")

  val TableHeader           = Css("lucuma-table-header")
  val TableHeaderExpandable = Css("lucuma-table-header-expandable")
  val TableHeaderContent    = Css("lucuma-table-header-content")

  val VisitHeader                      = Css("lucuma-visit-header")
  val VisitStepExtra                   = Css("lucuma-visit-step-extra")
  val VisitStepExtraDatetime           = Css("lucuma-visit-step-extra-datetime")
  val VisitStepExtraDatasets           = Css("lucuma-visit-step-extra-datasets")
  val VisitStepExtraDatasetItem        = Css("lucuma-visit-step-extra-dataset-item")
  val VisitStepExtraDatasetStatusIcon  = Css("lucuma-visit-step-extra-dataset-status-icon")
  val VisitStepExtraDatasetStatusLabel = Css("lucuma-visit-step-extra-dataset-status-label")

  object StepType:
    val Bias: Css   = Css("lucuma-sequence-step-type-bias")
    val Dark: Css   = Css("lucuma-sequence-step-type-dark")
    val Arc: Css    = Css("lucuma-sequence-step-type-arc")
    val Flat: Css   = Css("lucuma-sequence-step-type-flat")
    val Object: Css = Css("lucuma-sequence-step-type-object")
