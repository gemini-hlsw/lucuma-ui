// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.types.numeric.PosShort
import lucuma.core.enums.DatasetQaState
import lucuma.core.model.sequence.Dataset.Filename
import lucuma.core.util.TimestampInterval

case class Dataset(
  id:       Dataset.Id,
  index:    PosShort,
  filename: Dataset.Filename,
  qaState:  Option[DatasetQaState],
  interval: Option[TimestampInterval]
) derives Eq

object Dataset:
  type Id       = lucuma.core.model.sequence.Dataset.Id
  type Filename = lucuma.core.model.sequence.Dataset.Filename
  val Filename = lucuma.core.model.sequence.Dataset.Filename
