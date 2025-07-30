// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import cats.data.NonEmptyList
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import lucuma.ags.AgsAnalysis
import lucuma.core.enums.Flamingos2LyotWheel
import lucuma.core.enums.PortDisposition
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.flamingos2
import lucuma.core.geom.flamingos2.*
import lucuma.core.geom.flamingos2.all.*
import lucuma.core.geom.syntax.shapeexpression.*
import lucuma.core.math.Angle
import lucuma.core.math.Coordinates
import lucuma.core.math.Offset
import lucuma.core.model.sequence.flamingos2.Flamingos2FpuMask
import lucuma.react.common.style.Css
import lucuma.schemas.model.BasicConfiguration
import lucuma.ui.visualization.VisualizationStyles.*

import scala.collection.immutable.SortedMap

/**
 * Object to produce flamingos2 geometry for visualization
 */
object Flamingos2Geometry:

  // Shape to display for a specific mode
  def shapesForMode(
    posAngle:      Angle,
    offset:        Offset,
    configuration: Option[BasicConfiguration]
  ): SortedMap[Css, ShapeExpression] =
    configuration match {
      case Some(m: BasicConfiguration.Flamingos2LongSlit) =>
        SortedMap(
          (Flamingos2ScienceArea,
           scienceArea.shapeAt(posAngle,
                               offset,
                               Flamingos2LyotWheel.F16,
                               Flamingos2FpuMask.Builtin(m.fpu)
           )
          )
        )
      case _                                              =>
        SortedMap.empty
    }
  //
  // Shape to display always
  def commonShapes(
    l:        Flamingos2LyotWheel,
    posAngle: Angle,
    extraCss: Css
  ): SortedMap[Css, ShapeExpression] =
    SortedMap(
      (Flamingos2CandidatesArea |+| extraCss, candidatesAreaAt(l, posAngle, Offset.Zero))
    )

  // Shape for the intersection of patrol fields at each offset
  def patrolFieldIntersection(
    posAngle:      Angle,
    offsets:       NonEmptyList[Offset],
    configuration: BasicConfiguration,
    port:          PortDisposition,
    lyotWheel:     Flamingos2LyotWheel,
    extraCss:      Css = Css.Empty
  ): (Css, ShapeExpression) =
    (PatrolFieldIntersection |+| extraCss) ->
      offsets
        .map(patrolField(posAngle, _, configuration, lyotWheel, port))
        .reduce(using _ ∩ _)

  // Shape for the patrol field at a single position
  def patrolField(
    posAngle:      Angle,
    offset:        Offset,
    configuration: BasicConfiguration,
    lyotWheel:     Flamingos2LyotWheel,
    port:          PortDisposition
  ): ShapeExpression =
    configuration match {
      case m: BasicConfiguration.Flamingos2LongSlit =>
        flamingos2.patrolField.patrolFieldAt(posAngle, offset, lyotWheel, port)
      case _                                        =>
        ShapeExpression.Empty
    }

  // Shape to display always
  def probeShapes(
    posAngle:        Angle,
    guideStarOffset: Offset,
    offsetPos:       Offset,
    mode:            Option[BasicConfiguration],
    port:            PortDisposition,
    lyotWheel:       Flamingos2LyotWheel, // in practice this is always F16
    extraCss:        Css
  ): SortedMap[Css, ShapeExpression] =
    mode match
      case Some(m: BasicConfiguration.Flamingos2LongSlit) =>
        SortedMap(
          (Flamingos2ProbeArm |+| extraCss,
           probeArm.shapeAt(posAngle, guideStarOffset, offsetPos, lyotWheel, port)
          )
        )
      case _                                              =>
        SortedMap.empty

  // Full geometry for flamingos2
  def f2Geometry(
    referenceCoordinates:    Coordinates,
    scienceOffsets:          Option[NonEmptyList[Offset]],
    acquisitionOffsets:      Option[NonEmptyList[Offset]],
    fallbackPosAngle:        Option[Angle],
    conf:                    Option[BasicConfiguration],
    port:                    PortDisposition,
    gs:                      Option[AgsAnalysis.Usable],
    candidatesVisibilityCss: Css,
    lyotWheel:               Flamingos2LyotWheel = Flamingos2LyotWheel.F16 // in practice this is always F16
  ): Option[SortedMap[Css, ShapeExpression]] =
    gs.map(_.posAngle)
      .orElse(fallbackPosAngle)
      .map { posAngle =>
        // Shapes at base position
        val baseShapes: SortedMap[Css, ShapeExpression] =
          shapesForMode(posAngle, Offset.Zero, conf) ++
            commonShapes(lyotWheel, posAngle, candidatesVisibilityCss)

        // Don't show the probe if there is no usable GS
        val probe = gs
          .map { gs =>
            val gsOffset   =
              referenceCoordinates.diff(gs.target.tracking.baseCoordinates).offset
            val probeShape =
              probeShapes(posAngle, gsOffset, Offset.Zero, conf, port, lyotWheel, Css.Empty)

            val offsets =
              (scienceOffsets |+| acquisitionOffsets)
                .orElse(NonEmptyList.one(Offset.Zero).some)

            val patrolFieldIntersection =
              for {
                conf <- conf
                o    <- offsets
              } yield Flamingos2Geometry.patrolFieldIntersection(posAngle,
                                                                 o.distinct,
                                                                 conf,
                                                                 port,
                                                                 lyotWheel
              )

            patrolFieldIntersection.fold(probeShape)(probeShape + _)
          }

        baseShapes ++ probe.getOrElse(SortedMap.empty[Css, ShapeExpression])
      }
