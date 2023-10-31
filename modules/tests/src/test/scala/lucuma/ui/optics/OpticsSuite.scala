// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.kernel.Eq
import lucuma.core.util.arb.ArbEnumerated.given
import lucuma.schemas.model.ObservingMode.GmosSouthLongSlit
import lucuma.schemas.model.arb.ArbObservingMode.given
import monocle.law.discipline.LensTests
import munit.DisciplineSuite
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.*

class OpticsSuite extends DisciplineSuite:
  val disjointZip2 =
    disjointZip(
      GmosSouthLongSlit.grating,
      GmosSouthLongSlit.filter
    )
  val disjointZip3 =
    disjointZip(
      GmosSouthLongSlit.grating,
      GmosSouthLongSlit.filter,
      GmosSouthLongSlit.fpu
    )
  val disjointZip4 =
    disjointZip(
      GmosSouthLongSlit.grating,
      GmosSouthLongSlit.filter,
      GmosSouthLongSlit.fpu,
      GmosSouthLongSlit.explicitRoi
    )

  checkAll("disjointZip2", LensTests(disjointZip2))
  checkAll("disjointZip3", LensTests(disjointZip3))
  checkAll("disjointZip4", LensTests(disjointZip4))
