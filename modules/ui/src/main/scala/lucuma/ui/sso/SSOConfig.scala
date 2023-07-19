// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso

import cats.Eq
import cats.Show
import cats.derived.*
import cats.syntax.all.*
import io.circe.*
import org.http4s.Uri
import org.http4s.circe.*

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

case class SSOConfig(
  uri:                        Uri,
  readTimeoutSeconds:         Long = 3,
  refreshTimeoutDeltaSeconds: Long = 10, // time before expiration to renew
  refreshIntervalFactor:      Long = 1
) derives Eq,
      Show,
      Decoder:
  val readTimeout: FiniteDuration         = FiniteDuration(readTimeoutSeconds, TimeUnit.SECONDS)
  val refreshTimeoutDelta: FiniteDuration =
    FiniteDuration(refreshTimeoutDeltaSeconds, TimeUnit.SECONDS)
