// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import eu.timepit.refined.api.RefType
import japgolly.scalajs.react.CatsReact._
import japgolly.scalajs.react.Reusability
import lucuma.core.data.EnumZipper
import lucuma.core.math._
import lucuma.core.model._
import lucuma.core.util.Enumerated

/**
 * Instances of reusability for some utility types
 */
trait UtilReusabilityInstances {
  implicit def enumReuse[A: Enumerated]: Reusability[A] =
    Reusability.by(Enumerated[A].tag)

  implicit def enumZipperReuse[A: Reusability]: Reusability[EnumZipper[A]] =
    Reusability.by(z => (z.lefts, z.focus, z.rights))
}

/**
 * Instances of reusability for some common math types
 */
trait MathReusabilityInstances {
  implicit val angleReuse: Reusability[Angle]             =
    Reusability.by(_.toMicroarcseconds)
  implicit def raReuse: Reusability[RightAscension]       = Reusability.byEq
  implicit def decReuse: Reusability[Declination]         = Reusability.byEq
  implicit def coordinatesReuse: Reusability[Coordinates] = Reusability.byEq
  implicit def epochReuse: Reusability[Epoch]             = Reusability.byEq
  implicit def pvReuse: Reusability[ProperVelocity]       = Reusability.byEq
  implicit def rvReuse: Reusability[RadialVelocity]       = Reusability.byEq
  implicit def parallaxReuse: Reusability[Parallax]       = Reusability.byEq
}

/**
 * Generic reusability of refined types
 */
trait RefinedReusabiltyInstances {
  implicit def refTypeCogen[F[_, _], T: Reusability, P](implicit
    rt: RefType[F]
  ): Reusability[F[T, P]] =
    Reusability.by(rt.unwrap)
}

/**
 * Reusability instances for model classes
 */
trait ModelReusabiltyInstances
    extends RefinedReusabiltyInstances
    with UtilReusabilityInstances
    with MathReusabilityInstances {
  implicit val userIdReuse: Reusability[User.Id]                    = Reusability.derive
  implicit val orcidIdReuse: Reusability[OrcidId]                   = Reusability.by(_.value.toString)
  implicit val orcidProfileResuse: Reusability[OrcidProfile]        = Reusability.derive
  implicit val standardRoleIdReuse: Reusability[StandardRole.Id]    = Reusability.derive
  implicit val partnerReuse: Reusability[Partner]                   = Reusability.derive
  implicit val standardRoleReuse: Reusability[StandardRole]         = Reusability.derive
  implicit val standardUserReuse: Reusability[StandardUser]         = Reusability.derive
  implicit def catalogIdReuse: Reusability[CatalogId]               = Reusability.derive
  implicit def siderealTrackingReuse: Reusability[SiderealTracking] = Reusability.derive
}

package object reusability
    extends UtilReusabilityInstances
    with MathReusabilityInstances
    with ModelReusabiltyInstances
