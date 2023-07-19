// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components.state

import cats.Applicative
import cats.effect.Sync
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.{Async => DefaultA}
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.broadcastchannel.*
import lucuma.refined.*
import lucuma.ui.sso.UserVault
import react.common.ReactFnProps

case class LogoutTracker[E](
  setVault:             Option[UserVault] => DefaultA[Unit],
  setMessage:           NonEmptyString => DefaultA[Unit],
  channelName:          NonEmptyString,
  isLogoutEvent:        E => Boolean,
  getEventNonce:        E => String,
  createEventWithNonce: String => E
)(val render: DefaultA[Unit] => VdomNode)
    extends ReactFnProps(LogoutTracker.component)

object LogoutTracker:
  private type Props[E] = LogoutTracker[E]

  private def componentBuilder[E] =
    ScalaFnComponent
      .withHooks[Props[E]]
      // Create a nonce
      .useMemo(())(_ => System.currentTimeMillis)
      // Hold the broadcast channel
      .useState(none[BroadcastChannel[E]])
      .useEffectOnMountBy { (props, nonce, state) =>
        val bc = new BroadcastChannel[E](props.channelName.value)

        bc.onmessage = (
          (e: E) =>
            if (props.isLogoutEvent(e))
              (props.setVault(none) >> props.setMessage(
                "You logged out in another instance".refined
              )).whenA(props.getEventNonce(e) =!= nonce.value.toString)
            else
              Applicative[DefaultA].unit
        ): (E => DefaultA[Unit]) // Scala 3 infers the return type as Any if we don't ascribe

        state
          .setState(bc.some) *> CallbackTo(Callback(bc.close()).attempt)
      }
      .render { (props, nonce, bc) =>
        bc.value.fold[VdomNode](React.Fragment())(bc =>
          props.render(
            Sync[DefaultA]
              .delay(
                bc.postMessage(
                  props.createEventWithNonce(nonce.value.toString)
                )
              )
              .attempt
              .void
          )
        )
      }

  private val component = componentBuilder[Any]
