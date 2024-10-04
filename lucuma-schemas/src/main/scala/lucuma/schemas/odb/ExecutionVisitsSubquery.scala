// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.ExecutionVisits

object ExecutionVisitsSubquery
    extends GraphQLSubquery.Typed[ObservationDB, Option[ExecutionVisits]]("Execution"):
  override val subquery: String = s"""
    {
      visits(OFFSET: $$visitIdOffset) {
        matches {
          id
          instrument
          created
          interval $TimestampIntervalSubquery
          atomRecords {
            matches {
              id
              created
              executionState
              interval $TimestampIntervalSubquery
              sequenceType
              generatedId
              steps {
                matches {
                  id
                  created
                  executionState
                  interval $TimestampIntervalSubquery
                  stepConfig {
                    stepType
                    ... on Gcal {
                      continuum
                      arcs
                      filter
                      diffuser
                      shutter
                    }
                    ... on Science {
                      offset $OffsetSubquery
                      guiding
                    }
                  }
                  observeClass
                  qaState
                  datasets {
                    matches {
                      id
                      index
                      filename
                      qaState
                      comment
                      interval $TimestampIntervalSubquery
                    }
                  }
                  generatedId
                  gmosNorth {
                    exposure $TimeSpanSubquery
                    readout {
                      xBin
                      yBin
                      ampCount
                      ampGain
                      ampReadMode
                    }
                    dtax
                    roi
                    gratingConfig {
                      grating
                      order
                      wavelength $WavelengthSubquery
                    }
                    filter
                    fpu {
                      customMask {
                        filename
                        slitWidth
                      }
                      builtin
                    }
                  }
                  gmosSouth {
                    exposure $TimeSpanSubquery
                    readout {
                      xBin
                      yBin
                      ampCount
                      ampGain
                      ampReadMode
                    }
                    dtax
                    roi
                    gratingConfig {
                      grating
                      order
                      wavelength $WavelengthSubquery
                    }
                    filter
                    fpu {
                      customMask {
                        filename
                        slitWidth
                      }
                      builtin
                    }
                  }
                }
              }
            }
          }
        }
      }
    }  
  """

  // At some point we hope to support properly having fragments in subQueries, but currently these
  // fragments need to be redeclared where this subquery is used...
  val Fragments = s"""
    fragment nodAndShuffleFields on GmosNodAndShuffle {
      posA $OffsetSubquery
      posB $OffsetSubquery
      eOffset
      shuffleOffset
      shuffleCycles
    }
  """
