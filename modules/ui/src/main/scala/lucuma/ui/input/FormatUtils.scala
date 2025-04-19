// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.input

import eu.timepit.refined.types.numeric.NonNegInt

object FormatUtils {

  /** If the string represents a decimal number, strip trailing zeros past `n` decimal places. */
  def stripZerosPastNPlaces(str: String, n: NonNegInt): String = {
    val regex = s"(.*\\.\\d{0,${n.value}}\\d*?)0*".r
    str match {
      case regex(base) => base
      case _           => str
    }
  }
}
