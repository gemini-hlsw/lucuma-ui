// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import scala.scalajs.js.annotation._

import react.common.ReactProps
import react.common.style.Css
import react.semanticui.elements.label.LabelPointing
import react.semanticui.collections.form.Form
import cats.syntax.all._
import cats.effect._
import crystal._
import crystal.react._
import crystal.react.implicits._
import japgolly.scalajs.react.Reusability._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.ui.forms._
import lucuma.ui.optics.ValidFormatInput
import monocle.macros.Lenses
import org.scalajs.dom
import japgolly.scalajs.react.MonocleReact._

final case class FormComponent(root: ViewF[IO, RootModel])
    extends ReactProps[FormComponent](FormComponent.component)

object FormComponent {
  type Props = FormComponent

  @Lenses
  case class State(valid1: Boolean = true, valid2: Boolean = true)

  implicit val propsReuse = Reusability.derive[Props]
  implicit val stateReuse = Reusability.derive[State]

  val component =
    ScalaComponent
      .builder[Props]
      .initialState(State())
      .render { $ =>
        <.div(^.paddingTop := "20px")(
          s"MODEL: ${$.props.root.get}",
          <.br,
          s"STATE: ${$.state}",
          Form(
            FormInputEV(
              id = "field1",
              value = $.props.root.zoom(RootModel.field1),
              validFormat = ValidFormatInput(
                s =>
                  if (s.isEmpty)
                    "Can't be empty".invalidNec
                  else
                    s.toUpperCase.validNec,
                identity[String]
              ),
              onValidChange = v => $.setStateL(State.valid1)(v)
            ),
            FormInputEV(
              id = "field2",
              value = $.props.root.zoom(RootModel.field2),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput(
                s =>
                  if (s.isEmpty)
                    "Can't be empty".invalidNec
                  else
                    s.toLowerCase.validNec,
                identity[String]
              ),
              onValidChange = v => $.setStateL(State.valid2)(v)
            )
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build

}

@Lenses
final case class RootModel(field1: String, field2: String)

object RootModel {
  implicit val modelReusability: Reusability[RootModel] = Reusability.derive[RootModel]
}

case class AppContext[F[_]]()(implicit val cs: ContextShift[F])

object AppCtx extends AppRootContext[AppContext[IO]]

trait AppMain extends IOApp {

  protected def rootComponent(
    view: ViewF[IO, RootModel]
  ): VdomElement

  @JSExport
  def runIOApp(): Unit = main(Array.empty)

  override final def run(args: List[String]): IO[ExitCode] = {
    ReusabilityOverlay.overrideGloballyInDev()

    val initialModel = RootModel("FIELD1", "")

    for {
      _ <- AppCtx.initIn[IO](AppContext[IO]())
    } yield {
      val RootComponent = AppRoot[IO](initialModel)(rootComponent, None)

      val container = Option(dom.document.getElementById("root")).getOrElse {
        val elem = dom.document.createElement("div")
        elem.id = "root"
        dom.document.body.appendChild(elem)
        elem
      }

      RootComponent().renderIntoDOM(container)

      ExitCode.Success
    }
  }
}

@JSExportTopLevel("Demo")
object Demo extends AppMain {
  override protected def rootComponent(rootView: ViewF[IO, RootModel]): VdomElement =
    FormComponent(rootView)
}
