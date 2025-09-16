// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.Instrument
import lucuma.core.math.Offset
import lucuma.core.math.SignalToNoise
import lucuma.core.math.Wavelength
import lucuma.core.util.TimeSpan
import lucuma.react.common.*
import lucuma.react.syntax.*
import lucuma.react.table.*
import lucuma.ui.format.formatSN
import lucuma.ui.syntax.all.*
import lucuma.ui.table.*
import lucuma.ui.table.ColumnSize.*

import SequenceRowFormatters.*

// `T` is the actual type of the table row, from which we extract an `R` using `getStep`.
// `D` is the `DynamicConfig`.
// `TM` is the type of the table meta.
// `CM` is the type of the column meta.
// `TF` is the type of the global filter.
class SequenceColumns[D, T, R <: SequenceRow[D], TM, CM, TF](
  colDef:   ColumnDef.Applied[Expandable[HeaderOrRow[T]], TM, CM, TF],
  getStep:  T => Option[R],
  getIndex: T => Option[StepIndex]
):
  private lazy val indexAndTypeCol: colDef.TypeFor[(Option[StepIndex], Option[StepTypeDisplay])] =
    colDef(
      SequenceColumns.IndexAndTypeColumnId,
      _.value.toOption
        .map(row => (getIndex(row), getStep(row).flatMap(_.stepTypeDisplay)))
        .getOrElse((none, none)),
      header = "Step",
      cell = c =>
        React.Fragment(
          c.value._1.map(_.value.value),
          c.value._2.map(_.renderVdom)
        )
    )

  private lazy val exposureCol: colDef.TypeFor[Option[TimeSpan]] =
    colDef(
      SequenceColumns.ExposureColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.exposureTime)),
      header = _ => "Exp (sec)",
      cell = c =>
        (c.value, c.row.original.value.toOption.flatMap(getStep).flatMap(_.instrument)).mapN:
          (e, i) => FormatExposureTime(i)(e).value
    )

  private lazy val guideStateCol: colDef.TypeFor[Option[Boolean]] =
    colDef(
      SequenceColumns.GuideColumnId,
      _.value.toOption.flatMap(row => getStep(row).map(_.hasGuiding)),
      header = "",
      cell = _.value
        .filter(identity) // Only render on Some(true)
        .map(_ => SequenceIcons.Crosshairs.withClass(SequenceStyles.StepGuided))
    )

  private lazy val pOffsetCol: colDef.TypeFor[Option[Offset.P]] =
    colDef(
      SequenceColumns.PColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.offset.map(_.p))),
      header = _ => "p",
      cell = _.value.map(FormatOffsetP(_).value).orEmpty
    )

  private lazy val qOffsetCol: colDef.TypeFor[Option[Offset.Q]] =
    colDef(
      SequenceColumns.QColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.offset.map(_.q))),
      header = _ => "q",
      cell = _.value.map(FormatOffsetQ(_).value).orEmpty
    )

  private lazy val wavelengthCol: colDef.TypeFor[Option[Wavelength]] =
    colDef(
      SequenceColumns.WavelengthColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.wavelength)),
      header = _ => "λ (nm)",
      cell = _.value.map(FormatWavelength(_).value).getOrElse("-")
    )

  private lazy val fpuCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.FPUColumnId,
      _.value.toOption.map(row => getStep(row).flatMap(_.fpuName).getOrElse("None")),
      header = _ => "FPU",
      cell = _.value.orEmpty
    )

  private lazy val gratingCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.GratingColumnId,
      _.value.toOption.map(row => getStep(row).flatMap(_.gratingName).getOrElse("None")),
      header = "Grating",
      cell = _.value.orEmpty
    )

  private lazy val filterCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.FilterColumnId,
      _.value.toOption.map(row => getStep(row).flatMap(_.filterName).getOrElse("None")),
      header = "Filter",
      cell = _.value.orEmpty
    )

  private lazy val xBinCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.XBinColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.readoutXBin)),
      header = _ => "Xbin",
      cell = _.value.orEmpty
    )

  private lazy val yBinCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.YBinColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.readoutYBin)),
      header = _ => "Ybin",
      cell = _.value.orEmpty
    )

  private lazy val roiCol: colDef.TypeFor[Option[String]] =
    colDef(
      SequenceColumns.ROIColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.roi)),
      header = "ROI",
      cell = _.value.orEmpty
    )
  colDef(
    SequenceColumns.ROIColumnId,
    _.value.toOption.flatMap(row => getStep(row).flatMap(_.roi)),
    header = "ROI",
    cell = _.value.orEmpty
  )

  private lazy val snCol: colDef.TypeFor[Option[SignalToNoise]] =
    colDef(
      SequenceColumns.SNColumnId,
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.signalToNoise)),
      header = "S/N",
      cell = _.value.map(formatSN).orEmpty
    )

  private lazy val readModeCol: colDef.TypeFor[Option[String]] =
    colDef(
      ColumnId("readMode"),
      _.value.toOption.flatMap(row => getStep(row).flatMap(_.readMode)),
      header = "Read Mode",
      cell = _.value.orEmpty
    )

  lazy val ForGmos: List[colDef.TypeFor[?]] =
    List(
      indexAndTypeCol,
      exposureCol,
      guideStateCol,
      pOffsetCol,
      qOffsetCol,
      wavelengthCol,
      fpuCol,
      gratingCol,
      filterCol,
      xBinCol,
      yBinCol,
      roiCol,
      snCol
    )

  lazy val ForFlamingos2: List[colDef.TypeFor[?]] =
    List(
      indexAndTypeCol,
      exposureCol,
      guideStateCol,
      pOffsetCol,
      qOffsetCol,
      wavelengthCol,
      fpuCol,
      gratingCol,
      filterCol,
      readModeCol,
      snCol
    )

  def apply(instrument: Instrument): List[colDef.TypeFor[?]] =
    instrument match
      case Instrument.GmosNorth | Instrument.GmosSouth => ForGmos
      case Instrument.Flamingos2                       => ForFlamingos2
      case _                                           => throw new Exception(s"Unimplemented instrument: $instrument")

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
  val YBinColumnId: ColumnId         = ColumnId("ybin")
  val ROIColumnId: ColumnId          = ColumnId("roi")
  val ReadModeColumnId: ColumnId     = ColumnId("readMode")
  val SNColumnId: ColumnId           = ColumnId("sn")

  object BaseColumnSizes {
    private val CommonColumnSizes: Map[ColumnId, ColumnSize] = Map(
      IndexAndTypeColumnId -> FixedSize(60.toPx),
      ExposureColumnId     -> Resizable(77.toPx, min = 77.toPx, max = 130.toPx),
      GuideColumnId        -> FixedSize(33.toPx),
      PColumnId            -> FixedSize(75.toPx),
      QColumnId            -> FixedSize(75.toPx),
      WavelengthColumnId   -> Resizable(75.toPx, min = 75.toPx, max = 130.toPx),
      FPUColumnId          -> Resizable(132.toPx, min = 132.toPx),
      GratingColumnId      -> Resizable(120.toPx, min = 120.toPx),
      FilterColumnId       -> Resizable(90.toPx, min = 90.toPx),
      SNColumnId           -> Resizable(75.toPx, min = 75.toPx, max = 130.toPx)
    )

    val ForGmos: Map[ColumnId, ColumnSize] =
      CommonColumnSizes ++ Map(
        XBinColumnId -> FixedSize(60.toPx),
        YBinColumnId -> FixedSize(60.toPx),
        ROIColumnId  -> Resizable(75.toPx, min = 75.toPx)
      )

    val ForFlamingos2: Map[ColumnId, ColumnSize] =
      CommonColumnSizes ++ Map(
        ReadModeColumnId -> Resizable(75.toPx, min = 75.toPx)
      )

    def apply(instrument: Instrument): Map[ColumnId, ColumnSize] =
      instrument match
        case Instrument.GmosNorth | Instrument.GmosSouth => ForGmos
        case Instrument.Flamingos2                       => ForFlamingos2
        case _                                           => throw new Exception(s"Unimplemented instrument: $instrument")
  }

  // The order in which they are removed by overflow. The ones at the beginning go first.
  // Missing columns are not removed by overflow. (We declare them in reverse order)
  object BaseColumnPriorities {
    val ForGmos: List[ColumnId] = List(
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

    val ForFlamingos2: List[ColumnId] = List(
      PColumnId,
      QColumnId,
      GuideColumnId,
      ExposureColumnId,
      SNColumnId,
      FilterColumnId,
      GratingColumnId,
      FPUColumnId
    ).reverse

    def apply(instrument: Instrument): List[ColumnId] =
      instrument match
        case Instrument.GmosNorth | Instrument.GmosSouth => ForGmos
        case Instrument.Flamingos2                       => ForFlamingos2
        case _                                           => throw new Exception(s"Unimplemented instrument: $instrument")
  }

  def headerCell[T, R, TM, CM, FM](
    colId:  ColumnId,
    colDef: ColumnDef.Applied[Expandable[HeaderOrRow[T]], TM, CM, FM]
  ): colDef.TypeFor[Option[VdomNode]] =
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
