// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import eu.timepit.refined.api.RefType
import lucuma.core.model._
import japgolly.scalajs.react.Reusability
import lucuma.core.data.EnumZipper
import lucuma.core.math.Angle
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
  implicit val angleReuse: Reusability[Angle] =
    Reusability.by(_.toMicroarcseconds)
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
trait ModelReusabiltyInstances extends RefinedReusabiltyInstances {
  implicit val userIdReuse: Reusability[User.Id]                 = Reusability.derive
  implicit val orcidIdReuse: Reusability[OrcidId]                = Reusability.by(_.value.toString)
  implicit val orcidProfileResuse: Reusability[OrcidProfile]     = Reusability.derive
  implicit val standardRoleIdReuse: Reusability[StandardRole.Id] = Reusability.derive
  implicit val partnerReuse: Reusability[Partner]                = Reusability.derive
  implicit val standardRoleReuse: Reusability[StandardRole]      = Reusability.derive
  implicit val standardUserReuse: Reusability[StandardUser]      = Reusability.derive
}

package object reusability
    extends UtilReusabilityInstances
    with MathReusabilityInstances
    with ModelReusabiltyInstances
