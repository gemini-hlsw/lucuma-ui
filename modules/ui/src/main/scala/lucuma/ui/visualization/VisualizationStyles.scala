// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.visualization

import lucuma.react.common.Css

object VisualizationStyles:
  val VisualizationTooltip: Css       = Css("visualization-tooltip")
  val VisualizationTooltipTarget: Css = Css("visualization-tooltip-target")
  val CircleTarget: Css               = Css("circle-target")
  val CrosshairTarget                 = Css("crosshair-target")
  val ArrowBetweenTargets             = Css("arrow-between-targets")
  val OffsetPosition                  = Css("offset-position")
  val GuideStarCandidateTarget        = Css("guide-star-candidate-target")
  val GuideStarTarget                 = Css("guide-star-target")
  val GuideStarCandidateVisible       = Css("guide-star-candidate-target-visible")

  val AgsFast: Css    = Css("ags-fast-color")
  val AgsMedium: Css  = Css("ags-medium-color")
  val AgsSlow: Css    = Css("ags-slow-color")
  val AgsTooltip: Css = Css("ags-tooltip")

  val TargetsSvg = Css("targets-overlay-svg")
  val JtsTargets = Css("overlay-all-targets")

  val JtsGuides        = Css("viz-guides")
  val JtsPolygon       = Css("viz-polygon")
  val JtsCollection    = Css("viz-collecttion")
  val VisualizationSvg = Css("visualization-overlay-svg")

  // GMOS-specific styles
  val GmosScienceCcd: Css            = Css("gmos-science-ccd")
  val GmosFpu: Css                   = Css("gmos-fpu")
  val GmosPatrolField: Css           = Css("gmos-patrol-field")
  val PatrolFieldIntersection: Css   = Css("patrol-field-intersection")
  val GmosCandidatesArea: Css        = Css("gmos-candidates-area")
  val GmosProbeArm: Css              = Css("gmos-probe-arm")
  val GmosFpuVisible                 = Css("gmos-fpu-visible")
  val GmosCcdVisible                 = Css("gmos-ccd-visible")
  val GmosCandidatesAreaVisible: Css = Css("gmos-candidates-area-visible")
  val GmosPatrolFieldVisible: Css    = Css("gmos-patrol-field-visible")
  val GmosProbeVisible: Css          = Css("gmos-probe-visible")

  // Flamingos2-specific styles
  val F2ScienceArea: Css        = Css("f2-science-area")
  val F2Fpu: Css                = Css("f2-fpu")
  val F2FpuVisible: Css         = Css("f2-fpu-visible")
  val F2ScienceAreaVisible: Css = Css("f2-science-area-visible")
  val F2CandidatesArea: Css     = Css("f2-candidates-area")
