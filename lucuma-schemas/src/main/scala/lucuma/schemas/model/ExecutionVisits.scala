// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

trait ExecutionVisits:
  def visits: List[Visit]

object ExecutionVisits:
  case class GmosNorth(visits: List[Visit.GmosNorth]) extends ExecutionVisits
  case class GmosSouth(visits: List[Visit.GmosSouth]) extends ExecutionVisits
