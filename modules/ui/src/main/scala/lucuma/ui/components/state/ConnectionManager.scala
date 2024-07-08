// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import io.circe.Json
import io.circe.syntax.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnPropsWithChildren
import lucuma.ui.components.SolarProgress
import lucuma.ui.reusability.given
import lucuma.ui.sso.UserVault
import lucuma.ui.syntax.all.given
import org.typelevel.log4cats.Logger

case class ConnectionManager(
  vault:            UserVault,
  openConnections:  Map[String, Json] => DefaultA[Unit],
  closeConnections: DefaultA[Unit],
  onConnect:        DefaultA[Unit]
)(using
  val logger:       Logger[DefaultA]
) extends ReactFnPropsWithChildren(ConnectionManager.component):
  val payload: Map[String, Json] = Map("Authorization" -> vault.authorizationHeader.asJson)

object ConnectionManager {
  private type Props = ConnectionManager

  private val component = ScalaFnComponent
    .withHooks[Props]
    .withPropsChildren
    .useState(false) // initialized as state, which forces rerender on set
    .useRef(false)   // initialized as ref, which can be read asynchronously by cleanup
    .useEffectWithDepsBy((props, _, _, _) => props.vault.token): (props, _, initializedState, _) =>
      _ =>
        import props.given

        // In clue, connect will be a no-op (with a warn) and initialize will restart the protocol and reestablish subscriptions.
        (Logger[DefaultA].debug(s"[ConnectionManager] Token changed. Refreshing connections.") >>
          props.openConnections(props.payload))
          .whenA(initializedState.value)
    .useAsyncEffectOnMountBy: (props, _, initializedState, initializedRef) =>
      import props.given

      val initialize: DefaultA[Unit] =
        props.openConnections(props.payload) >>
          initializedRef.setAsync(true) >>
          initializedState.setStateAsync(true) >>
          props.onConnect

      val cleanup: DefaultA[Unit] =
        initializedRef.getAsync >>= (initialized =>
          (Logger[DefaultA].debug(s"[ConnectionManager] Terminating connections.") >>
            props.closeConnections).whenA(initialized)
        )

      initialize.as(cleanup)
    .render: (props, children, initializedState, _) =>
      if (initializedState.value)
        children
      else
        SolarProgress()
}
