// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import cats.data.NonEmptyList
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import lucuma.ags.AgsAnalysis
import lucuma.core.enums.PortDisposition
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.f2.*
import lucuma.core.geom.f2.all.*
import lucuma.core.geom.syntax.shapeexpression.*
import lucuma.core.math.Angle
import lucuma.core.math.Coordinates
import lucuma.core.math.Offset
import lucuma.react.common.style.Css
import lucuma.schemas.model.BasicConfiguration
import lucuma.ui.visualization.VisualizationStyles.*

import scala.collection.immutable.SortedMap
import lucuma.core.enums.F2LyotWheel
import lucuma.core.model.sequence.f2.F2FpuMask

/**
 * Object to produce f2 geometry for visualization
 */
object Flamingos2Geometry:

  // Shape to display for a specific mode
  def shapesForMode(
    posAngle:      Angle,
    offset:        Offset,
    configuration: Option[BasicConfiguration]
  ): SortedMap[Css, ShapeExpression] =
    configuration match {
      case Some(m: BasicConfiguration.F2LongSlit) =>
        SortedMap(
          (F2ScienceArea,
           scienceArea.shapeAt(posAngle,
                               offset,
                               F2LyotWheel.F16,
                               F2FpuMask.Builtin(m.fpu)
           ) ⟲ posAngle
          )
        )
      case _                                      =>
        SortedMap.empty
    }
  //
  // Shape to display always
  def commonShapes(
    l:        F2LyotWheel,
    posAngle: Angle,
    extraCss: Css
  ): SortedMap[Css, ShapeExpression] =
    SortedMap(
      (F2CandidatesArea |+| extraCss, candidatesAreaAt(l, posAngle, Offset.Zero))
    )

  // Full geometry for f2
  def f2Geometry(
    referenceCoordinates:    Coordinates,
    scienceOffsets:          Option[NonEmptyList[Offset]],
    acquisitionOffsets:      Option[NonEmptyList[Offset]],
    fallbackPosAngle:        Option[Angle],
    conf:                    Option[BasicConfiguration],
    gs:                      Option[AgsAnalysis.Usable],
    candidatesVisibilityCss: Css,
    lyotWheel:               F2LyotWheel = F2LyotWheel.F16 // in practice this is always F16
  ): Option[SortedMap[Css, ShapeExpression]] =
    gs.map(_.posAngle)
      .orElse(fallbackPosAngle)
      .map { posAngle =>
        // Shapes at base position
        val baseShapes: SortedMap[Css, ShapeExpression] =
          shapesForMode(posAngle, Offset.Zero, conf) ++
            commonShapes(lyotWheel, posAngle, candidatesVisibilityCss)

        baseShapes
      }
