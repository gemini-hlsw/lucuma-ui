// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import lucuma.core.model.TimingWindow
import lucuma.schemas.ObservationDB
import lucuma.schemas.decoders.given

object TimingWindowSubquery
    extends GraphQLSubquery.Typed[ObservationDB, TimingWindow]("TimingWindow"):
  override val subquery: String = s"""
        {
          inclusion
          startUtc
          end {
            ... on TimingWindowEndAt {
              atUtc
            }
            ... on TimingWindowEndAfter {
              after {
                milliseconds
              }         
              repeat {
                period {
                  milliseconds
                }
                times
              }
            }
          }
        }
      """
