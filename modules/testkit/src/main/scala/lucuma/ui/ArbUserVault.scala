// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso.arb

import eu.timepit.refined.scalacheck.string.*
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.arb.*
import lucuma.core.model.User
import lucuma.core.model.arb.*
import lucuma.ui.sso.UserVault
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Cogen

import java.time.Instant

// should go in testkit (new project!)
trait ArbUserVault {
  import ArbUser.given
  import ArbTime.given

  implicit val userVaultArb: Arbitrary[UserVault] = Arbitrary[UserVault] {
    for {
      user  <- arbitrary[User]
      exp   <- arbitrary[Instant]
      token <- arbitrary[NonEmptyString]
    } yield UserVault(user, exp, token)
  }

  implicit def userVaultCogen: Cogen[UserVault] =
    Cogen[(User, Instant, String)].contramap(m => (m.user, m.expiration, m.token.value))
}

object ArbUserVault extends ArbUserVault
