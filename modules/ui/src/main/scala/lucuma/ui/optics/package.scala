// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.data.NonEmptyChain
import cats.data.NonEmptyList
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.optics.Format

package object optics {
  type InputFormat[A]          = Format[String, A]
  type ValidFormatNec[E, T, A] = ValidFormat[NonEmptyChain[E], T, A]
  type ValidFormatNel[E, T, A] = ValidFormat[NonEmptyList[E], T, A]
  type ValidFormatInput[A]     = ValidFormatNec[NonEmptyString, String, A]
  type ChangeAuditor[A]        = (String, Int) => AuditResult[A]
}
