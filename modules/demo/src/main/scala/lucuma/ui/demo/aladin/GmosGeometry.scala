// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.Order
import cats.data.NonEmptyMap
import cats.implicits.*
import lucuma.core.enums.GmosNorthFpu
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.enums.PortDisposition
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.gmos
import lucuma.core.geom.syntax.shapeexpression.*
import lucuma.core.math.Angle
import lucuma.core.math.Offset
import lucuma.core.math.syntax.int.*
import lucuma.react.common.style.*

object GmosGeometry {

  val posAngle: Angle =
    145.deg

  val guideStarOffset: Offset =
    Offset(170543999.µas.p, -24177003.µas.q)

  val offsetPos: Offset =
    Offset(-60.arcsec.p, 60.arcsec.q)

  val fpu: Option[Either[GmosNorthFpu, GmosSouthFpu]] =
    Some(Right(GmosSouthFpu.LongSlit_5_00))

  val port: PortDisposition =
    PortDisposition.Side

  implicit val orderCss: Order[Css] = Order.by(_.htmlClass)

  // Shape to display
  val shapes: NonEmptyMap[Css, ShapeExpression] =
    NonEmptyMap.of(
      (Css("gmos-probe"), gmos.probeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port)),
      (Css("gmos-patrol-field"), gmos.probeArm.patrolFieldAt(posAngle, offsetPos, fpu, port)),
      (Css("gmos-science-ccd"), gmos.scienceArea.imaging ⟲ posAngle),
      (Css("gmos-science-ccd-offset"), gmos.scienceArea.imaging ↗ offsetPos ⟲ posAngle)
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

}
