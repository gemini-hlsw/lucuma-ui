// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.Applicative
import cats.syntax.all.*
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import fs2.dom.BroadcastChannel
import fs2.dom.Serializer
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.ReactFnProps
import lucuma.refined.*
import lucuma.ui.sso.UserVault

case class LogoutTracker[E](
  setVault:             Option[UserVault] => DefaultA[Unit],
  setMessage:           NonEmptyString => DefaultA[Unit],
  channelName:          NonEmptyString,
  isLogoutEvent:        E => Boolean,
  getEventNonce:        E => String,
  createEventWithNonce: String => E
)(val render: DefaultA[Unit] => VdomNode)(using val serializerE: Serializer[E])
    extends ReactFnProps(LogoutTracker.component) {}

object LogoutTracker:
  private type Props[E] = LogoutTracker[E]

  private def componentBuilder[E] =
    ScalaFnComponent
      .withHooks[Props[E]]
      // Create a nonce
      .useMemo(())(_ => System.currentTimeMillis)
      .useResourceOnMountBy: (props, _) =>
        import props.given
        BroadcastChannel[DefaultA, E](props.channelName.value)
      .useStreamBy((_, _, bc) => bc.isReady): (props, nonce, bc) =>
        _ =>
          bc.toOption.map(_.messages).orEmpty.evalTap { e =>
            if (props.isLogoutEvent(e.data))
              (props.setVault(none) >> props.setMessage(
                "You logged out in another instance".refined
              )).whenA(props.getEventNonce(e.data) =!= nonce.value.toString)
            else
              Applicative[DefaultA].unit
          }
      .render: (props, nonce, bc, _) =>
        bc.toOption.fold[VdomNode](React.Fragment()): bc =>
          props.render:
            bc.postMessage:
              props.createEventWithNonce(nonce.value.toString)
            .attempt
              .void

  private val component = componentBuilder[Any]
