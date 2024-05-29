// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.Eq
import cats.syntax.all.*
import lucuma.core.enums.Breakpoint
import lucuma.core.enums.Instrument
import lucuma.core.enums.StepGuideState
import lucuma.core.math.Angle
import lucuma.core.math.Axis
import lucuma.core.math.Offset
import lucuma.core.math.SignalToNoise
import lucuma.core.math.Wavelength
import lucuma.core.model.sequence.*
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.model.sequence.gmos.GmosFpuMask
import lucuma.core.util.TimeSpan
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.react.table.RowId
import lucuma.schemas.model.StepRecord
import lucuma.schemas.model.Visit

/**
 * A row of a sequence table. It can be one of:
 *   - `FutureStep`
 *   - `Executed`:
 *     - `ExecutedVisit`
 *     - `ExecutedStep`
 * We usually want group executed steps by visits in the tables, thus dedicating an (expandable) row
 * to the visit.
 */
trait SequenceRow[+D]:
  def id: Either[Visit.Id, Step.Id]
  protected def instrumentConfig: Option[D]
  def stepConfig: Option[StepConfig]
  def breakpoint: Breakpoint
  def isFinished: Boolean
  def stepEstimate: Option[StepEstimate]
  def signalToNoise: Option[SignalToNoise]

  lazy val rowId: RowId = RowId:
    id match
      case Left(visitId) => visitId.toString
      case Right(stepId) => stepId.toString

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

  lazy val guiding: Option[StepGuideState] = science.map(_.guiding)
  lazy val hasGuiding: Boolean             = guiding.contains_(StepGuideState.Enabled)

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
  case class FutureStep[+D](
    step:          Step[D],
    atomId:        Atom.Id,
    firstOf:       Option[Int],
    signalToNoise: Option[SignalToNoise]
  ) extends SequenceRow[D]:
    val id               = step.id.asRight
    val instrumentConfig = step.instrumentConfig.some
    val stepConfig       = step.stepConfig.some
    val breakpoint       = step.breakpoint
    val isFinished       = false
    val stepEstimate     = step.estimate.some
    export step.{id => stepId}

  object FutureStep:
    def fromAtom[D](
      atom:          Atom[D],
      signalToNoise: Step[D] => Option[SignalToNoise]
    ): List[FutureStep[D]] =
      FutureStep(
        atom.steps.head,
        atom.id,
        atom.steps.length.some.filter(_ > 1),
        signalToNoise(atom.steps.head)
      ) +: atom.steps.tail.map(step =>
        SequenceRow.FutureStep(step, atom.id, none, signalToNoise(step))
      )

    def fromAtoms[D](
      atoms:         List[Atom[D]],
      signalToNoise: Step[D] => Option[SignalToNoise]
    ): List[FutureStep[D]] =
      atoms.flatMap(atom =>
        FutureStep(
          atom.steps.head,
          atom.id,
          atom.steps.length.some.filter(_ > 1),
          signalToNoise(atom.steps.head)
        ) +: atom.steps.tail.map(step =>
          SequenceRow.FutureStep(step, atom.id, none, signalToNoise(step))
        )
      )

    given [D]: Eq[FutureStep[D]] = Eq.by(_.id)

  sealed abstract class Executed[+D] extends SequenceRow[D]:
    val breakpoint   = Breakpoint.Disabled
    val isFinished   = true
    val stepEstimate = none

    def created: Timestamp
    def interval: Option[TimestampInterval]

  object Executed:
    case class ExecutedVisit[+D](
      visit:         Visit[D],
      signalToNoise: Option[SignalToNoise]
    ) extends Executed[D]:
      val id               = visit.id.asLeft
      val instrumentConfig = none
      val stepConfig       = none
      export visit.{created, id => visitId, interval}

    object ExecutedVisit:
      given [D]: Eq[ExecutedVisit[D]] = Eq.by(_.id)

    case class ExecutedStep[+D](
      stepRecord:    StepRecord[D],
      signalToNoise: Option[SignalToNoise]
    ) extends Executed[D]:
      val id               = stepRecord.id.asRight
      val instrumentConfig = stepRecord.instrumentConfig.some
      val stepConfig       = stepRecord.stepConfig.some
      export stepRecord.{created, datasets, id => stepId, interval, qaState}

    object ExecutedStep:
      given [D]: Eq[ExecutedStep[D]] = Eq.by(_.id)

  given [D]: Eq[SequenceRow[D]] = Eq.by(_.id)
