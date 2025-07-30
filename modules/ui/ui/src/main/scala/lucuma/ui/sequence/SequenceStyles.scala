// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.common.Css

object SequenceStyles:
  val SequenceTable: Css = Css("lucuma-sequence-table")
  val StepGuided: Css    = Css("lucuma-sequence-step-guided")

  val TableHeader           = Css("lucuma-sequence-header")
  val TableHeaderExpandable = Css("lucuma-sequence-header-expandable")
  val TableHeaderContent    = Css("lucuma-sequence-header-content")

  val HiddenColTableHeader: Css = Css("lucuma-sequence-hidden-col-table-header")
  val RowHasExtra: Css          = Css("lucuma-sequence-row-has-extra")
  val ExtraRowShown: Css        = Css("lucuma-sequence-extra-row-shown")

  val VisitHeader                   = Css("lucuma-sequence-visit-header")
  val VisitStepExtra                = Css("lucuma-sequence-visit-extraRow")
  val VisitStepExtraDatetime        = Css("lucuma-sequence-visit-extraRow-datetime")
  val VisitStepExtraStatus          = Css("lucuma-sequence-visit-extraRow-status")
  val VisitStepExtraDatasets        = Css("lucuma-sequence-visit-extraRow-datasets")
  val VisitStepExtraDatasetItem     = Css("lucuma-sequence-visit-extraRow-dataset-item")
  val VisitStepExtraDatasetQAStatus = Css("lucuma-sequence-visit-extraRow-dataset-qaStatus")

  val CurrentHeader = Css("lucuma-sequence-current-header")

  object StepType:
    val Bias: Css   = Css("lucuma-sequence-step-type-bias")
    val Dark: Css   = Css("lucuma-sequence-step-type-dark")
    val Arc: Css    = Css("lucuma-sequence-step-type-arc")
    val Flat: Css   = Css("lucuma-sequence-step-type-flat")
    val Object: Css = Css("lucuma-sequence-step-type-object")
