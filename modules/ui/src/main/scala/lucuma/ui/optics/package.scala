// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.data.NonEmptyChain
import cats.data.NonEmptyList

package object optics {
  type ValidateNec[E, T, A] = Validate[NonEmptyChain[E], T, A]
  type ValidateNel[E, T, A] = Validate[NonEmptyList[E], T, A]
  type ValidateInput[A]     = ValidateNec[String, String, A]
}
