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
            sequence(futureLimit: 100) {
              executionConfig {
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
                  setup {
                    ...setupTimeFields
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
                  setup {
                    ...setupTimeFields
                  }
                }
              }
            }
          }
        }

        fragment setupTimeFields on SetupTime {
          full { microseconds }
          reacquisition { microseconds }
        }

        fragment nodAndShuffleFields on GmosNodAndShuffle {
          posA { ...offsetFields }
          posB { ...offsetFields }
          eOffset
          shuffleOffset
          shuffleCycles
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
          digest {
            ...sequenceDigestFields
          }
          nextAtom {
            ...gmosNorthAtomFields
          }
          possibleFuture {
            ...gmosNorthAtomFields
          }
          hasMore
          atomCount
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
          digest {
            ...sequenceDigestFields
          }
          nextAtom {
            ...gmosSouthAtomFields
          }
          possibleFuture {
            ...gmosSouthAtomFields
          }
          hasMore
          atomCount
        }

        fragment offsetFields on Offset {
          p { microarcseconds }
          q { microarcseconds }
        }
      """

    object Data:
      object Observation:
        object Sequence:
          type ExecutionConfig = InstrumentExecutionConfig
