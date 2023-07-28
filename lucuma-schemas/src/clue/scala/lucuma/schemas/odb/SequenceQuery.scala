// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLOperation
import lucuma.core.model.sequence.*
import lucuma.schemas.ObservationDB
// gql: import lucuma.odb.json.sequence.given

object SequenceSQL:
  @clue.annotation.GraphQL
  trait SequenceQuery extends GraphQLOperation[ObservationDB]:
    val document = s"""
        query($$obsId: ObservationId!) {
          observation(observationId: $$obsId) {
            execution {
              config(futureLimit: 100) {
                instrument
                ... on GmosNorthExecutionConfig {
                  static {
                    stageMode
                    detector
                    mosPreImaging
                    nodAndShuffle {
                      ...nodAndShuffleFields
                    }
                  }
                  acquisition {
                    ...gmosNorthSequenceFields
                  }
                  science {
                    ...gmosNorthSequenceFields
                  }
                }
                ... on GmosSouthExecutionConfig {
                  static {
                    stageMode
                    detector
                    mosPreImaging
                    nodAndShuffle {
                      ...nodAndShuffleFields
                    }
                  }
                  acquisition {
                    ...gmosSouthSequenceFields
                  }
                  science {
                    ...gmosSouthSequenceFields
                  }
                }
              }
            }
          }
        }

        fragment nodAndShuffleFields on GmosNodAndShuffle {
          posA { ...offsetFields }
          posB { ...offsetFields }
          eOffset
          shuffleOffset
          shuffleCycles
        }

        fragment stepConfigFields on StepConfig {
          stepType
          ... on Gcal {
            continuum
            arcs
            filter
            diffuser
            shutter
          }
          ... on Science {
            offset { ...offsetFields }
            guiding
          }
          ... on SmartGcal {
            smartGcalType
          }
        }

        fragment stepEstimateFields on StepEstimate {
          configChange {
            all {
              name
              description
              estimate { microseconds }
            }
            index
          }
          detector {
            all {
              name
              description
              dataset {
                exposure { microseconds }
                readout { microseconds }
                write { microseconds }
              }
              count
            }
            index
          }
        }

        fragment gmosNorthAtomFields on GmosNorthAtom {
          id
          description
          steps {
            id
            instrumentConfig {
              exposure { microseconds }
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
                wavelength { picometers }
              }
              filter
              fpu {
                builtin
              }
            }
            stepConfig {
              ...stepConfigFields
            }
            estimate {
              ...stepEstimateFields
            }
            observeClass
            breakpoint
          }
        }

        fragment gmosNorthSequenceFields on GmosNorthExecutionSequence {
          nextAtom {
            ...gmosNorthAtomFields
          }
          possibleFuture {
            ...gmosNorthAtomFields
          }
          hasMore
        }

        fragment gmosSouthAtomFields on GmosSouthAtom {
          id
          description
          steps {
            id
            instrumentConfig {
              exposure { microseconds }
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
                wavelength { picometers }
              }
              filter
              fpu {
                builtin
              }
            }
            stepConfig {
              ...stepConfigFields
            }
            estimate {
              ...stepEstimateFields
            }
            observeClass
            breakpoint
          }
        }

        fragment gmosSouthSequenceFields on GmosSouthExecutionSequence {
          nextAtom {
            ...gmosSouthAtomFields
          }
          possibleFuture {
            ...gmosSouthAtomFields
          }
          hasMore
        }

        fragment offsetFields on Offset {
          p { microarcseconds }
          q { microarcseconds }
        }
      """

    object Data:
      object Observation:
        object Execution:
          type Config = InstrumentExecutionConfig

  @clue.annotation.GraphQL
  trait DigestQuery extends GraphQLOperation[ObservationDB]:
    val document = s"""
        query($$obsId: ObservationId!) {
          observation(observationId: $$obsId) {
            execution {
              digest {
                setup {
                  ...setupTimeFields
                }
                acquisition {
                  ...sequenceDigestFields
                }
                science {
                  ...sequenceDigestFields
                }
              }
            }
          }
        }

        fragment setupTimeFields on SetupTime {
          full { microseconds }
          reacquisition { microseconds }
        }

        fragment sequenceDigestFields on SequenceDigest {
          observeClass
          plannedTime {
            charges {
              chargeClass
              time { microseconds }
            }
          }
          offsets { ...offsetFields }
          atomCount
        }

        fragment offsetFields on Offset {
          p { microarcseconds }
          q { microarcseconds }
        }
      """

    object Data:
      object Observation:
        object Execution:
          type Digest = ExecutionDigest
