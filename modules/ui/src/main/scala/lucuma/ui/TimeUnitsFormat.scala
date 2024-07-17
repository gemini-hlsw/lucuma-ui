// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

enum TimeUnitsFormat(val hours: String, val minutes: String):
  case Letter       extends TimeUnitsFormat("h", "m")
  case Abbreviation extends TimeUnitsFormat("hrs", "mins")
