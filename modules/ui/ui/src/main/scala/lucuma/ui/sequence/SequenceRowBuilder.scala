// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.SequenceType
import lucuma.core.model.sequence.Step
import lucuma.core.syntax.all.*
import lucuma.core.util.Timestamp
import lucuma.core.util.time.format.UtcFormatter
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.tooltip.*
import lucuma.react.table.Expandable
import lucuma.react.table.Expanded
import lucuma.react.table.RowId
import lucuma.schemas.model.AtomRecord
import lucuma.schemas.model.Dataset
import lucuma.schemas.model.Visit
import lucuma.schemas.model.enums.StepExecutionState
import lucuma.ui.LucumaIcons
import lucuma.ui.display.given
import lucuma.ui.format.DurationFormatter
import lucuma.ui.sequence.*
import lucuma.ui.syntax.render.*
import lucuma.ui.table.*

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
    val rowId: RowId = RowId(s"$visitId-$sequenceType")

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
            .reduce(using _.plus(_))
        )
      )
    )

  protected def renderCurrentHeader(sequenceType: SequenceType): VdomNode =
    <.span(SequenceStyles.CurrentHeader, sequenceType.toString)

  // private val ArchiveBaseUrl = "https://archive.gemini.edu/preview" // In case they want the image instead
  private val ArchiveBaseUrl = "https://archive.gemini.edu/fullheader"

  private def renderQALabel(
    qaState: Option[DatasetQaState],
    comment: Option[NonEmptyString]
  ): String =
    qaState.fold("QA Not Set")(_.shortName) + comment.fold("")(c => s": $c")

  private def renderQaIcon(
    qaState: Option[DatasetQaState],
    comment: Option[NonEmptyString]
  ): VdomNode =
    <.span(qaState.renderVdom)
      .withTooltip(content = renderQALabel(qaState, comment), position = Tooltip.Position.Top)

  protected def renderVisitExtraRow(
    step:               SequenceRow.Executed.ExecutedStep[D],
    showOngoingLabel:   Boolean,
    renderDatasetQa:    (Dataset, VdomNode) => VdomNode = (_, renderIcon) => renderIcon,
    datasetIdsInFlight: Set[Dataset.Id] = Set.empty
  ) =
    <.div(SequenceStyles.VisitStepExtra)(
      <.span(SequenceStyles.VisitStepExtraDatetime)(
        step.interval
          .map(_.start.toInstant)
          .fold("---")(start => UtcFormatter.format(start))
      ),
      <.span(SequenceStyles.VisitStepExtraStatus)(
        step.executionState.renderVdom
          .unless(!showOngoingLabel && step.executionState === StepExecutionState.Ongoing)
      ),
      <.span(SequenceStyles.VisitStepExtraDatasets)(
        step.datasets
          .map: dataset =>
            val datasetName: String = dataset.filename.format

            <.span(^.key := dataset.id.toString)(SequenceStyles.VisitStepExtraDatasetItem)(
              if (dataset.isWritten)
                <.a(^.href := s"$ArchiveBaseUrl/$datasetName", ^.target.blank)(
                  datasetName
                )
              else datasetName,
              <.span(SequenceStyles.VisitStepExtraDatasetQAStatus)(
                if datasetIdsInFlight.contains_(dataset.id)
                then LucumaIcons.CircleNotch
                else renderDatasetQa(dataset, renderQaIcon(dataset.qaState, dataset.comment))
              )
            )
          .toVdomArray
      )
    )

  private def buildVisitRows(
    visitId:       Visit.Id,
    atoms:         List[AtomRecord[D]],
    sequenceType:  SequenceType,
    currentStepId: Option[Step.Id], // Will be removed from visit rows
    startIndex:    StepIndex = StepIndex.One
  ): (Option[VisitData], StepIndex) =
    atoms
      .flatMap(_.steps)
      .filterNot(step => currentStepId.contains_(step.id))
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
  def visitsSequences(
    visits:        List[Visit[D]],
    currentStepId: Option[Step.Id] // Will be removed from visits
  ): (List[VisitData], StepIndex) =
    visits
      .foldLeft((List.empty[VisitData], StepIndex.One))((accum, visit) =>
        val (seqs, scienceIndex) = accum

        // Acquisition indices restart at 1 in each visit.
        // Science indices continue from one visit to the next.
        val (acquisition, nextAcquisitionIndex) =
          buildVisitRows(visit.id, visit.acquisitionAtoms, SequenceType.Acquisition, currentStepId)

        val (science, nextScienceIndex) =
          buildVisitRows(
            visit.id,
            visit.scienceAtoms,
            SequenceType.Science,
            currentStepId,
            scienceIndex
          )

        (
          seqs ++ List(acquisition, science).flattenOption,
          nextScienceIndex
        )
      )

  protected val AlertRowId: RowId = RowId("alert")

  protected case class AlertRow(sequenceType: SequenceType, position: NonNegInt, content: VdomNode)

  def stitchSequence(
    visits:           List[VisitData],
    currentVisitId:   Option[Visit.Id],     // Used to move current visit steps to current sequences
    nextScienceIndex: StepIndex,            // Used to continue numbering from visits
    acquisitionRows:  List[SequenceRow[D]], // Should have completed steps already removed
    scienceRows:      List[SequenceRow[D]], // Should have completed steps already removed
    alertRow:         Option[AlertRow] = none
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

    def insertAlertRow(
      sequenceType: SequenceType,
      stepRows:     List[SequenceTableRowType]
    ): List[SequenceTableRowType] =
      alertRow
        .filter(_.sequenceType === sequenceType)
        .fold(stepRows): alert =>
          val (before, after) = stepRows.splitAt(alert.position.value)
          before ++ List(Expandable(HeaderRow(AlertRowId, alert.content).toHeaderOrRow)) ++ after

    def buildSequenceRows(
      sequenceType:     SequenceType,
      currentVisitRows: List[SequenceTableRowType],
      steps:            List[SequenceRow[D]],
      nextIndex:        StepIndex
    ): List[SequenceTableRowType] =
      Option
        .when(currentVisitRows.nonEmpty || steps.nonEmpty):
          Expandable(
            HeaderRow(
              RowId(sequenceType.toString),
              renderCurrentHeader(sequenceType)
            ).toHeaderOrRow,
            currentVisitRows ++
              insertAlertRow(
                sequenceType,
                steps
                  .zipWithStepIndex(nextIndex)
                  ._1
                  .map: (step, index) =>
                    Expandable(SequenceIndexedRow(step, index).toHeaderOrRow)
              )
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
