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

  val ScienceOffsetPosition     = Css("science-offset-position")
  val AcquisitionOffsetPosition = Css("acquisition-offset-position")

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
  val Flamingos2ScienceArea: Css           = Css("flamingos2-science-ccd")
  val Flamingos2ScienceAreaVisible: Css    = Css("flamingos2-science-ccd-visible")
  val Flamingos2Fpu: Css                   = Css("flamingos2-fpu")
  val Flamingos2FpuVisible: Css            = Css("flamingos2-fpu-visible")
  val Flamingos2CandidatesArea: Css        = Css("flamingos2-candidates-area")
  val Flamingos2CandidatesAreaVisible: Css = Css("flamingos2-candidates-area-visible")
  val Flamingos2ProbeArm: Css              = Css("flamingos2-probe-arm")
  val Flamingos2ProbeArmVisible: Css       = Css("flamingos2-probe-arm-visible")
  val Flamingos2PatrolField: Css           = Css("flamingos2-patrol-field")
  val Flamingos2PatrolFieldVisible: Css    = Css("flamingos2-patrol-field-visible")
