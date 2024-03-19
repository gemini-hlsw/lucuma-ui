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

enum StepRecord[+D] derives Eq:
  def id: Step.Id
  def created: Timestamp
  def interval: Option[TimestampInterval]
  def instrumentConfig: D
  def stepConfig: StepConfig
  def observeClass: ObserveClass
  def qaState: Option[DatasetQaState]
  def datasets: List[Dataset]

  case GmosNorth protected[schemas] (
    id:               Step.Id,
    created:          Timestamp,
    interval:         Option[TimestampInterval],
    instrumentConfig: DynamicConfig.GmosNorth,
    stepConfig:       StepConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset]
  ) extends StepRecord[DynamicConfig.GmosNorth]

  case GmosSouth protected[schemas] (
    id:               Step.Id,
    created:          Timestamp,
    interval:         Option[TimestampInterval],
    instrumentConfig: DynamicConfig.GmosSouth,
    stepConfig:       StepConfig,
    observeClass:     ObserveClass,
    qaState:          Option[DatasetQaState],
    datasets:         List[Dataset]
  ) extends StepRecord[DynamicConfig.GmosSouth]
