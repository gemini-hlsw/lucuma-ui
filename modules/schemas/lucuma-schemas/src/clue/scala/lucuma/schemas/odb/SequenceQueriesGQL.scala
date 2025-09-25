// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLOperation
import clue.annotation.GraphQL
import lucuma.core.model.sequence.*
import lucuma.schemas.ObservationDB
// gql: import lucuma.odb.json.sequence.given

object SequenceQueriesGQL:
  @GraphQL
  trait SequenceQuery extends GraphQLOperation[ObservationDB]:
    val document = s"""
        query($$obsId: ObservationId!, $$includeItc: Boolean = true) {
          observation(observationId: $$obsId) @include(if: $$includeItc) {
            itc { ...itcFields }
          }

          executionConfig(observationId: $$obsId, futureLimit: 100) {
            instrument
            gmosNorth {
              static {
                stageMode
                detector
                mosPreImaging
                nodAndShuffle { ...nodAndShuffleFields }
              }
              acquisition { ...gmosNorthSequenceFields }
              science { ...gmosNorthSequenceFields }
            }
            gmosSouth {
              static {
                stageMode
                detector
                mosPreImaging
                nodAndShuffle { ...nodAndShuffleFields }
              }
              acquisition { ...gmosSouthSequenceFields }
              science { ...gmosSouthSequenceFields }
            }
            flamingos2 {
              static {
                mosPreImaging
                useElectronicOffsetting
              }
              acquisition { ...flamingos2SequenceFields }
              science { ...flamingos2SequenceFields }
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
            instrumentConfig $GmosNorthDynamicConfigSubquery
            stepConfig { ...stepConfigFields }
            telescopeConfig {
              offset { ...offsetFields }
              guiding
            }
            estimate { ...stepEstimateFields }
            observeClass
            breakpoint
          }
        }

        fragment gmosNorthSequenceFields on GmosNorthExecutionSequence {
          nextAtom { ...gmosNorthAtomFields }
          possibleFuture { ...gmosNorthAtomFields }
          hasMore
        }

        fragment gmosSouthAtomFields on GmosSouthAtom {
          id
          description
          steps {
            id
            instrumentConfig $GmosSouthDynamicConfigSubquery
            stepConfig { ...stepConfigFields }
            telescopeConfig {
              offset { ...offsetFields }
              guiding
            }
            estimate { ...stepEstimateFields }
            observeClass
            breakpoint
          }
        }

        fragment gmosSouthSequenceFields on GmosSouthExecutionSequence {
          nextAtom { ...gmosSouthAtomFields }
          possibleFuture { ...gmosSouthAtomFields }
          hasMore
        }

        fragment flamingos2AtomFields on Flamingos2Atom {
          id
          description
          steps {
            id
            instrumentConfig $Flamingos2DynamicConfigSubquery
            stepConfig { ...stepConfigFields }
            telescopeConfig {
              offset { ...offsetFields }
              guiding
            }
            estimate { ...stepEstimateFields }
            observeClass
            breakpoint
          }
        }

        fragment flamingos2SequenceFields on Flamingos2ExecutionSequence {
          nextAtom { ...flamingos2AtomFields }
          possibleFuture { ...flamingos2AtomFields }
          hasMore
        }

        fragment offsetFields on Offset {
          p { microarcseconds }
          q { microarcseconds }
        }

        fragment itcFields on Itc {
          acquisition {
            selected {
              signalToNoiseAt {
                single
                total
              }
            }
          }
          science {
            selected {
              signalToNoiseAt {
                single
                total
              }
            }
          }
        }
      """

    object Data:
      type ExecutionConfig = InstrumentExecutionConfig
