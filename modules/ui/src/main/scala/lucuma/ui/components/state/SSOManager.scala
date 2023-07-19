// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.effect.IO
import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.{Async => DefaultA}
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.refined.*
import lucuma.ui.sso.SSOClient
import lucuma.ui.sso.UserVault
import org.typelevel.log4cats.Logger
import react.common.ReactFnProps

import java.time.Instant

case class SSOManager(
  ssoClient:  SSOClient[DefaultA],
  expiration: Instant,
  setVault:   Option[UserVault] => DefaultA[Unit],
  setMessage: NonEmptyString => DefaultA[Unit]
)(using val logger: Logger[DefaultA])
    extends ReactFnProps(SSOManager.component)

object SSOManager:
  private type Props = SSOManager

  private def tokenRefresher(
    expiration: Instant,
    setVault:   Option[UserVault] => DefaultA[Unit],
    setMessage: NonEmptyString => DefaultA[Unit],
    ssoClient:  SSOClient[DefaultA]
  ): DefaultA[Unit] =
    for {
      vaultOpt <- ssoClient.refreshToken(expiration)
      _        <- setVault(vaultOpt)
      _        <- vaultOpt.fold(setMessage("Your session has expired".refined))(vault =>
                    tokenRefresher(vault.expiration, setVault, setMessage, ssoClient)
                  )
    } yield ()

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .useRef(none[DefaultA[Unit]]) // cancelToken
      .useAsyncEffectOnMountBy { (props, cancelToken) =>
        import props.given

        tokenRefresher(props.expiration, props.setVault, props.setMessage, props.ssoClient)
          .onError(t =>
            Logger[DefaultA].error(t)("Error refreshing SSO token") >>
              (props.setVault(none) >>
                props.setMessage(
                  "There was an error while checking the validity of your session".refined
                ))
          )
          .start
          .flatMap(fiber => cancelToken.setAsync(fiber.cancel.some))
          .as(
            cancelToken.getAsync >>=
              (cancelOpt => cancelOpt.foldMap(_ >> props.setVault(none)))
          )
      }
      .render((_, _) => EmptyVdom) // This is a "phantom" component. Doesn't render anything.
