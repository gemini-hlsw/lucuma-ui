// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.enums.ObserveClass
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import lucuma.core.model.sequence.TelescopeConfig
import lucuma.core.model.sequence.flamingos2.Flamingos2DynamicConfig
import lucuma.core.model.sequence.gmos
import lucuma.core.util.Timestamp
import lucuma.core.util.TimestampInterval
import lucuma.schemas.model.enums.StepExecutionState
import monocle.Focus
import monocle.Lens

enum StepRecord[+D]:
  def id: Step.Id
  def created: Timestamp
  def executionState: StepExecutionState
  def interval: Option[TimestampInterval]
  def instrumentConfig: D
  def stepConfig: StepConfig
  def telescopeConfig: TelescopeConfig
  def observeClass: ObserveClass
  def qaState: Option[DatasetQaState]
  def datasets: List[Dataset]
  def generatedId: Option[Step.Id]

  case GmosNorth(
    id:               Step.Id,
    created:          Timestamp,
    executionState:   StepExecutionState,
    interval:         Option[TimestampInterval],
    instrumentConfig: gmos.DynamicConfig.GmosNorth,
    stepConfig:       StepConfig,
    telescopeConfig:  TelescopeConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset],
    generatedId:      Option[Step.Id]
  ) extends StepRecord[gmos.DynamicConfig.GmosNorth]

  case GmosSouth(
    id:               Step.Id,
    created:          Timestamp,
    executionState:   StepExecutionState,
    interval:         Option[TimestampInterval],
    instrumentConfig: gmos.DynamicConfig.GmosSouth,
    stepConfig:       StepConfig,
    telescopeConfig:  TelescopeConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset],
    generatedId:      Option[Step.Id]
  ) extends StepRecord[gmos.DynamicConfig.GmosSouth]

  case Flamingos2(
    id:               Step.Id,
    created:          Timestamp,
    executionState:   StepExecutionState,
    interval:         Option[TimestampInterval],
    instrumentConfig: Flamingos2DynamicConfig,
    stepConfig:       StepConfig,
    telescopeConfig:  TelescopeConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset],
    generatedId:      Option[Step.Id]
  ) extends StepRecord[Flamingos2DynamicConfig]

object StepRecord:
  given [A]: Eq[StepRecord[A]] = Eq.derived

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

    val instrumentConfig: Lens[GmosNorth, gmos.DynamicConfig.GmosNorth] =
      Focus[GmosNorth](_.instrumentConfig)

    val stepConfig: Lens[GmosNorth, StepConfig] =
      Focus[GmosNorth](_.stepConfig)

    val telescopeConfig: Lens[GmosNorth, TelescopeConfig] =
      Focus[GmosNorth](_.telescopeConfig)

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

    val instrumentConfig: Lens[GmosSouth, gmos.DynamicConfig.GmosSouth] =
      Focus[GmosSouth](_.instrumentConfig)

    val stepConfig: Lens[GmosSouth, StepConfig] =
      Focus[GmosSouth](_.stepConfig)

    val telescopeConfig: Lens[GmosSouth, TelescopeConfig] =
      Focus[GmosSouth](_.telescopeConfig)

    val observeClass: Lens[GmosSouth, ObserveClass] =
      Focus[GmosSouth](_.observeClass)

    val qaState: Lens[GmosSouth, Option[DatasetQaState]] =
      Focus[GmosSouth](_.qaState)

    val datasets: Lens[GmosSouth, List[Dataset]] =
      Focus[GmosSouth](_.datasets)

  object Flamingos2:
    given Eq[Flamingos2] = Eq.derived

    val id: Lens[Flamingos2, Step.Id] =
      Focus[Flamingos2](_.id)

    val created: Lens[Flamingos2, Timestamp] =
      Focus[Flamingos2](_.created)

    val executionState: Lens[Flamingos2, StepExecutionState] =
      Focus[Flamingos2](_.executionState)

    val interval: Lens[Flamingos2, Option[TimestampInterval]] =
      Focus[Flamingos2](_.interval)

    val instrumentConfig: Lens[Flamingos2, Flamingos2DynamicConfig] =
      Focus[Flamingos2](_.instrumentConfig)

    val stepConfig: Lens[Flamingos2, StepConfig] =
      Focus[Flamingos2](_.stepConfig)

    val telescopeConfig: Lens[Flamingos2, TelescopeConfig] =
      Focus[Flamingos2](_.telescopeConfig)

    val observeClass: Lens[Flamingos2, ObserveClass] =
      Focus[Flamingos2](_.observeClass)

    val qaState: Lens[Flamingos2, Option[DatasetQaState]] =
      Focus[Flamingos2](_.qaState)

    val datasets: Lens[Flamingos2, List[Dataset]] =
      Focus[Flamingos2](_.datasets)
