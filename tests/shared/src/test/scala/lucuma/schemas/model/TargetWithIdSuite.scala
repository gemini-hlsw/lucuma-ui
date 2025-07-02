// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.kernel.laws.discipline.*
import lucuma.schemas.model.arb.ArbTargetWithId.given
import monocle.law.discipline.*
import munit.DisciplineSuite

class TargetWithIdSuite extends DisciplineSuite:
  checkAll("Eq[TargetWithId]", EqTests[TargetWithId].eqv)
  checkAll("Eq[SiderealTargetWithId]", EqTests[SiderealTargetWithId].eqv)
  checkAll("TargetWithId.sidereal", PrismTests(TargetWithId.sidereal))
  checkAll("TargetWithId.nonsidereal", PrismTests(TargetWithId.nonsidereal))
