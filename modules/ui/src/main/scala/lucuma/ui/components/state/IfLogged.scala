// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.syntax.all.*
import crystal.react.View
import crystal.react.syntax.view.*
import eu.timepit.refined.types.string.NonEmptyString
import fs2.dom.Serializer
import io.circe.Json
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.ui.components.UserSelectionForm
import lucuma.ui.sso.SSOClient
import lucuma.ui.sso.UserVault
import org.typelevel.log4cats.Logger

case class IfLogged[E](
  systemName:           NonEmptyString,
  systemNameStyle:      Css,
  allowGuest:           Boolean,
  ssoClient:            SSOClient[DefaultA],
  userVault:            View[Option[UserVault]],
  userSelectionMessage: View[Option[NonEmptyString]],
  openConnections:      DefaultA[Map[String, Json]] => DefaultA[Unit],
  closeConnections:     DefaultA[Unit],
  onConnect:            DefaultA[Unit],
  channelName:          NonEmptyString,
  isLogoutEvent:        E => Boolean,
  getEventNonce:        E => String,
  createEventWithNonce: String => E
)(
  val render:           DefaultA[Unit] => VdomNode
)(using
  val logger:           Logger[DefaultA],
  val serializerE:      Serializer[E]
) extends ReactFnProps(IfLogged.component)

object IfLogged:
  private type Props[E] = IfLogged[E]

  private def componentBuilder[E] =
    ScalaFnComponent[Props[E]]: props =>
      import props.given

      val vaultSet   = props.userVault.async.set
      val messageSet = props.userSelectionMessage.async.set.compose((_: NonEmptyString).some)

      props.userVault.get.fold[VdomElement](
        UserSelectionForm(
          props.systemName,
          props.systemNameStyle,
          props.ssoClient,
          props.userVault,
          props.userSelectionMessage,
          props.allowGuest
        )
      ): vault =>
        React.Fragment(
          SSOManager(props.ssoClient, vault.expiration, vaultSet, messageSet),
          ConnectionManager(
            vault,
            props.openConnections,
            props.closeConnections,
            props.onConnect
          ).withKey(s"${vault.user.id}-${vault.user.role.toString}")(
            LogoutTracker(
              vaultSet,
              messageSet,
              props.channelName,
              props.isLogoutEvent,
              props.getEventNonce,
              props.createEventWithNonce
            )(props.render(_))
          )
        )

  private val component = componentBuilder[Any]
