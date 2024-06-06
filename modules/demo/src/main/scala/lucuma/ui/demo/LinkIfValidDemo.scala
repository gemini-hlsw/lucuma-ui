// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.effect.IO
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import log4cats.loglevel.LogLevelLogger
import lucuma.ui.components.LinkIfValid
import org.http4s.*
import org.http4s.client.Client
import org.http4s.dom.FetchClientBuilder
import org.scalajs.dom
import org.typelevel.log4cats.Logger
import typings.loglevel.mod.LogLevelDesc

import scala.concurrent.duration.*

object LinkIfValidDemo:
  private given client: Client[IO] = FetchClientBuilder[IO]
    .withRequestTimeout(2.seconds)
    .withCache(dom.RequestCache.`no-store`)
    .create

  LogLevelLogger.setLevel(LogLevelDesc.TRACE)
  private given Logger[IO] = LogLevelLogger.createForRoot[IO]

  val component = ScalaFnComponent[Unit]: _ =>
    <.div(
      <.h2("LinkIfValid!"),
      LinkIfValid("https://google.com", ^.target := "_blank")(
        "Google"
      ),
      <.br,
      LinkIfValid("https://elgoog.com", ^.target := "_blank")(
        "Elgoog"
      )
    )
