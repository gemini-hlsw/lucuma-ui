// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.ObserveClass
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import lucuma.core.model.sequence.gmos.DynamicConfig
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.schemas.model.enums.StepExecutionState
import monocle.Focus
import monocle.Lens

enum StepRecord[+D] derives Eq:
  def id: Step.Id
  def created: Timestamp
  def executionState: StepExecutionState
  def interval: Option[TimestampInterval]
  def instrumentConfig: D
  def stepConfig: StepConfig
  def observeClass: ObserveClass
  def qaState: Option[DatasetQaState]
  def datasets: List[Dataset]
  def generatedId: Option[Step.Id]

  case GmosNorth(
    id:               Step.Id,
    created:          Timestamp,
    executionState:   StepExecutionState,
    interval:         Option[TimestampInterval],
    instrumentConfig: DynamicConfig.GmosNorth,
    stepConfig:       StepConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset],
    generatedId:      Option[Step.Id]
  ) extends StepRecord[DynamicConfig.GmosNorth]

  case GmosSouth(
    id:               Step.Id,
    created:          Timestamp,
    executionState:   StepExecutionState,
    interval:         Option[TimestampInterval],
    instrumentConfig: DynamicConfig.GmosSouth,
    stepConfig:       StepConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset],
    generatedId:      Option[Step.Id]
  ) extends StepRecord[DynamicConfig.GmosSouth]

object StepRecord:
  object GmosNorth:
    given Eq[GmosNorth] = Eq.derived

    val id: Lens[GmosNorth, Step.Id] =
      Focus[GmosNorth](_.id)

    val created: Lens[GmosNorth, Timestamp] =
      Focus[GmosNorth](_.created)

    val executionState: Lens[GmosNorth, StepExecutionState] =
      Focus[GmosNorth](_.executionState)

    val interval: Lens[GmosNorth, Option[TimestampInterval]] =
      Focus[GmosNorth](_.interval)

    val instrumentConfig: Lens[GmosNorth, DynamicConfig.GmosNorth] =
      Focus[GmosNorth](_.instrumentConfig)

    val stepConfig: Lens[GmosNorth, StepConfig] =
      Focus[GmosNorth](_.stepConfig)

    val observeClass: Lens[GmosNorth, ObserveClass] =
      Focus[GmosNorth](_.observeClass)

    val qaState: Lens[GmosNorth, Option[DatasetQaState]] =
      Focus[GmosNorth](_.qaState)

    val datasets: Lens[GmosNorth, List[Dataset]] =
      Focus[GmosNorth](_.datasets)

  object GmosSouth:
    given Eq[GmosSouth] = Eq.derived

    val id: Lens[GmosSouth, Step.Id] =
      Focus[GmosSouth](_.id)

    val created: Lens[GmosSouth, Timestamp] =
      Focus[GmosSouth](_.created)

    val executionState: Lens[GmosSouth, StepExecutionState] =
      Focus[GmosSouth](_.executionState)

    val interval: Lens[GmosSouth, Option[TimestampInterval]] =
      Focus[GmosSouth](_.interval)

    val instrumentConfig: Lens[GmosSouth, DynamicConfig.GmosSouth] =
      Focus[GmosSouth](_.instrumentConfig)

    val stepConfig: Lens[GmosSouth, StepConfig] =
      Focus[GmosSouth](_.stepConfig)

    val observeClass: Lens[GmosSouth, ObserveClass] =
      Focus[GmosSouth](_.observeClass)

    val qaState: Lens[GmosSouth, Option[DatasetQaState]] =
      Focus[GmosSouth](_.qaState)

    val datasets: Lens[GmosSouth, List[Dataset]] =
      Focus[GmosSouth](_.datasets)
