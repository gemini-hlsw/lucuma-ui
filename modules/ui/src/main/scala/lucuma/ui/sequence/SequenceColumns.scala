// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.all.*
import eu.timepit.refined.collection.NonEmpty
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.SignalToNoise
import lucuma.react.common.*
import lucuma.react.table.*
import lucuma.refined.*
import lucuma.ui.syntax.all.*
import lucuma.ui.utils.given

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

  // `T` is the actual type of the table row, from which we extract a `R` using `getStep`.
  // `D` is the `DynamicConfig`.
  def gmosColumns[D, T, R <: SequenceRow[D]](
    colDef:        ColumnDef.Applied[T],
    getStep:       T => Option[R],
    getIndex:      T => Option[StepIndex],
    signalToNoise: Option[R] => Option[SignalToNoise]
  ): List[ColumnDef.Single[T, ?]] =
    List(
      colDef(
        IndexAndTypeColumnId,
        row => (getIndex(row), getStep(row).flatMap(_.stepTypeDisplay)),
        header = "Step",
        cell = c =>
          React.Fragment(
            c.value._1.map(_.value.value),
            c.value._2.map(_.renderVdom)
          )
      ),
      colDef(
        ExposureColumnId,
        getStep(_).flatMap(_.exposureTime),
        header = _ => "Exp (sec)",
        cell = c =>
          (c.value, getStep(c.row.original).flatMap(_.instrument)).mapN: (e, i) =>
            FormatExposureTime(i)(e).value
      ),
      colDef(
        GuideColumnId,
        getStep(_).map(_.hasGuiding),
        header = "",
        cell = _.value
          .filter(identity) // Only render on Some(true)
          .map(_ => SequenceIcons.Crosshairs.withClass(SequenceStyles.StepGuided))
      ),
      colDef(
        PColumnId,
        getStep(_).flatMap(_.p),
        header = _ => "p",
        cell = _.value.map(FormatOffsetP)
      ),
      colDef(
        QColumnId,
        getStep(_).flatMap(_.q),
        header = _ => "q",
        cell = _.value.map(FormatOffsetQ)
      ),
      colDef(
        WavelengthColumnId,
        getStep(_).flatMap(_.wavelength),
        header = _ => "λ (nm)",
        cell = _.value.map(FormatWavelength).getOrElse("-".refined[NonEmpty])
      ),
      colDef(
        FPUColumnId,
        getStep(_).flatMap(_.fpuName),
        header = _ => "FPU",
        cell = _.value.getOrElse("None")
      ),
      colDef(
        GratingColumnId,
        getStep(_).flatMap(_.gratingName),
        header = "Grating",
        cell = _.value.getOrElse("None")
      ),
      colDef(
        FilterColumnId,
        getStep(_).flatMap(_.filterName),
        header = "Filter",
        cell = _.value.getOrElse("None")
      ),
      colDef(
        XBinColumnId,
        getStep(_).flatMap(_.readoutXBin),
        header = _ => "Xbin",
        cell = cell => cell.value.orEmpty
      ),
      colDef(
        YBinColumnId,
        getStep(_).flatMap(_.readoutYBin),
        header = _ => "Ybin",
        cell = cell => cell.value.orEmpty
      ),
      colDef(ROIColumnId, getStep(_).flatMap(_.roi), header = "ROI", cell = _.value.orEmpty),
      colDef(SNColumnId,
             s => signalToNoise(getStep(s)).foldMap(s => f"${s.toBigDecimal}%.3f"),
             header = "S/N"
      )
    )
