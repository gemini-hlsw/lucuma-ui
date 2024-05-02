// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosInt
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.SequenceType
import lucuma.core.syntax.all.given
import lucuma.core.util.Timestamp
import lucuma.react.table.Expandable
import lucuma.react.table.Expanded
import lucuma.react.table.RowId
import lucuma.schemas.model.AtomRecord
import lucuma.schemas.model.Visit
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles
import lucuma.ui.display.given
import lucuma.ui.format.DurationFormatter
import lucuma.ui.format.UtcFormatter
import lucuma.ui.sequence.*
import lucuma.ui.table.*

import java.time.Duration

// Methods for building visits rows on the sequence table
trait SequenceRowBuilder[D]:
  protected type SequenceTableRowType = Expandable[HeaderOrRow[SequenceIndexedRow[D]]]

  protected def getRowId(row: SequenceTableRowType): RowId =
    row.value match
      case Left(HeaderRow(rowId, _)) => rowId
      case Right(stepRow)            => stepRow.step.rowId

  protected val CurrentExpandedState =
    Expanded.fromExpandedRows(
      RowId(SequenceType.Acquisition.toString),
      RowId(SequenceType.Science.toString)
    )

  protected case class VisitData(
    visitId:      Visit.Id,
    created:      Timestamp,
    sequenceType: SequenceType,
    stepRows:     NonEmptyList[SequenceIndexedRow[D]],
    datasetRange: Option[(Short, Short)]
  ):
    val rowId: RowId = RowId(s"$visitId-sequenceType")

  protected def renderVisitHeader(visit: VisitData): VdomNode =
    <.div(SequenceStyles.VisitHeader)( // Steps is non-empty => head is safe
      <.span(
        s"${visit.sequenceType.shortName} Visit on ${UtcFormatter.format(visit.created.toInstant)}"
      ),
      <.span(s"Steps: ${visit.stepRows.head.index} - ${visit.stepRows.last.index}"),
      <.span(
        "Files: " + visit.datasetRange
          .map((min, max) => s"$min - $max")
          .getOrElse("---")
      ),
      <.span(
        DurationFormatter(
          visit.stepRows
            .map(_.step.exposureTime.orEmpty.toDuration)
            .reduce(_.plus(_))
        )
      )
    )

  protected def renderCurrentHeader(sequenceType: SequenceType): VdomNode =
    <.span(SequenceStyles.CurrentHeader, sequenceType.toString)

  protected def renderVisitExtraRow(step: SequenceRow.Executed.ExecutedStep[D]) =
    <.div(SequenceStyles.VisitStepExtra)(
      <.span(SequenceStyles.VisitStepExtraDatetime)(
        step.interval
          .map(_.start.toInstant)
          .fold("---")(start => UtcFormatter.format(start))
      ),
      <.span(SequenceStyles.VisitStepExtraDatasets)(
        step.datasets
          .map: dataset =>
            <.span(SequenceStyles.VisitStepExtraDatasetItem)(
              dataset.filename.format,
              dataset.qaState.map: qaState =>
                React.Fragment(
                  LucumaIcons.Circle.withClass(
                    SequenceStyles.VisitStepExtraDatasetStatusIcon |+|
                      (qaState match
                        case DatasetQaState.Pass   => LucumaStyles.IndicatorOK
                        case DatasetQaState.Usable => LucumaStyles.IndicatorWarning
                        case DatasetQaState.Fail   => LucumaStyles.IndicatorFail
                      )
                  ),
                  <.span(SequenceStyles.VisitStepExtraDatasetStatusLabel)(
                    qaState.shortName
                  )
                )
            )
          .toVdomArray
      )
    )

  private def buildVisitRows(
    visitId:      Visit.Id,
    atoms:        List[AtomRecord[D]],
    sequenceType: SequenceType,
    startIndex:   StepIndex = StepIndex.One
  ): (Option[VisitData], StepIndex) =
    atoms
      .flatMap(_.steps)
      .some
      .filter(_.nonEmpty)
      .map: steps =>
        val datasetIndices = steps.flatMap(_.datasets).map(_.index.value)

        (
          steps.head.created,
          steps
            .map(SequenceRow.Executed.ExecutedStep(_, none)) // TODO Add SignalToNoise
            .zipWithStepIndex(startIndex),
          datasetIndices.minOption.map(min => (min, datasetIndices.max))
        )
      .map: (created, zipResult, datasetRange) =>
        val (rows, nextIndex) = zipResult

        (VisitData(
           visitId,
           created,
           sequenceType,
           NonEmptyList.fromListUnsafe(rows.map(SequenceIndexedRow(_, _))),
           datasetRange
         ).some,
         nextIndex
        )
      .getOrElse:
        (none, startIndex)

  /**
   * Returns a streamlined list of visits, splitting them into acquisition and science, followed by
   * the next science index.
   */
  def visitsSequences(visits: List[Visit[D]]): (List[VisitData], StepIndex) =
    visits
      .foldLeft((List.empty[VisitData], StepIndex.One))((accum, visit) =>
        val (seqs, scienceIndex) = accum

        // Acquisition indices restart at 1 in each visit.
        // Science indices continue from one visit to the next.
        val (acquisition, nextAcquisitionIndex) =
          buildVisitRows(visit.id, visit.acquisitionAtoms, SequenceType.Acquisition)

        val (science, nextScienceIndex) =
          buildVisitRows(visit.id, visit.scienceAtoms, SequenceType.Science, scienceIndex)

        (
          seqs ++ List(acquisition, science).flattenOption,
          nextScienceIndex
        )
      )

  def stitchSequence(
    visits:           List[VisitData],
    currentVisitId:   Option[Visit.Id],
    nextScienceIndex: StepIndex,
    acquisitionRows:  List[SequenceRow[D]],
    scienceRows:      List[SequenceRow[D]]
  ): List[SequenceTableRowType] = {
    val (pastVisits, currentVisits): (List[VisitData], List[VisitData]) =
      visits.partition: visitData =>
        !currentVisitId.contains_(visitData.visitId)

    val pastVisitsRows: List[SequenceTableRowType] =
      pastVisits.map: visit =>
        Expandable(
          HeaderRow(visit.rowId, renderVisitHeader(visit)).toHeaderOrRow,
          visit.stepRows.toList.map(step => Expandable(step.toHeaderOrRow))
        )

    def currentVisitsRows(sequenceType: SequenceType): List[SequenceTableRowType] =
      currentVisits
        .filter(_.sequenceType === sequenceType)
        .flatMap(_.stepRows.toList)
        .map(step => Expandable(step.toHeaderOrRow))

    val currentVisitAcquisitionRows: List[SequenceTableRowType] =
      currentVisitsRows(SequenceType.Acquisition)

    val currentVisitScienceRows: List[SequenceTableRowType] =
      currentVisitsRows(SequenceType.Science)

    def buildSequenceRows(
      sequenceType:     SequenceType,
      currentVisitRows: List[SequenceTableRowType],
      steps:            List[SequenceRow[D]],
      nextIndex:        StepIndex
    ): List[SequenceTableRowType] =
      Option
        .when(steps.nonEmpty):
          Expandable(
            HeaderRow(
              RowId(sequenceType.toString),
              renderCurrentHeader(sequenceType)
            ).toHeaderOrRow,
            currentVisitRows ++
              steps
                .zipWithStepIndex(nextIndex)
                ._1
                .map: (step, index) =>
                  Expandable(SequenceIndexedRow(step, index).toHeaderOrRow)
          )
        .toList

    val nextAcquisitionIndex: StepIndex =
      StepIndex(PosInt.unsafeFrom(currentVisitAcquisitionRows.size + 1))

    val acquisitionTableRows: List[SequenceTableRowType] =
      buildSequenceRows(
        SequenceType.Acquisition,
        currentVisitAcquisitionRows,
        acquisitionRows,
        nextAcquisitionIndex
      )

    val scienceTableRows: List[SequenceTableRowType] =
      buildSequenceRows(
        SequenceType.Science,
        currentVisitScienceRows,
        scienceRows,
        nextScienceIndex
      )

    pastVisitsRows ++ acquisitionTableRows ++ scienceTableRows
  }
