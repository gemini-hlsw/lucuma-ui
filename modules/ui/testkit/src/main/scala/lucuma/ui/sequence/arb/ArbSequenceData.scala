// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence.arb

import lucuma.core.enums.SequenceType
import lucuma.core.math.SingleSN
import lucuma.core.math.TotalSN
import lucuma.core.math.arb.ArbSignalToNoise.given
import lucuma.core.model.sequence.InstrumentExecutionConfig
import lucuma.core.model.sequence.arb.ArbInstrumentExecutionConfig.given
import lucuma.core.util.arb.ArbEnumerated.given
import lucuma.core.util.arb.ArbNewType.given
import lucuma.ui.sequence.SequenceData
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Cogen

trait ArbSequenceData:
  given Arbitrary[SequenceData] = Arbitrary:
    for
      config     <- arbitrary[InstrumentExecutionConfig]
      snPerClass <- arbitrary[Map[SequenceType, (SingleSN, TotalSN)]]
    yield SequenceData(config, snPerClass)

  given Cogen[SequenceData] =
    Cogen[(InstrumentExecutionConfig, List[(SequenceType, (SingleSN, TotalSN))])].contramap: sd =>
      (sd.config, sd.snPerClass.toList)

object ArbSequenceData extends ArbSequenceData
