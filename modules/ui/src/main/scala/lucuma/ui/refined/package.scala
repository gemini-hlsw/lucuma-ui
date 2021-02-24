// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.boolean.And
import eu.timepit.refined.boolean.Not
import eu.timepit.refined.char.LowerCase
import eu.timepit.refined.collection.Forall
import eu.timepit.refined.collection.NonEmpty

/**
 * Convenience refined definitions.
 */
package object refined {
  type UpperNEPred = And[NonEmpty, Forall[Not[LowerCase]]]
  type UpperNES    = String Refined UpperNEPred
  object UpperNES extends RefinedTypeOps[UpperNES, String]
}
