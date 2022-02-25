// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.Eq
import cats.Order
import cats.data.NonEmptyList
import cats.data.NonEmptySet
import cats.kernel.instances.sortedMap._
import coulomb.Quantity
import eu.timepit.refined.api.RefType
import io.circe.Json
import japgolly.scalajs.react.Key
import japgolly.scalajs.react.ReactCats._
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.facade.JsNumber
import lucuma.core.data.EnumZipper
import lucuma.core.math._
import lucuma.core.math.dimensional._
import lucuma.core.model._
import lucuma.core.util.Enumerated
import react.common.Size
import react.common.implicits._

import java.time.Duration
import java.time.Instant
import scala.annotation.nowarn
import scala.collection.immutable.SortedMap
import scala.collection.immutable.SortedSet

/**
 * Instances of reusability for some utility types
 */
trait UtilReusabilityInstances {
  implicit def enumReuse[A: Enumerated]: Reusability[A] =
    Reusability.by(Enumerated[A].tag)

  implicit def enumZipperReuse[A: Reusability]: Reusability[EnumZipper[A]] =
    Reusability.by(z => (z.lefts, z.focus, z.rights))

  implicit val jsonReuse: Reusability[Json] = Reusability.by_==

  implicit def sortedSetReuse[A: Order]: Reusability[SortedSet[A]]    = Reusability.byEq
  implicit def sortedMapReuse[K, V: Eq]: Reusability[SortedMap[K, V]] = Reusability.byEq

  implicit def nonEmptyListReuse[A: Reusability]: Reusability[NonEmptyList[A]] =
    Reusability.by(nel => (nel.head, nel.tail))
  implicit def nonEmptySetReuse[A]: Reusability[NonEmptySet[A]]                =
    Reusability.by(_.toSortedSet.unsorted)

  implicit val keyReuse: Reusability[Key] = Reusability.by_==
}

/**
 * Instances of reusability for some common math types
 */
trait MathReusabilityInstances {
  // Reusability for coulomb quantities.
  implicit def quantityReuse[N: Reusability, U]: Reusability[Quantity[N, U]] =
    Reusability.by(_.value)
  implicit val angleReuse: Reusability[Angle]                                = Reusability.by(_.toMicroarcseconds)
  implicit def raReuse: Reusability[RightAscension]                          = Reusability.byEq
  implicit def decReuse: Reusability[Declination]                            = Reusability.byEq
  implicit def coordinatesReuse: Reusability[Coordinates]                    = Reusability.byEq
  implicit def epochReuse: Reusability[Epoch]                                = Reusability.byEq
  implicit def pmReuse: Reusability[ProperMotion]                            = Reusability.byEq
  implicit def rvReuse: Reusability[RadialVelocity]                          = Reusability.byEq
  implicit def parallaxReuse: Reusability[Parallax]                          = Reusability.byEq
  implicit val brightnessValueReuse: Reusability[BrightnessValue]            = Reusability.byEq
  implicit val jsNumberReuse: Reusability[JsNumber]                          = Reusability.byEq
  implicit val bigDecimalReuse: Reusability[BigDecimal]                      = Reusability.byEq
  implicit val sizeReuse: Reusability[Size]                                  = Reusability.by(x => (x.height, x.width))
  implicit val unitsReuse: Reusability[Units]                                = Reusability.byEq
  @nowarn // Reusability context bound is required but the compiler emits a warning anyway.
  implicit def measureReuse[N: Reusability]: Reusability[Measure[N]] = Reusability.derive
}

/**
 * reusability of time types
 */
trait TimeReusabilityInstances {
  implicit val durationReuse: Reusability[Duration] = Reusability.by(_.getSeconds)
  implicit val instantReuse: Reusability[Instant]   =
    Reusability.by(i => (i.getEpochSecond, i.getNano))
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
  implicit def idReuse[Id <: WithId#Id]: Reusability[Id]                      = Reusability.by(_.value)
  implicit val orcidIdReuse: Reusability[OrcidId]                             = Reusability.by(_.value.toString)
  implicit val orcidProfileResuse: Reusability[OrcidProfile]                  = Reusability.derive
  implicit val partnerReuse: Reusability[Partner]                             = Reusability.derive
  implicit val standardRoleReuse: Reusability[StandardRole]                   = Reusability.derive
  implicit val standardUserReuse: Reusability[StandardUser]                   = Reusability.derive
  implicit def catalogInfoReuse: Reusability[CatalogInfo]                     = Reusability.derive
  implicit def siderealTrackingReuse: Reusability[SiderealTracking]           = Reusability.derive
  implicit val unormalizedSEDReuse: Reusability[UnnormalizedSED]              = Reusability.byEq
  implicit val sourceProfileReuse: Reusability[SourceProfile]                 = Reusability.byEq
  implicit val userReuse: Reusability[User]                                   = Reusability.byEq
  implicit val offsetReuse: Reusability[Offset]                               = Reusability.byEq
  implicit val wavelengthReuse: Reusability[Wavelength]                       = Reusability.byEq
  implicit def spectralDefinitionReuse[T]: Reusability[SpectralDefinition[T]] = Reusability.derive
  implicit val semesterReuse: Reusability[Semester]                           = Reusability.derive
  implicit val ephemerisKeyReuse: Reusability[EphemerisKey]                   = Reusability.derive
  implicit val siderealTargetReuse: Reusability[Target.Sidereal]              = Reusability.derive
  implicit val nonsiderealTargetReuse: Reusability[Target.Nonsidereal]        = Reusability.derive
  implicit val targetReuse: Reusability[Target]                               = Reusability.derive
}

package object reusability
    extends UtilReusabilityInstances
    with MathReusabilityInstances
    with ModelReusabiltyInstances
    with TimeReusabilityInstances
