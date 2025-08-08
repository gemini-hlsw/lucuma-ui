// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import io.circe.parser.decode
import munit.FunSuite

class ObservingModeSuite extends FunSuite:

  test("decode obsmode.json as ObservingMode"):
    val jsonSource = scala.io.Source.fromResource("obsmode.json")
    val json       = jsonSource.mkString
    jsonSource.close()

    val result = decode[ObservingMode](json)

    result match {
      case Right(gni: ObservingMode.GmosNorthImaging) =>
        assertEquals(gni.initialFilters.size, 1)
        assertEquals(gni.filters.size, 1)
        assertEquals(gni.explicitMultipleFiltersMode, None)
        assertEquals(gni.explicitBin, None)
        assertEquals(gni.explicitAmpReadMode, None)
        assertEquals(gni.explicitAmpGain, None)
        assertEquals(gni.explicitRoi, None)
        assertEquals(gni.offsets, List())
      case _                                          => assert(false, s"Failed to decode $json")
    }
