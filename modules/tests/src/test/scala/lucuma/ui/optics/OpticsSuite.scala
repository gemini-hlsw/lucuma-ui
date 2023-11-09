// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.kernel.Eq
import eu.timepit.refined.cats.*
import eu.timepit.refined.scalacheck.all.*
import lucuma.core.optics.laws.discipline.SplitEpiTests
import lucuma.core.util.arb.ArbEnumerated.given
import lucuma.core.util.arb.ArbTimeSpan.given
import lucuma.schemas.model.ObservingMode.GmosSouthLongSlit
import lucuma.schemas.model.arb.ArbObservingMode.given
import monocle.law.discipline.IsoTests
import monocle.law.discipline.LensTests
import munit.DisciplineSuite
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.*

class OpticsSuite extends DisciplineSuite:
  val disjointZip2 = (GmosSouthLongSlit.grating, GmosSouthLongSlit.filter).disjointZip

  val disjointZip3 =
    (GmosSouthLongSlit.grating, GmosSouthLongSlit.filter, GmosSouthLongSlit.fpu).disjointZip

  val disjointZip4 =
    (GmosSouthLongSlit.grating,
     GmosSouthLongSlit.filter,
     GmosSouthLongSlit.fpu,
     GmosSouthLongSlit.explicitRoi
    ).disjointZip

  val disjointZip5 =
    (GmosSouthLongSlit.grating,
     GmosSouthLongSlit.filter,
     GmosSouthLongSlit.fpu,
     GmosSouthLongSlit.explicitRoi,
     GmosSouthLongSlit.defaultXBin
    ).disjointZip

  checkAll("disjointZip2", LensTests(disjointZip2))
  checkAll("disjointZip3", LensTests(disjointZip3))
  checkAll("disjointZip4", LensTests(disjointZip4))
  checkAll("disjointZip5", LensTests(disjointZip5))

  checkAll("OptionNonEmptyStringIso", IsoTests(OptionNonEmptyStringIso))
  checkAll("Iso.option", IsoTests(OptionNonEmptyStringIso.option))
  checkAll("TimeSpanSecondsSplitEpi", SplitEpiTests(TimeSpanSecondsSplitEpi).splitEpi)
  checkAll("SortedSetFromList[Int]", SplitEpiTests(SortedSetFromList[Int]).splitEpi)
