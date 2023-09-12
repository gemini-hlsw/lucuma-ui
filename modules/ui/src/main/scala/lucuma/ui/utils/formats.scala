// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import eu.timepit.refined.types.numeric.PosInt
import lucuma.core.math.SignalToNoise
import lucuma.core.util.TimeSpan

def formatDurationSeconds(ts: TimeSpan): String =
  val seconds = ts.toSeconds
  f"$seconds%.0f sec"

def formatDurationHours(ts: TimeSpan): String =
  val seconds = ts.toSeconds
  if (seconds < 60)
    f"$seconds%.0f sec"
  else if (seconds < 3600)
    f"${seconds / 60.0}%.2f min"
  else
    f"${seconds / 3600.0}%.2f hr"

def format(time: TimeSpan, count: PosInt): String =
  s"$count × ${formatDurationSeconds(time)} = ${formatDurationHours(time *| count.value)}"

def formatSN(sn: SignalToNoise): String = f"${sn.toBigDecimal.toDouble}%.1f"
