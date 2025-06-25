// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import cats.data.NonEmptyList
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import lucuma.ags.AgsAnalysis
import lucuma.core.enums.PortDisposition
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.gmos
import lucuma.core.geom.syntax.shapeexpression.*
import lucuma.core.math.Angle
import lucuma.core.math.Coordinates
import lucuma.core.math.Offset
import lucuma.react.common.style.Css
import lucuma.schemas.model.BasicConfiguration
import lucuma.ui.visualization.VisualizationStyles.*

import scala.collection.immutable.SortedMap

/**
 * Test object to produce a gmos geometry. it is for demo purposes only
 */
object GmosGeometry:

  // Shape to display for a specific mode
  def shapesForMode(
    posAngle:      Angle,
    offset:        Offset,
    configuration: Option[BasicConfiguration],
    port:          PortDisposition
  ): SortedMap[Css, ShapeExpression] =
    val base =
      configuration
        .map(conf =>
          SortedMap((GmosScienceCcd, gmos.scienceArea.imaging ⟲ posAngle),
                    (GmosPatrolField, patrolField(posAngle, offset, conf, port))
          )
        )
        .getOrElse(SortedMap.empty[Css, ShapeExpression])
    configuration match {
      case Some(m: BasicConfiguration.GmosNorthLongSlit) =>
        base +
          (GmosFpu -> gmos.scienceArea.shapeAt(posAngle, offset, m.fpu.asLeft.some))
      case Some(m: BasicConfiguration.GmosSouthLongSlit) =>
        base +
          (GmosFpu -> gmos.scienceArea.shapeAt(posAngle, offset, m.fpu.asRight.some))
      case Some(_: BasicConfiguration.GmosNorthImaging)  =>
        base
      case Some(_: BasicConfiguration.GmosSouthImaging)  =>
        base
      case _                                             =>
        SortedMap.empty
    }

  // Shape for the intersection of patrol fields at each offset
  def patrolFieldIntersection(
    posAngle:      Angle,
    offsets:       NonEmptyList[Offset],
    configuration: BasicConfiguration,
    port:          PortDisposition,
    extraCss:      Css = Css.Empty
  ): (Css, ShapeExpression) =
    (PatrolFieldIntersection |+| extraCss) ->
      offsets
        .map(patrolField(posAngle, _, configuration, port))
        .reduce(using _ ∩ _)

  // Shape for the patrol field at a single position
  def patrolField(
    posAngle:      Angle,
    offset:        Offset,
    configuration: BasicConfiguration,
    port:          PortDisposition
  ): ShapeExpression =
    configuration match {
      case m: BasicConfiguration.GmosNorthLongSlit  =>
        gmos.patrolField.patrolFieldAt(posAngle, offset, m.fpu.asLeft.some, port)
      case m: BasicConfiguration.GmosSouthLongSlit  =>
        gmos.patrolField.patrolFieldAt(posAngle, offset, m.fpu.asRight.some, port)
      case _: BasicConfiguration.GmosNorthImaging   =>
        gmos.patrolField.patrolFieldAt(posAngle, offset, none, port)
      case _: BasicConfiguration.GmosSouthImaging   =>
        gmos.patrolField.patrolFieldAt(posAngle, offset, none, port)
      case m: BasicConfiguration.Flamingos2LongSlit =>
        ShapeExpression.Empty
    }

  // Shape to display always
  def commonShapes(posAngle: Angle, extraCss: Css): SortedMap[Css, ShapeExpression] =
    SortedMap(
      (GmosCandidatesArea |+| extraCss, gmos.candidatesArea.candidatesAreaAt(posAngle, Offset.Zero))
    )

  // Shape to display always
  def probeShapes(
    posAngle:        Angle,
    guideStarOffset: Offset,
    offsetPos:       Offset,
    mode:            Option[BasicConfiguration],
    port:            PortDisposition,
    extraCss:        Css
  ): SortedMap[Css, ShapeExpression] =
    mode match
      case Some(m: BasicConfiguration.GmosNorthLongSlit) =>
        SortedMap(
          (GmosProbeArm |+| extraCss,
           gmos.probeArm.shapeAt(posAngle, guideStarOffset, offsetPos, m.fpu.asLeft.some, port)
          )
        )
      case Some(m: BasicConfiguration.GmosSouthLongSlit) =>
        SortedMap(
          (GmosProbeArm |+| extraCss,
           gmos.probeArm.shapeAt(posAngle, guideStarOffset, offsetPos, m.fpu.asRight.some, port)
          )
        )
      case _                                             =>
        SortedMap(
          (GmosProbeArm |+| extraCss,
           gmos.probeArm.shapeAt(posAngle, guideStarOffset, offsetPos, none, port)
          )
        )

  // Full geometry for GMOS
  def gmosGeometry(
    referenceCoordinates:    Coordinates,
    scienceOffsets:          Option[NonEmptyList[Offset]],
    acquisitionOffsets:      Option[NonEmptyList[Offset]],
    fallbackPosAngle:        Option[Angle],
    conf:                    Option[BasicConfiguration],
    port:                    PortDisposition,
    gs:                      Option[AgsAnalysis.Usable],
    candidatesVisibilityCss: Css
  ): Option[SortedMap[Css, ShapeExpression]] =
    gs.map(_.posAngle)
      .orElse(fallbackPosAngle)
      .map { posAngle =>

        // Shapes at base position
        val baseShapes: SortedMap[Css, ShapeExpression] =
          shapesForMode(posAngle, Offset.Zero, conf, port) ++
            commonShapes(posAngle, candidatesVisibilityCss)

        // Don't show the probe if there is no usable GS
        val probe = gs
          .map { gs =>
            val gsOffset   =
              referenceCoordinates.diff(gs.target.tracking.baseCoordinates).offset
            val probeShape =
              probeShapes(posAngle, gsOffset, Offset.Zero, conf, port, Css.Empty)

            val offsets =
              (scienceOffsets |+| acquisitionOffsets)
                .orElse(NonEmptyList.one(Offset.Zero).some)

            val patrolFieldIntersection =
              for {
                conf <- conf
                o    <- offsets
              } yield GmosGeometry
                .patrolFieldIntersection(posAngle, o.distinct, conf, port)

            patrolFieldIntersection.fold(probeShape)(probeShape + _)
          }
        baseShapes ++ probe.getOrElse(SortedMap.empty[Css, ShapeExpression])
      }
