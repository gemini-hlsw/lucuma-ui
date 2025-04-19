// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.effect.Resource
import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import io.circe.Json
import io.circe.syntax.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnPropsWithChildren
import lucuma.ui.sso.UserVault
import lucuma.ui.syntax.all.*
import org.http4s.headers.Authorization
import org.typelevel.log4cats.Logger

case class ConnectionManager(
  vault:            UserVault,
  openConnections:  DefaultA[Map[String, Json]] => DefaultA[Unit],
  closeConnections: DefaultA[Unit],
  onConnect:        DefaultA[Unit]
)(using
  val logger:       Logger[DefaultA]
) extends ReactFnPropsWithChildren(ConnectionManager.component):
  val payload: Map[String, Json] = Map(
    Authorization.name.toString -> vault.authorizationHeader.credentials.renderString.asJson
  )

object ConnectionManager:
  private type Props = ConnectionManager

  private val component = ScalaFnComponent
    .withHooks[Props]
    .withPropsChildren
    .useShadowRef(pc => pc.props.payload)
    .useResourceOnMountBy: (props, _, payloadRef) => // Returns Pot[Unit]; Ready when connected.
      Resource.make(props.openConnections(payloadRef.getAsync))(_ => props.closeConnections) >>
        Resource.eval(props.onConnect)
    .render: (_, children, _, connectedPot) =>
      connectedPot.renderPot(_ => children)
