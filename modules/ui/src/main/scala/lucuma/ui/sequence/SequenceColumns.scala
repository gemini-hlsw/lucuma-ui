// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.react.syntax.*
import lucuma.react.table.*
import lucuma.ui.syntax.all.*
import lucuma.ui.table.*
import lucuma.ui.table.ColumnSize.*
import lucuma.ui.utils.formatSN

import SequenceRowFormatters.*

object SequenceColumns:
  val IndexAndTypeColumnId: ColumnId = ColumnId("stepType")
  val ExposureColumnId: ColumnId     = ColumnId("exposure")
  val GuideColumnId: ColumnId        = ColumnId("guide")
  val PColumnId: ColumnId            = ColumnId("p")
  val QColumnId: ColumnId            = ColumnId("q")
  val WavelengthColumnId: ColumnId   = ColumnId("lambda")
  val FPUColumnId: ColumnId          = ColumnId("fpu")
  val GratingColumnId: ColumnId      = ColumnId("grating")
  val FilterColumnId: ColumnId       = ColumnId("filter")
  val XBinColumnId: ColumnId         = ColumnId("xbin")
  val YBinColumnId: ColumnId         = ColumnId("Ybin")
  val ROIColumnId: ColumnId          = ColumnId("roi")
  val SNColumnId: ColumnId           = ColumnId("sn")

  val BaseColumnSizes: Map[ColumnId, ColumnSize] = Map(
    IndexAndTypeColumnId -> FixedSize(60.toPx),
    ExposureColumnId     -> Resizable(77.toPx, min = 77.toPx, max = 130.toPx),
    GuideColumnId        -> FixedSize(33.toPx),
    PColumnId            -> FixedSize(75.toPx),
    QColumnId            -> FixedSize(75.toPx),
    WavelengthColumnId   -> Resizable(75.toPx, min = 75.toPx, max = 130.toPx),
    FPUColumnId          -> Resizable(132.toPx, min = 132.toPx),
    GratingColumnId      -> Resizable(120.toPx, min = 120.toPx),
    FilterColumnId       -> Resizable(90.toPx, min = 90.toPx),
    XBinColumnId         -> FixedSize(60.toPx),
    YBinColumnId         -> FixedSize(60.toPx),
    ROIColumnId          -> Resizable(75.toPx, min = 75.toPx),
    SNColumnId           -> Resizable(75.toPx, min = 75.toPx, max = 130.toPx)
  )

  // The order in which they are removed by overflow. The ones at the beginning go first.
  // Missing columns are not removed by overflow. (We declare them in reverse order)
  val BaseColumnPriorities: List[ColumnId] = List(
    PColumnId,
    QColumnId,
    GuideColumnId,
    ExposureColumnId,
    SNColumnId,
    ROIColumnId,
    XBinColumnId,
    YBinColumnId,
    FilterColumnId,
    GratingColumnId,
    FPUColumnId
  ).reverse

  def headerCell[T, R, TM](
    colId:  ColumnId,
    colDef: ColumnDef.Applied[Expandable[HeaderOrRow[T]], TM]
  ): ColumnDef.Single.WithTableMeta[Expandable[HeaderOrRow[T]], Option[VdomNode], TM] =
    colDef(
      colId,
      _.value.left.toOption.map(_.content),
      header = "",
      cell = cell =>
        cell.value
          .map: header =>
            <.span(
              SequenceStyles.TableHeader,
              TagMod(
                SequenceStyles.TableHeaderExpandable,
                ^.onClick ==>
                  (_.stopPropagationCB >> cell.row.getToggleExpandedHandler()),
                <.span(
                  TableStyles.ExpanderChevron,
                  TableStyles.ExpanderChevronOpen.when(cell.row.getIsExpanded())
                )(TableIcons.ChevronRight.withFixedWidth())
              ).when(cell.row.getCanExpand()),
              <.span(SequenceStyles.TableHeaderContent)(header)
            ),
      enableResizing = false
    )

  // `T` is the actual type of the table row, from which we extract an `R` using `getStep`.
  // `D` is the `DynamicConfig`.
  // `TM` is the type of the table meta.
  def gmosColumns[D, T, R <: SequenceRow[D], TM](
    colDef:   ColumnDef.Applied[Expandable[HeaderOrRow[T]], TM],
    getStep:  T => Option[R],
    getIndex: T => Option[StepIndex]
  ): List[ColumnDef.Single.WithTableMeta[Expandable[HeaderOrRow[T]], ?, TM]] =
    List(
      colDef(
        IndexAndTypeColumnId,
        _.value.toOption
          .map(row => (getIndex(row), getStep(row).flatMap(_.stepTypeDisplay)))
          .getOrElse((none, none)),
        header = "Step",
        cell = c =>
          React.Fragment(
            c.value._1.map(_.value.value),
            c.value._2.map(_.renderVdom)
          )
      ),
      colDef(
        ExposureColumnId,
        _.value.toOption.flatMap(row => getStep(row).flatMap(_.exposureTime)),
        header = _ => "Exp (sec)",
        cell = c =>
          (c.value, c.row.original.value.toOption.flatMap(getStep).flatMap(_.instrument)).mapN:
            (e, i) => FormatExposureTime(i)(e).value
      ),
      colDef(
        GuideColumnId,
        _.value.toOption.flatMap(row => getStep(row).map(_.hasGuiding)),
        header = "",
        cell = _.value
          .filter(identity) // Only render on Some(true)
          .map(_ => SequenceIcons.Crosshairs.withClass(SequenceStyles.StepGuided))
      ),
      colDef(
        PColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.offset.map(_.p))),
        header = _ => "p",
        cell = _.value.map(_.map(FormatOffsetP(_).value)).orEmpty
      ),
      colDef(
        QColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.offset.map(_.q))),
        header = _ => "q",
        cell = _.value.map(_.map(FormatOffsetQ(_).value)).orEmpty
      ),
      colDef(
        WavelengthColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.wavelength)),
        header = _ => "λ (nm)",
        cell = _.value.map(_.map(FormatWavelength(_).value).getOrElse("-")).orEmpty
      ),
      colDef(
        FPUColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.fpuName).getOrElse("None")),
        header = _ => "FPU",
        cell = _.value.orEmpty
      ),
      colDef(
        GratingColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.gratingName).getOrElse("None")),
        header = "Grating",
        cell = _.value.orEmpty
      ),
      colDef(
        FilterColumnId,
        _.value.toOption.map(row => getStep(row).flatMap(_.filterName).getOrElse("None")),
        header = "Filter",
        cell = _.value.orEmpty
      ),
      colDef(
        XBinColumnId,
        _.value.toOption.flatMap(row => getStep(row).flatMap(_.readoutXBin)),
        header = _ => "Xbin",
        cell = _.value.orEmpty
      ),
      colDef(
        YBinColumnId,
        _.value.toOption.flatMap(row => getStep(row).flatMap(_.readoutYBin)),
        header = _ => "Ybin",
        cell = _.value.orEmpty
      ),
      colDef(
        ROIColumnId,
        _.value.toOption.flatMap(row => getStep(row).flatMap(_.roi)),
        header = "ROI",
        cell = _.value.orEmpty
      ),
      colDef(
        SNColumnId,
        _.value.toOption.flatMap(row => getStep(row).flatMap(_.signalToNoise)),
        header = "S/N",
        cell = _.value.map(formatSN).orEmpty
      )
    )
