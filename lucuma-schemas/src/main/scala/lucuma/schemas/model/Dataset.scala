// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.enums.DatasetQaState

final case class Dataset(index: PosInt, filename: NonEmptyString, qaState: Option[DatasetQaState])
    derives Eq
