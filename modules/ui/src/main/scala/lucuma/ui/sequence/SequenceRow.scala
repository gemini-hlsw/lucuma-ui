// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.Eq
import cats.syntax.all.*
import lucuma.core.enums.Breakpoint
import lucuma.core.enums.GuideState
import lucuma.core.enums.Instrument
import lucuma.core.math.Angle
import lucuma.core.math.Axis
import lucuma.core.math.Offset
import lucuma.core.math.Wavelength
import lucuma.core.model.sequence.*
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.GmosFpuMask
import lucuma.core.util.TimeSpan
import lucuma.react.table.RowId
import lucuma.schemas.model.StepRecord
import lucuma.schemas.model.Visit

import java.time.Instant

/**
 * A row of a sequence table. It can be one of:
 *   - `FutureStep`
 *   - `Executed`:
 *     - `ExecutedVisit`
 *     - `ExecutedStep`
 * We usually want group executed steps by visits in the tables, thus dedicating an (expandable) row
 * to the visit.
 */
sealed trait SequenceRow[D](
  val id:           Either[Visit.Id, Step.Id],
  instrumentConfig: Option[D],
  val stepConfig:   Option[StepConfig],
  val breakpoint:   Breakpoint,
  val isFinished:   Boolean,
  val stepEstimate: Option[StepEstimate]
):
  lazy val rowId: RowId = RowId(id match
    case Left(visitId) => visitId.toString
    case Right(stepId) => stepId.toString
  )

  lazy val instrument: Option[Instrument] = instrumentConfig.map:
    case DynamicConfig.GmosNorth(_, _, _, _, _, _, _) => Instrument.GmosNorth
    case DynamicConfig.GmosSouth(_, _, _, _, _, _, _) => Instrument.GmosSouth

  lazy val stepTypeDisplay: Option[StepTypeDisplay] =
    stepConfig.flatMap(StepTypeDisplay.fromStepConfig)

  lazy val science: Option[StepConfig.Science] = stepConfig.flatMap(StepConfig.science.getOption)

  lazy val offset: Option[Offset] = science.map(_.offset)

  lazy val (p, q): (Option[Offset.P], Option[Offset.Q]) = stepConfig match
    case Some(StepConfig.Science(Offset(p, q), _)) => (p.some, q.some)
    case Some(_)                                   => (Offset.Component.Zero[Axis.P].some, Offset.Component.Zero[Axis.Q].some)
    case _                                         => (none, none)

  lazy val guiding: Option[GuideState] = science.map(_.guiding)
  lazy val hasGuiding: Boolean         = guiding.contains_(GuideState.Enabled)

  lazy val hasBreakpoint: Boolean = breakpoint === Breakpoint.Enabled

  lazy val wavelength: Option[Wavelength] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, _, _, _, grating, _, _) => grating.map(_.wavelength)
    case DynamicConfig.GmosSouth(_, _, _, _, grating, _, _) => grating.map(_.wavelength)

  lazy val exposureTime: Option[TimeSpan] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(exposure, _, _, _, _, _, _) => exposure.some
    case DynamicConfig.GmosSouth(exposure, _, _, _, _, _, _) => exposure.some
    case _                                                   => none

  // There's no unified grating type, so we return a string.
  lazy val gratingName: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, _, _, _, grating, _, _) => grating.map(_.grating.shortName)
    case DynamicConfig.GmosSouth(_, _, _, _, grating, _, _) => grating.map(_.grating.shortName)

  // There's no unified FPU type, so we return a string.
  lazy val fpuName: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, _, _, _, _, _, Some(GmosFpuMask.Builtin(builtin)))     =>
      builtin.longName.some
    case DynamicConfig.GmosNorth(_, _, _, _, _, _, Some(GmosFpuMask.Custom(_, slitWidth))) =>
      slitWidth.longName.some
    case DynamicConfig.GmosSouth(_, _, _, _, _, _, Some(GmosFpuMask.Builtin(builtin)))     =>
      builtin.longName.some
    case DynamicConfig.GmosSouth(_, _, _, _, _, _, Some(GmosFpuMask.Custom(_, slitWidth))) =>
      slitWidth.longName.some
    case _                                                                                 =>
      none

  // There's no unified filter type, so we return a string.
  lazy val filterName: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, _, _, _, _, filter, _) => filter.map(_.shortName)
    case DynamicConfig.GmosSouth(_, _, _, _, _, filter, _) => filter.map(_.shortName)

  lazy val readoutXBin: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, readout, _, _, _, _, _) => readout.xBin.shortName.some
    case DynamicConfig.GmosSouth(_, readout, _, _, _, _, _) => readout.xBin.shortName.some

  lazy val readoutYBin: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, readout, _, _, _, _, _) => readout.yBin.shortName.some
    case DynamicConfig.GmosSouth(_, readout, _, _, _, _, _) => readout.yBin.shortName.some

  lazy val roi: Option[String] = instrumentConfig.flatMap:
    case DynamicConfig.GmosNorth(_, _, _, roi, _, _, _) => roi.shortName.some
    case DynamicConfig.GmosSouth(_, _, _, roi, _, _, _) => roi.shortName.some

object SequenceRow:
  final class FutureStep[D](
    step:        Step[D],
    val atomId:  Atom.Id,
    val firstOf: Option[Int]
  ) extends SequenceRow[D](
        id = step.id.asRight,
        instrumentConfig = step.instrumentConfig.some,
        stepConfig = step.stepConfig.some,
        breakpoint = step.breakpoint,
        isFinished = false,
        stepEstimate = step.estimate.some
      ):
    export step.{id => stepId}

  object FutureStep:
    def fromAtom[D](atom: Atom[D]): List[FutureStep[D]] =
      FutureStep(atom.steps.head, atom.id, atom.steps.length.some.filter(_ > 1)) +:
        atom.steps.tail.map(step => SequenceRow.FutureStep(step, atom.id, none))

    def fromAtoms[D](atoms: List[Atom[D]]): List[FutureStep[D]] =
      atoms.flatMap(atom =>
        FutureStep(atom.steps.head, atom.id, atom.steps.length.some.filter(_ > 1)) +:
          atom.steps.tail.map(step => SequenceRow.FutureStep(step, atom.id, none))
      )

    given [D]: Eq[FutureStep[D]] = Eq.by: x =>
      (x.id, // TODO Is it same to assume step identity just by id ??
       x.breakpoint,
       x.stepEstimate,
       x.atomId,
       x.firstOf
      )

  sealed abstract class Executed[D](
    id:               Either[Visit.Id, Step.Id],
    instrumentConfig: Option[D],
    stepConfig:       Option[StepConfig]
  ) extends SequenceRow[D](
        id,
        instrumentConfig: Option[D],
        stepConfig: Option[StepConfig],
        breakpoint = Breakpoint.Disabled,
        isFinished = true,
        stepEstimate = none
      ):
    def created: Instant
    def startTime: Option[Instant]
    def endTime: Option[Instant]
    def duration: Option[TimeSpan]

  object Executed:
    final class ExecutedVisit[S, D](visit: Visit[S, D])
        extends Executed[D](
          id = visit.id.asLeft,
          instrumentConfig = none,
          stepConfig = none
        ):
      export visit.{created, duration, endTime, id => visitId, startTime}

    object ExecutedVisit:
      // TODO Is it same to assume step identity just by id ??
      given [S, D]: Eq[ExecutedVisit[S, D]] = Eq.by(_.id)

    final class ExecutedStep[D](stepRecord: StepRecord[D])
        extends Executed[D](
          id = stepRecord.id.asRight,
          instrumentConfig = stepRecord.instrumentConfig.some,
          stepConfig = stepRecord.stepConfig.some
        ):
      export stepRecord.{
        created,
        datasetEvents,
        datasets,
        duration,
        endTime,
        id => stepId,
        startTime,
        stepEvents,
        stepQaState
      }

    object ExecutedStep:
      // TODO Is it same to assume step identity just by id ??
      given [D]: Eq[ExecutedStep[D]] = Eq.by(_.id)

  given [S, D]: Eq[SequenceRow[D]] = Eq.instance:
    case (l: FutureStep[D], r: FutureStep[D])                                                     =>
      l === r
    case (l: Executed.ExecutedVisit[S @unchecked, D], r: Executed.ExecutedVisit[S @unchecked, D]) =>
      l === r
    case (l: Executed.ExecutedStep[D], r: Executed.ExecutedStep[D])                               =>
      l === r
    case _                                                                                        =>
      false
