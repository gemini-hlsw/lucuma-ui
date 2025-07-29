// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.effect.Async
import cats.effect.Sync
import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnProps
import lucuma.refined.*
import lucuma.ui.reusability.given
import lucuma.ui.sso.SSOClient
import lucuma.ui.sso.UserVault
import org.typelevel.log4cats.Logger

import java.time.Instant
import scala.concurrent.duration.*

case class SSOManager(
  ssoClient:  SSOClient[DefaultA],
  expiration: Instant,
  setVault:   Option[UserVault] => DefaultA[Unit],
  setMessage: NonEmptyString => DefaultA[Unit]
)(using val F: Async[DefaultA], val logger: Logger[DefaultA])
    extends ReactFnProps(SSOManager.component)

object SSOManager:
  private type Props = SSOManager

  // We check the expiration periodically instead of using a timeout to the next expiration.
  // This ensures that we resync again if the computer goes to sleep.
  private val ExpirationCheckInterval: FiniteDuration = 1.second

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .useEffectStreamWithDepsBy(props => props.expiration): props =>
        expiration =>
          import props.given

          val refreshInstant: Instant =
            expiration.minus(props.ssoClient.config.expirationAnticipation)

          fs2.Stream.eval:
            Logger[DefaultA].debug(s"Next token refresh: $refreshInstant")
          ++ fs2.Stream
            .awakeDelay(ExpirationCheckInterval)
            .evalMap(_ => Sync[DefaultA].delay(Instant.now))
            .takeThrough(_.isBefore(refreshInstant)) // When the stream ends, refresh the token.
            .void ++
            fs2.Stream.eval:
              (for
                _        <- Logger[DefaultA].debug("Refreshing user token")
                vaultOpt <- props.ssoClient.whoami
                _        <- Logger[DefaultA].debug:
                              s"User token refreshed. New expiration: ${vaultOpt.map(_.expiration)}."
                _        <- props.setVault(vaultOpt)
                _        <- props.setMessage("Your session has expired".refined).whenA(vaultOpt.isEmpty)
              yield ())
                .onError:
                  case t =>
                    Logger[DefaultA].error(t)("Error refreshing user token") >> props.setVault(none)
      .render(_ => EmptyVdom) // This is a "phantom" component. Doesn't render anything.
