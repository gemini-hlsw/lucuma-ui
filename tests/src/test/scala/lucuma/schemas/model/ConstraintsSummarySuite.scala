// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.kernel.laws.discipline.EqTests
import lucuma.schemas.model.arb.ArbConstraintsSummary.given
import munit.DisciplineSuite

class ConstraintsSummarySuite extends DisciplineSuite:
  checkAll("Eq[ConstraintsSummary]", EqTests[ConstraintsSummary].eqv)
