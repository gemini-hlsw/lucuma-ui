// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

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

package object reusability extends UtilReusabilityInstances with MathReusabilityInstances
