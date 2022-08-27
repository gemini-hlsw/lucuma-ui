// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.enums

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.refined.*

enum ExecutionEnvironment(val suffix: Option[NonEmptyString]) derives Eq:
  case Development extends ExecutionEnvironment("DEV".refined[NonEmpty].some)
  case Staging     extends ExecutionEnvironment("STG".refined[NonEmpty].some)
  case Production  extends ExecutionEnvironment(none)
