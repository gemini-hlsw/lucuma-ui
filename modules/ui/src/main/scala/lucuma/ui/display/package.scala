// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import lucuma.core.util.Display
import java.util.concurrent.TimeUnit

package object display:
  given Display[TimeUnit] = Display.by(
    {
      case TimeUnit.NANOSECONDS  => "ns"
      case TimeUnit.MICROSECONDS => "µs"
      case TimeUnit.MILLISECONDS => "ms"
      case TimeUnit.SECONDS      => "s"
      case TimeUnit.MINUTES      => "m"
      case TimeUnit.HOURS        => "h"
      case TimeUnit.DAYS         => "d"
    },
    _.toString.toLowerCase()
  )
