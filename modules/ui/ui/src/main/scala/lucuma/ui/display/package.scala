// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.display

import lucuma.core.enums.*
import lucuma.core.util.Display

import java.util.concurrent.TimeUnit

given Display[Site] =
  Display.byShortName(_.shortName)

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

given Display[GmosXBinning] = Display.by(_.shortName, _.longName)

given Display[GmosYBinning] = Display.by(_.shortName, _.longName)

given Display[GmosNorthGrating] = Display.byShortName(_.longName)

given Display[GmosSouthGrating] = Display.byShortName(_.longName)

given Display[GmosNorthFilter] = Display.byShortName(_.longName)

given Display[GmosSouthFilter] = Display.byShortName(_.longName)

given Display[GmosNorthFpu] = Display.byShortName(_.longName)

given Display[GmosSouthFpu] = Display.byShortName(_.longName)

given Display[GmosAmpReadMode] =
  Display.by(_.shortName, _.longName)

given Display[GmosAmpGain] = Display.by(_.shortName, _.longName)

given Display[GmosRoi] = Display.byShortName(_.longName)

given Display[SequenceType] = Display.byShortName:
  case SequenceType.Acquisition => "Acquisition"
  case SequenceType.Science     => "Science"
