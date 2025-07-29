// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model.arb

import lucuma.core.model.Target
import lucuma.core.model.arb.ArbTarget.given
import lucuma.core.util.arb.ArbGid.given
import lucuma.schemas.model.*
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.*
import org.scalacheck.Cogen

trait ArbTargetWithId:
  given Arbitrary[TargetWithId] = Arbitrary {
    for {
      id <- arbitrary[Target.Id]
      t  <- arbitrary[Target]
    } yield TargetWithId(id, t)
  }

  given Cogen[TargetWithId] =
    Cogen[(Target.Id, Target)].contramap(x => (x.id, x.target))

  given Arbitrary[SiderealTargetWithId] = Arbitrary {
    for {
      id <- arbitrary[Target.Id]
      t  <- arbitrary[Target.Sidereal]
    } yield SiderealTargetWithId(id, t)
  }

  given Cogen[SiderealTargetWithId] =
    Cogen[(Target.Id, Target.Sidereal)].contramap(x => (x.id, x.target))

  given Arbitrary[NonsiderealTargetWithId] = Arbitrary {
    for {
      id <- arbitrary[Target.Id]
      t  <- arbitrary[Target.Nonsidereal]
    } yield NonsiderealTargetWithId(id, t)
  }

  given Cogen[NonsiderealTargetWithId] =
    Cogen[(Target.Id, Target.Nonsidereal)].contramap(x => (x.id, x.target))

object ArbTargetWithId extends ArbTargetWithId
