// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.kernel.Eq
import eu.timepit.refined.cats.*
import eu.timepit.refined.scalacheck.all.*
import lucuma.core.optics.laws.discipline.SplitEpiTests
import lucuma.core.util.arb.ArbTimeSpan.given
import monocle.law.discipline.IsoTests
import munit.DisciplineSuite
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.*

class OpticsSuite extends DisciplineSuite:
  checkAll("OptionNonEmptyStringIso", IsoTests(OptionNonEmptyStringIso))
  checkAll("Iso.option", IsoTests(OptionNonEmptyStringIso.option))
  checkAll("TimeSpanSecondsSplitEpi", SplitEpiTests(TimeSpanSecondsSplitEpi).splitEpi)
  checkAll("SortedSetFromList[Int]", SplitEpiTests(SortedSetFromList[Int]).splitEpi)
