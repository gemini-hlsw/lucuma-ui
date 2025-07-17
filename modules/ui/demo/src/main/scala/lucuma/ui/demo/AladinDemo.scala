// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.effect.*
import demo.TargetBody
import japgolly.scalajs.react.React
import japgolly.scalajs.react.ReactDOMClient
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.ui.syntax.all.given
import org.scalajs.dom

import scala.scalajs.js.annotation.*

trait AladinAppMain extends IOApp.Simple {
  protected def rootComponent: VdomElement

  @JSExport
  def runIOApp(): Unit = main(Array.empty)

  override final def run: IO[Unit] = IO {
    ReusabilityOverlay.overrideGloballyInDev()

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    ReactDOMClient.createRoot(container).render(React.StrictMode(rootComponent))
  }
}

@JSExportTopLevel("AladinDemo")
object AladinDemo extends AladinAppMain {
  override protected val rootComponent: VdomElement =
    <.div(
      <.h1("Aladin Demo"),
      TargetBody()
    )
}
