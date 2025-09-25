// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.Reusability
import lucuma.core.enums.SequenceType
import lucuma.core.math.SingleSN
import lucuma.core.math.TotalSN
import lucuma.core.model.sequence.InstrumentExecutionConfig
import lucuma.schemas.odb.SequenceQueriesGQL.SequenceQuery
import monocle.Focus
import monocle.Lens

/**
 * Bundles the execution configuration and the signal-to-noise ratio for each sequence type.
 */
case class SequenceData(
  config:     InstrumentExecutionConfig,
  snPerClass: Map[SequenceType, (SingleSN, TotalSN)]
) derives Eq

object SequenceData:
  private def buildSnMap(
    itc: SequenceQuery.Data.Observation.Itc
  ): Map[SequenceType, (SingleSN, TotalSN)] =
    val acq: Option[(SequenceType, (SingleSN, TotalSN))] =
      (itc.acquisition.selected.signalToNoiseAt.map(_.single),
       itc.acquisition.selected.signalToNoiseAt.map(_.total)
      ).mapN: (s, t) =>
        SequenceType.Acquisition -> (SingleSN(s), TotalSN(t))

    val sci: Option[(SequenceType, (SingleSN, TotalSN))] =
      (itc.science.selected.signalToNoiseAt.map(_.single),
       itc.science.selected.signalToNoiseAt.map(_.total)
      ).mapN: (s, t) =>
        SequenceType.Science -> (SingleSN(s), TotalSN(t))
    List(acq, sci).flattenOption.toMap

  def fromOdbResponse(data: SequenceQuery.Data): Option[SequenceData] =
    data.executionConfig.map: config =>
      val snMap = data.observation.map(obs => buildSnMap(obs.itc)).getOrElse(Map.empty)
      SequenceData(config, snMap)

  val config: Lens[SequenceData, InstrumentExecutionConfig]                  = Focus[SequenceData](_.config)
  val snPerClass: Lens[SequenceData, Map[SequenceType, (SingleSN, TotalSN)]] =
    Focus[SequenceData](_.snPerClass)

  given Reusability[SequenceData] = Reusability.byEq
