// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.effect._
import cats.effect.std.Dispatcher
import cats.syntax.all._
import crystal.ViewF
import crystal.react._
import crystal.react.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.numeric._
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.MonocleReact._
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.Reusability._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^._
import log4cats.loglevel.LogLevelLogger
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.RightAscension
import lucuma.ui.forms._
import lucuma.ui.implicits._
import lucuma.ui.optics.ChangeAuditor
import lucuma.ui.optics.FilterMode
import lucuma.ui.optics.TruncatedDec
import lucuma.ui.optics.TruncatedRA
import lucuma.ui.optics.TruncatedRefinedBigDecimal
import lucuma.ui.optics.ValidFormatInput
import lucuma.ui.refined._
import lucuma.ui.reusability._
import monocle.macros.Lenses
import org.scalajs.dom
import org.typelevel.log4cats.Logger
import react.common.ReactProps
import react.common.style.Css
import react.semanticui.collections.form.Form
import react.semanticui.elements.label.LabelPointing

import scala.scalajs.js.annotation._

case class AppContext[F[_]]()(implicit val dispatcher: Dispatcher[F], val logger: Logger[F])

object AppContext {
  implicit val contextReusability: Reusability[AppContext[IO]] = Reusability.never

  implicit def ctx2Dispatcher[F[_]](implicit ctx: AppContext[F]): Dispatcher[F] = ctx.dispatcher
  implicit def ctx2Logger[F[_]](implicit ctx:     AppContext[F]): Logger[F]     = ctx.logger
}

object AppCtx extends Ctx[IO, AppContext[IO]]

import AppContext._

final case class FormComponent(root: ViewF[IO, FormComponent.RootModel])(implicit
  val ctx:                           AppContext[IO]
) extends ReactProps[FormComponent](FormComponent.component)

object FormComponent {
  type Props = FormComponent

  @Lenses
  case class State(
    valid1:        Boolean = true,
    valid2:        Boolean = true,
    forcedUpper:   Boolean = true,
    validJaI:      Boolean = true,
    refinedInt:    Boolean = true,
    refinedOdd:    Boolean = true,
    bigDecimal:    Boolean = true,
    refinedBigDec: Boolean = true,
    ra:            Boolean = true,
    dec:           Boolean = true,
    epoch:         Boolean = true,
    optionalEpoch: Boolean = true
  )

  val OneBD   = BigDecimal(1.0)
  val ThreeBD = BigDecimal(3.0)
  type OneToThree = Interval.Closed[OneBD.type, ThreeBD.type]
  type ZeroTo2048 = Interval.Closed[0, 2048]

  @Lenses
  final case class RootModel(
    field1:        UpperNES,
    field2:        String,
    forcedUpper:   String Refined UpperNEPred,
    justAnInt:     Int,
    refinedInt:    Int Refined Interval.Closed[0, 2048],
    refinedOdd:    Int Refined Odd,
    bigDecimal:    BigDecimal,
    refinedBigDec: BigDecimal Refined OneToThree,
    ra:            RightAscension,
    dec:           Declination,
    epoch:         Epoch,
    optionalEpoch: Option[Epoch]
  )

  object RootModel {
    implicit val modelReusability: Reusability[RootModel] = Reusability.by_==[RootModel]
  }

  implicit val propsReuse = Reusability.derive[Props]
  implicit val stateReuse = Reusability.derive[State]

  val component =
    ScalaComponent
      .builder[Props]
      .initialState(State())
      .render { $ =>
        implicit val ctx = $.props.ctx

        <.div(^.paddingTop := "20px")(
          s"MODEL: ${$.props.root.get}",
          <.br,
          s"STATE: ${$.state}",
          Form(
            FormInputEV(
              id = "field1",
              label = "field1 - uppercased on blur, can't be empty",
              value = $.props.root.zoom(RootModel.field1),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.upperNESValidFormat,
              onValidChange = v => $.setStateL(State.valid1)(v)
            ),
            FormInputEV(
              id = "field2",
              label = "field2 - can't be empty",
              value = $.props.root.zoom(RootModel.field2),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              error = NonEmptyString("This is another error"),
              validFormat = ValidFormatInput(
                s =>
                  if (s.isEmpty)
                    NonEmptyString("Can't be empty").invalidNec
                  else
                    s.toLowerCase.validNec,
                identity[String]
              ),
              onValidChange = v => $.setStateL(State.valid2)(v)
            ),
            FormInputEV(
              id = "forced-upper",
              label = "forced uppercase",
              value = $.props.root.zoom(RootModel.forcedUpper),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.forRefinedString[UpperNEPred]("Can't be empty"),
              changeAuditor = ChangeAuditor.forRefinedString[UpperNEPred](formatFn = _.toUpperCase,
                                                                          filterMode =
                                                                            FilterMode.Lax
              ),
              onValidChange = v => $.setStateL(State.forcedUpper)(v)
            ),
            FormInputEV(
              id = "just-an-int",
              label = "Just An Int",
              value = $.props.root.zoom(RootModel.justAnInt),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.intValidFormat(),
              changeAuditor = ChangeAuditor.int,
              onValidChange = v => $.setStateL(State.validJaI)(v)
            ),
            FormInputEV(
              id = "refined-int",
              label = "refined Int - 0 to 2048, input constrained",
              value = $.props.root.zoom(RootModel.refinedInt),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.forRefinedInt[ZeroTo2048]("Must be in range 0-2048"),
              changeAuditor = ChangeAuditor.forRefinedInt[ZeroTo2048](),
              onValidChange = v => $.setStateL(State.refinedInt)(v)
            ),
            FormInputEV(
              id = "odd-int",
              label = "odd Int - validated on blur",
              value = $.props.root.zoom(RootModel.refinedOdd),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.forRefinedInt[Odd]("Must be an odd integer"),
              changeAuditor = ChangeAuditor.forRefinedInt[Odd](filterMode = FilterMode.Lax),
              onValidChange = v => $.setStateL(State.refinedOdd)(v)
            ),
            FormInputEV(
              id = "big-decimal",
              label = "Big Decimal, 4 decimal places",
              value = $.props.root.zoom(RootModel.bigDecimal),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.bigDecimalValidFormat(),
              changeAuditor = ChangeAuditor.bigDecimal(4),
              onValidChange = v => $.setStateL(State.bigDecimal)(v)
            ),
            FormInputEV(
              id = "refined-big-decimal",
              label = "Refined Big Decimal - 1 decimal place",
              value = $.props.root
                .zoom(RootModel.refinedBigDec)
                .zoomSplitEpi(
                  TruncatedRefinedBigDecimal.unsafeRefinedBigDecimal[OneToThree, 1]
                ),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput
                .forRefinedTruncatedBigDecimal[OneToThree, 1]("Must be 1.0 to 3.0"),
              changeAuditor = ChangeAuditor.accept.decimal(1),
              onValidChange = v => $.setStateL(State.refinedBigDec)(v)
            ),
            FormInputEV(
              id = "ra",
              label = "RA",
              value = $.props.root.zoom(RootModel.ra).zoomSplitEpi(TruncatedRA.rightAscension),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.truncatedRA,
              changeAuditor = ChangeAuditor.truncatedRA,
              onValidChange = v => $.setStateL(State.ra)(v)
            ),
            FormInputEV(
              id = "dec",
              label = "Dec",
              value = $.props.root.zoom(RootModel.dec).zoomSplitEpi(TruncatedDec.declination),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = ValidFormatInput.truncatedDec,
              changeAuditor = ChangeAuditor.truncatedDec,
              onValidChange = v => $.setStateL(State.dec)(v)
            ),
            FormInputEV(
              id = "epoch",
              label = "Epoch",
              value = $.props.root.zoom(RootModel.epoch),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat =
                ValidFormatInput.fromFormat(Epoch.fromStringNoScheme, "Must be a number"),
              changeAuditor = ChangeAuditor.fromFormat(Epoch.fromStringNoScheme).decimal(3),
              onValidChange = v => $.setStateL(State.epoch)(v)
            ),
            FormInputEV(
              id = "opt-epoch",
              label = "Optional Epoch",
              value = $.props.root.zoom(RootModel.optionalEpoch),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat =
                ValidFormatInput.fromFormatOptional(Epoch.fromStringNoScheme, "Must be a number"),
              changeAuditor =
                ChangeAuditor.fromFormat(Epoch.fromStringNoScheme).decimal(3).optional,
              onValidChange = v => $.setStateL(State.epoch)(v)
            )
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build
}

trait AppMain extends IOApp {
  import FormComponent._

  implicit protected val logger: Logger[IO] = LogLevelLogger.createForRoot[IO]

  protected def rootComponent(view: ViewF[IO, RootModel])(implicit ctx: AppContext[IO]): VdomElement

  @JSExport
  def runIOApp(): Unit = main(Array.empty)

  override final def run(args: List[String]): IO[ExitCode] = {
    ReusabilityOverlay.overrideGloballyInDev()

    val initialModel =
      RootModel(
        "FIELD",
        "",
        "UPPER",
        0,
        0,
        1,
        0.123456,
        Refined.unsafeApply[BigDecimal, OneToThree](OneBD),
        RightAscension.fromStringHMS.getOption("12:34:56.789876").get,
        Declination.fromStringSignedDMS.getOption("-11:22:33.987654").get,
        Epoch.J2000,
        None
      )

    Dispatcher[IO].allocated.map(_._1).flatMap { implicit dispatcher =>
      IO {
        val container = Option(dom.document.getElementById("root")).getOrElse {
          val elem = dom.document.createElement("div")
          elem.id = "root"
          dom.document.body.appendChild(elem)
          elem
        }

        implicit val ctx = new AppContext[IO]()

        val RootComponent = ContextProvider[IO](AppCtx, ctx)(
          StateProvider[IO](initialModel)(rootComponent)
        )

        RootComponent().renderIntoDOM(container)

        ExitCode.Success
      }
    }
  }
}

@JSExportTopLevel("Demo")
object Demo extends AppMain {
  override protected def rootComponent(rootView: ViewF[IO, FormComponent.RootModel])(implicit
    ctx:                                         AppContext[IO]
  ): VdomElement =
    FormComponent(rootView)
}
