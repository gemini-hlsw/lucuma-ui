// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import lucuma.ui.refined._
import cats.syntax.all._
import eu.timepit.refined.types.string.NonEmptyString

/**
 * Convenience ValidFormatInput instances.
 */
trait ValidFormatInputInstances {
  val nonEmptyValidFormat = ValidFormatInput[NonEmptyString](
    s => NonEmptyString.from(s).fold(_ => "Can't be empty".invalidNec, _.validNec),
    _.toString
  )

  val upperNESValidFormat = ValidFormatInput[UpperNES](
    s => UpperNES.from(s.toUpperCase).fold(_ => "Can't be empty".invalidNec, s => s.validNec),
    _.toString
  )
}
