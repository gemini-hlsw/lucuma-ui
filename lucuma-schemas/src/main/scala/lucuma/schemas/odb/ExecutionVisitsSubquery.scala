// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given
import lucuma.schemas.model.ExecutionVisits

object ExecutionVisitsSubquery
    extends GraphQLSubquery.Typed[ObservationDB, ExecutionVisits]("Execution"):
  override val subquery: String = s"""
    {
      config {
        instrument
        ... on GmosNorthExecutionConfig {
          ...gmosNorthStaticConfigFields
        }
        ... on GmosSouthExecutionConfig {
          ...gmosSouthStaticConfigFields
        }
      }
      visits {
        matches {
          id
          instrument
          created
          interval $TimestampIntervalSubquery
          atomRecords {
            matches {
              id
              created
              interval $TimestampIntervalSubquery
              sequenceType
              steps {
                matches {
                  id
                  created
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
                      interval $TimestampIntervalSubquery
                    }
                  }
                  ... on GmosNorthStepRecord {
                    instrumentConfig {
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
    }  
  """

  val Fragments = s"""
    fragment nodAndShuffleFields on GmosNodAndShuffle {
      posA $OffsetSubquery
      posB $OffsetSubquery
      eOffset
      shuffleOffset
      shuffleCycles
    }

    fragment gmosNorthStaticConfigFields on GmosNorthExecutionConfig {
      static {
        stageMode
        detector
        mosPreImaging
        nodAndShuffle {
          ...nodAndShuffleFields
        }
      }
    }

    fragment gmosSouthStaticConfigFields on GmosSouthExecutionConfig {
      static {
        stageMode
        detector
        mosPreImaging
        nodAndShuffle {
          ...nodAndShuffleFields
        }
      }
    }
  """
