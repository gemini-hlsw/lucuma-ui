// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.Eq
import cats.syntax.all.*
import lucuma.core.enums.Instrument
import lucuma.core.enums.StepGuideState
import lucuma.core.math.Offset
import lucuma.core.math.SignalToNoise
import lucuma.core.math.Wavelength
import lucuma.core.model.sequence.*
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2FpuMask
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
 * We usually want to group executed steps by visits in the tables, thus dedicating an (expandable)
 * row to the visit.
 */
trait SequenceRow[+D]:
  def id: Either[Visit.Id, Step.Id]
  protected def instrumentConfig: Option[D]
  def stepConfig: Option[StepConfig]
  def telescopeConfig: Option[TelescopeConfig]
  def isFinished: Boolean
  def stepEstimate: Option[StepEstimate]
  def signalToNoise: Option[SignalToNoise]

  lazy val rowId: RowId = RowId:
    id match
      case Left(visitId) => visitId.toString
      case Right(stepId) => stepId.toString

  lazy val instrument: Option[Instrument] = instrumentConfig.map:
    case gmos.DynamicConfig.GmosNorth(_, _, _, _, _, _, _)  => Instrument.GmosNorth
    case gmos.DynamicConfig.GmosSouth(_, _, _, _, _, _, _)  => Instrument.GmosSouth
    case Flamingos2DynamicConfig(_, _, _, _, _, _, _, _, _) => Instrument.Flamingos2

  lazy val stepTypeDisplay: Option[StepTypeDisplay] =
    stepConfig.flatMap(StepTypeDisplay.fromStepConfig)

  lazy val offset: Option[Offset] = telescopeConfig.map(_.offset)

  lazy val guiding: Option[StepGuideState] = telescopeConfig.map(_.guiding)
  lazy val hasGuiding: Boolean             = guiding.contains_(StepGuideState.Enabled)

  lazy val wavelength: Option[Wavelength] = instrumentConfig.flatMap:
    case gn @ gmos.DynamicConfig.GmosNorth(_, _, _, _, _, _, _)  => gn.centralWavelength
    case gs @ gmos.DynamicConfig.GmosSouth(_, _, _, _, _, _, _)  => gs.centralWavelength
    case f2 @ Flamingos2DynamicConfig(_, _, _, _, _, _, _, _, _) => f2.centralWavelength.some

  lazy val exposureTime: Option[TimeSpan] = instrumentConfig.flatMap:
    case gmos.DynamicConfig.GmosNorth(exposure, _, _, _, _, _, _)  => exposure.some
    case gmos.DynamicConfig.GmosSouth(exposure, _, _, _, _, _, _)  => exposure.some
    case Flamingos2DynamicConfig(exposure, _, _, _, _, _, _, _, _) => exposure.some
    case _                                                         => none

  // There's no unified grating type, so we return a string.
  lazy val gratingName: Option[String] = instrumentConfig.flatMap:
    case gmos.DynamicConfig.GmosNorth(_, _, _, _, grating, _, _)    => grating.map(_.grating.shortName)
    case gmos.DynamicConfig.GmosSouth(_, _, _, _, grating, _, _)    => grating.map(_.grating.shortName)
    case Flamingos2DynamicConfig(_, disperser, _, _, _, _, _, _, _) =>
      disperser.map(_.shortName)

  // There's no unified FPU type, so we return a string.
  lazy val fpuName: Option[String] = instrumentConfig.flatMap:
    case gmos.DynamicConfig.GmosNorth(_, _, _, _, _, _, fpu)  =>
      fpu match
        case Some(GmosFpuMask.Builtin(builtin))     => builtin.longName.some
        case Some(GmosFpuMask.Custom(_, slitWidth)) => slitWidth.longName.some
        case None                                   => "Imaging".some
    case gmos.DynamicConfig.GmosSouth(_, _, _, _, _, _, fpu)  =>
      fpu match
        case Some(GmosFpuMask.Builtin(builtin))     => builtin.longName.some
        case Some(GmosFpuMask.Custom(_, slitWidth)) => slitWidth.longName.some
        case None                                   => "Imaging".some
    case Flamingos2DynamicConfig(_, _, _, _, _, fpu, _, _, _) =>
      fpu match
        case Flamingos2FpuMask.Builtin(builtin)     => builtin.longName.some
        case Flamingos2FpuMask.Custom(_, slitWidth) => slitWidth.longName.some
        case Flamingos2FpuMask.Imaging              => "Imaging".some
    case _                                                    =>
      none

  // There's no unified filter type, so we return a string.
  lazy val filterName: Option[String] = instrumentConfig.flatMap:
    case gmos.DynamicConfig.GmosNorth(_, _, _, _, _, filter, _)  => filter.map(_.shortName)
    case gmos.DynamicConfig.GmosSouth(_, _, _, _, _, filter, _)  => filter.map(_.shortName)
    case Flamingos2DynamicConfig(_, _, filter, _, _, _, _, _, _) => filter.shortName.some

  lazy val readoutXBin: Option[String] = instrumentConfig.collect:
    case gmos.DynamicConfig.GmosNorth(_, readout, _, _, _, _, _) => readout.xBin.shortName
    case gmos.DynamicConfig.GmosSouth(_, readout, _, _, _, _, _) => readout.xBin.shortName

  lazy val readoutYBin: Option[String] = instrumentConfig.collect:
    case gmos.DynamicConfig.GmosNorth(_, readout, _, _, _, _, _) => readout.yBin.shortName
    case gmos.DynamicConfig.GmosSouth(_, readout, _, _, _, _, _) => readout.yBin.shortName

  lazy val readMode: Option[String] = instrumentConfig.collect:
    case Flamingos2DynamicConfig(_, _, _, readMode, _, _, _, _, _) => readMode.shortName

  lazy val roi: Option[String] = instrumentConfig.collect:
    case gmos.DynamicConfig.GmosNorth(_, _, _, roi, _, _, _) => roi.shortName
    case gmos.DynamicConfig.GmosSouth(_, _, _, roi, _, _, _) => roi.shortName

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
    val telescopeConfig  = step.telescopeConfig.some
    val breakpoint       = step.breakpoint
    val isFinished       = false
    val stepEstimate     = step.estimate.some
    export step.{id => stepId}

  object FutureStep:
    def fromAtom[D](
      atom:              Atom[D],
      atomSignalToNoise: Option[SignalToNoise]
    ): List[FutureStep[D]] =
      FutureStep(
        atom.steps.head,
        atom.id,
        atom.steps.length.some.filter(_ > 1),
        atom.steps.head.getSignalToNoise(atomSignalToNoise)
      ) +: atom.steps.tail.map(step =>
        SequenceRow.FutureStep(step, atom.id, none, step.getSignalToNoise(atomSignalToNoise))
      )

    def fromAtoms[D](
      atoms:                List[Atom[D]],
      seqTypeSignalToNoise: Option[SignalToNoise]
    ): List[FutureStep[D]] =
      atoms.flatMap(atom =>
        FutureStep(
          atom.steps.head,
          atom.id,
          atom.steps.length.some.filter(_ > 1),
          atom.steps.head.getSignalToNoise(seqTypeSignalToNoise)
        ) +: atom.steps.tail.map(step =>
          SequenceRow.FutureStep(step, atom.id, none, step.getSignalToNoise(seqTypeSignalToNoise))
        )
      )

    given [D]: Eq[FutureStep[D]] = Eq.by(_.id)

  sealed abstract class Executed[+D] extends SequenceRow[D]:
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
      val telescopeConfig  = none
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
      val telescopeConfig  = stepRecord.telescopeConfig.some
      export stepRecord.{
        created,
        datasets,
        executionState,
        generatedId,
        id => stepId,
        interval,
        qaState
      }

    object ExecutedStep:
      given [D]: Eq[ExecutedStep[D]] = Eq.by(_.id)

  given [D]: Eq[SequenceRow[D]] = Eq.by(_.id)
