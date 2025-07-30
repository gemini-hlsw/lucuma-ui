// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

import cats.kernel.laws.discipline.*
import lucuma.ui.sso.UserVault
import lucuma.ui.sso.arb.ArbUserVault.given
import munit.DisciplineSuite

class UserVaultSuite extends DisciplineSuite:
  checkAll("Eq[UserVault]", EqTests[UserVault].eqv)
