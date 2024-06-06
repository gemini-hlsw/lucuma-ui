// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import cats.effect.IO
import cats.effect.Temporal
import cats.syntax.all.*
import crystal.react.hooks.*
import crystal.react.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import org.http4s.Method
import org.http4s.Request
import org.http4s.Uri
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.*
import scala.concurrent.duration.FiniteDuration

/**
 * A component that renders a link if the link is valid, otherwise it just renders the children.
 */
case class LinkIfValid(href: String, mods: TagMod*)(val children: VdomNode*)(using
  val client: Client[IO],
  val logger: Logger[IO],
  val F:      Temporal[IO]
) extends ReactFnProps(LinkIfValid.component)

object LinkIfValid:
  private type Props = LinkIfValid

  private val PollInterval: FiniteDuration = 5.seconds

  private val ProxyUri: String = "https://cors-proxy.lucuma.xyz" // Avoid CORS

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .useState(false) // isValid
      .useEffectStreamWithDepsBy((_, isValid) => isValid.value): (props, isValid) =>
        isValudValue =>
          import props.F

          Option
            .when(!isValudValue):
              Uri
                .fromString(s"$ProxyUri/${props.href}")
                .bimap(
                  parseError =>
                    fs2.Stream.eval(props.logger.error(s"Error parsing URI: $parseError")),
                  uri =>
                    fs2.Stream
                      .eval:
                        props.logger.trace(s"Checking URI: $uri") >>
                          props.client
                            .successful(Request[IO](Method.HEAD, uri))
                            .flatMap: valid => // If valid is true, this will cancel the stream
                              props.logger.trace(s"Checked URI: $uri, isValid: $valid") >>
                                isValid.setStateAsync(valid)
                      .repeat
                      .spaced[IO](PollInterval, startImmediately = true)
                )
                .bisequence
                .void
            .orEmpty
      .render: (props, isValid) =>
        if isValid.value
        then <.a(^.href := props.href)(props.mods*)(React.Fragment(props.children*))
        else React.Fragment(props.children*)
