// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import scala.scalajs.js.annotation._

import react.common.ReactProps
import react.semanticui.collections.form.Form
import cats.effect._
import crystal._
import crystal.react._
import crystal.react.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Interval
import japgolly.scalajs.react.Reusability._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math._
import lucuma.ui.forms._
import lucuma.ui.reusability._
import monocle.macros.Lenses
import org.scalajs.dom

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
      .builder[Props]("Home")
      .render_P { p =>
        <.div(
          Form(
            FormInputEV(id = "field1",
                        label = "Upper case string",
                        value = p.root.zoom(RootModel.field1),
                        validate = InputValidate.notEmpty,
                        changeAuditor = ChangeAuditor.upperCase
            ),
            <.div(p.root.get.field1, ^.padding := "0 0 1em 1em"),
            FormInputEV(
              id = "just",
              label = "Int max 1024 constrained",
              value = p.root.zoom(RootModel.justAnInt),
              validate = InputValidate.forInt,
              changeAuditor = ChangeAuditor.forNonNegInt(max = 1024)
            ),
            <.div(p.root.get.justAnInt, ^.padding := "0 0 1em 1em"),
            FormInputEV(
              id = "validated-int",
              label = "Int max 1024 post validated",
              value = p.root.zoom(RootModel.validatedInt),
              validate = InputValidate.forIntRange(0, 1024)
            ),
            <.div(p.root.get.validatedInt, ^.padding := "0 0 1em 1em"),
            FormInputEV(
              id = "refined",
              label = "Refined int 0 - 2048",
              value = p.root.zoom(RootModel.refinedInt),
              validate = InputValidate.forRefinedInt[Interval.Closed[0, 2048]],
              changeAuditor =
                ChangeAuditor.fromFormat(InputFormat.forRefinedInt[Interval.Closed[0, 2048]])
            ),
            <.div(p.root.get.refinedInt.value, ^.padding := "0 0 1em 1em"),
            FormInputEV(
              id = "neg",
              label = "Int -1023 to 1024",
              value = p.root.zoom(RootModel.negInt),
              validate = InputValidate.forIntRange(),
              changeAuditor = ChangeAuditor.forInt(-1023, 1024)
            ),
            <.div(p.root.get.negInt, ^.padding := "0 0 1em 1em"),
            FormInputEV(
              id = "ra",
              label = "RA",
              value = p.root.zoom(RootModel.ra),
              validate =
                InputValidate.fromFormat(RightAscension.fromStringHMS, "Invalid RA Format"),
              changeAuditor = ChangeAuditor.rightAscension
            ),
            <.div(p.root.get.ra.toString, ^.padding := "0 0 1em 1em")
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build

}

@Lenses
final case class RootModel(
  field1:       String,
  justAnInt:    Int,
  validatedInt: Int,
  refinedInt:   Int Refined Interval.Closed[0, 2048],
  negInt:       Int,
  ra:           RightAscension
)

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

    val initialModel = RootModel("Starts Mixed", 0, 0, 0, 0, RightAscension.fromRadians(0.0))

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
