// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.types.numeric.PosShort
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.enums.DatasetQaState
import lucuma.core.model.sequence.Dataset.Filename
import lucuma.core.util.TimestampInterval
import monocle.Focus
import monocle.Lens

case class Dataset(
  id:        Dataset.Id,
  index:     PosShort,
  filename:  Dataset.Filename,
  qaState:   Option[DatasetQaState],
  comment:   Option[NonEmptyString],
  interval:  Option[TimestampInterval],
  isWritten: Boolean
) derives Eq

object Dataset:
  type Id       = lucuma.core.model.sequence.Dataset.Id
  type Filename = lucuma.core.model.sequence.Dataset.Filename
  val Filename = lucuma.core.model.sequence.Dataset.Filename

  val id: Lens[Dataset, Dataset.Id] =
    Focus[Dataset](_.id)

  val index: Lens[Dataset, PosShort] =
    Focus[Dataset](_.index)

  val filename: Lens[Dataset, Dataset.Filename] =
    Focus[Dataset](_.filename)

  val qaState: Lens[Dataset, Option[DatasetQaState]] =
    Focus[Dataset](_.qaState)

  val comment: Lens[Dataset, Option[NonEmptyString]] =
    Focus[Dataset](_.comment)

  val interval: Lens[Dataset, Option[TimestampInterval]] =
    Focus[Dataset](_.interval)

  val isWritten: Lens[Dataset, Boolean] =
    Focus[Dataset](_.isWritten)
