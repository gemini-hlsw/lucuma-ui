// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all._
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.generic.semiauto
import io.circe.refined._
import lucuma.core.enum.Breakpoint
import lucuma.core.enum.GmosNorthFpu
import lucuma.core.enum.GmosSouthFpu
import lucuma.core.enum.Instrument
import lucuma.core.enum.StepType
import lucuma.core.model.sequence._

import java.time.Duration

trait SequenceDecoders {

  implicit val stepTimeDecoder: Decoder[StepTime] = semiauto.deriveDecoder

  implicit val gmosCcdModeDecoder: Decoder[GmosCcdMode] = semiauto.deriveDecoder

  implicit val gmosNorthGratingDecoder: Decoder[GmosGrating.North] = semiauto.deriveDecoder

  implicit val gmosSouthGratingDecoder: Decoder[GmosGrating.South] = semiauto.deriveDecoder

  private val gmosCustomMaskDecoder: Decoder[GmosFpuMask.Custom] = semiauto.deriveDecoder

  implicit val gmosFpuCustomMaskDecoder: Decoder[GmosFpuMask.Custom] =
    Decoder.instance(_.downField("customMask").as[GmosFpuMask.Custom](gmosCustomMaskDecoder))

  implicit def gmosFpuBuiltinMaskDecoder[T: Decoder]: Decoder[GmosFpuMask.Builtin[T]] =
    Decoder.instance(_.downField("builtin").as[T].map(GmosFpuMask.Builtin.apply))

  implicit def gmosFpuOptionsDecoder[T: Decoder]: Decoder[GmosFpuMask[T]] =
    List[Decoder[GmosFpuMask[T]]](
      Decoder[GmosFpuMask.Custom].widen,
      Decoder[GmosFpuMask.Builtin[T]].widen
    ).reduceLeft(_ or _)

  implicit val gmosNorthFpuOptionsDecoder: Decoder[GmosFpuMask[GmosNorthFpu]] =
    gmosFpuOptionsDecoder[GmosNorthFpu]

  implicit val gmosSouthFpuOptionsDecoder: Decoder[GmosFpuMask[GmosSouthFpu]] =
    gmosFpuOptionsDecoder[GmosSouthFpu]

  implicit val gmosNorthDynamicConfigDecoder: Decoder[DynamicConfig.GmosNorth] =
    semiauto.deriveDecoder

  implicit val gmosSouthDynamicConfigDecoder: Decoder[DynamicConfig.GmosSouth] =
    semiauto.deriveDecoder

  implicit val gcalStepConfigDecoder: Decoder[StepConfig.Gcal] = semiauto.deriveDecoder

  implicit val scienceStepConfigDecoder: Decoder[StepConfig.Science] = semiauto.deriveDecoder

  implicit val stepConfigDecoder: Decoder[StepConfig] = Decoder.instance(c =>
    c.downField("stepType").as[StepType].flatMap {
      case StepType.Bias      => StepConfig.Bias.asRight
      case StepType.Dark      => StepConfig.Dark.asRight
      case StepType.Gcal      => c.as[StepConfig.Gcal]
      case StepType.Science   => c.as[StepConfig.Science]
      case StepType.SmartGcal => DecodingFailure("SmartGcal is not supported", c.history).asLeft
    }
  )

  implicit val gmosNorthStepDecoder: Decoder[Step.GmosNorth] =
    Decoder.instance { c =>
      for {
        id               <- c.downField("id").as[Step.Id]
        instrumentConfig <- c.downField("instrumentConfig").as[DynamicConfig.GmosNorth]
        stepConfig       <- c.downField("stepConfig").as[StepConfig]
        time             <- c.downField("time").as[StepTime]
        breakpoint       <- c.downField("breakpoint").as[Breakpoint]
      } yield Step.GmosNorth(id, instrumentConfig, stepConfig, time, breakpoint)
    }

  implicit val gmosSouthStepDecoder: Decoder[Step.GmosSouth] =
    Decoder.instance { c =>
      for {
        id               <- c.downField("id").as[Step.Id]
        instrumentConfig <- c.downField("instrumentConfig").as[DynamicConfig.GmosSouth]
        stepConfig       <- c.downField("stepConfig").as[StepConfig]
        time             <- c.downField("time").as[StepTime]
        breakpoint       <- c.downField("breakpoint").as[Breakpoint]
      } yield Step.GmosSouth(id, instrumentConfig, stepConfig, time, breakpoint)
    }

  implicit val gmosNorthAtomDecoder: Decoder[Atom.GmosNorth] =
    Decoder.instance(c =>
      for {
        id    <- c.downField("id").as[Atom.Id]
        steps <- c.downField("steps").as[List[Step.GmosNorth]]
      } yield Atom.GmosNorth(id, steps)
    )

  implicit val gmosSouthAtomDecoder: Decoder[Atom.GmosSouth] =
    Decoder.instance(c =>
      for {
        id    <- c.downField("id").as[Atom.Id]
        steps <- c.downField("steps").as[List[Step.GmosSouth]]
      } yield Atom.GmosSouth(id, steps)
    )

  implicit val gmosNodAndShuffleDecoder: Decoder[GmosNodAndShuffle] = semiauto.deriveDecoder

  implicit val gmosNorthStaticConfigDecoder: Decoder[StaticConfig.GmosNorth] =
    semiauto.deriveDecoder

  implicit val gmosSouthStaticConfigDecoder: Decoder[StaticConfig.GmosSouth] =
    semiauto.deriveDecoder

  implicit val gmosNorthManualConfigDecoder: Decoder[ManualConfig.GmosNorth] =
    Decoder.instance(c =>
      for {
        static      <- c.downField("staticN").as[StaticConfig.GmosNorth]
        setupTime   <- c.downField("plannedTime").downField("setup").as[Duration]
        acquisition <- c.downField("acquisitionN").downField("atoms").as[List[Atom.GmosNorth]]
        science     <- c.downField("scienceN").downField("atoms").as[List[Atom.GmosNorth]]
      } yield ManualConfig.GmosNorth(static, setupTime, acquisition, science)
    )

  implicit val gmosSouthManualConfigDecoder: Decoder[ManualConfig.GmosSouth] =
    Decoder.instance(c =>
      for {
        static      <- c.downField("staticS").as[StaticConfig.GmosSouth]
        setupTime   <- c.downField("plannedTime").downField("setup").as[Duration]
        acquisition <- c.downField("acquisitionS").downField("atoms").as[List[Atom.GmosSouth]]
        science     <- c.downField("scienceS").downField("atoms").as[List[Atom.GmosSouth]]
      } yield ManualConfig.GmosSouth(static, setupTime, acquisition, science)
    )

  implicit val manualConfigDecoder: Decoder[ManualConfig] = Decoder.instance(c =>
    c.downField("instrument").as[Instrument].flatMap {
      case Instrument.GmosNorth => c.as[ManualConfig.GmosNorth]
      case Instrument.GmosSouth => c.as[ManualConfig.GmosSouth]
      case _                    => DecodingFailure("Only Gmos supported", c.history).asLeft
    }
  )

  implicit val gmosNorthExecutionSequenceDecoder: Decoder[ExecutionSequence.GmosNorth] =
    Decoder.instance(c =>
      for {
        nextAtom       <- c.downField("nextAtom").as[Atom.GmosNorth]
        possibleFuture <- c.downField("possibleFuture").as[List[Atom.GmosNorth]]
      } yield ExecutionSequence.GmosNorth(nextAtom, possibleFuture)
    )

  implicit val gmosSouthExecutionSequenceDecoder: Decoder[ExecutionSequence.GmosSouth] =
    Decoder.instance(c =>
      for {
        nextAtom       <- c.downField("nextAtom").as[Atom.GmosSouth]
        possibleFuture <- c.downField("possibleFuture").as[List[Atom.GmosSouth]]
      } yield ExecutionSequence.GmosSouth(nextAtom, possibleFuture)
    )

  implicit val gmosNorthFutureExecutionConfigDecoder: Decoder[FutureExecutionConfig.GmosNorth] =
    Decoder.instance(c =>
      for {
        static      <- c.downField("staticN").as[StaticConfig.GmosNorth]
        acquisition <- c.downField("acquisitionN").as[ExecutionSequence.GmosNorth]
        science     <- c.downField("scienceN").as[ExecutionSequence.GmosNorth]
      } yield FutureExecutionConfig.GmosNorth(static, acquisition, science)
    )

  implicit val gmosSouthFutureExecutionConfigDecoder: Decoder[FutureExecutionConfig.GmosSouth] =
    Decoder.instance(c =>
      for {
        static      <- c.downField("staticS").as[StaticConfig.GmosSouth]
        acquisition <- c.downField("acquisitionS").as[ExecutionSequence.GmosSouth]
        science     <- c.downField("scienceS").as[ExecutionSequence.GmosSouth]
      } yield FutureExecutionConfig.GmosSouth(static, acquisition, science)
    )

  implicit val futureCxecutionConfigDecoder: Decoder[FutureExecutionConfig] = Decoder.instance(c =>
    c.downField("instrument").as[Instrument].flatMap {
      case Instrument.GmosNorth => c.as[FutureExecutionConfig.GmosNorth]
      case Instrument.GmosSouth => c.as[FutureExecutionConfig.GmosSouth]
      case _                    => DecodingFailure("Only Gmos supported", c.history).asLeft
    }
  )
}
