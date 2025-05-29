// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso

import cats.Eq
import cats.Show
import cats.derived.*
import io.circe.*
import org.http4s.Uri
import org.http4s.circe.*

import java.time.Duration
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

case class SSOConfig(
  uri:                           Uri,
  readTimeoutSeconds:            Long = 3,
  expirationAnticipationSeconds: Long = 10 // time before expiration to renew
) derives Eq,
      Show,
      Decoder:
  val readTimeout: FiniteDuration      = FiniteDuration(readTimeoutSeconds, TimeUnit.SECONDS)
  val expirationAnticipation: Duration = Duration.ofSeconds(expirationAnticipationSeconds)
