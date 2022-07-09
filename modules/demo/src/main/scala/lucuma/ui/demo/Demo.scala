// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.data.NonEmptyChain
import cats.effect._
import cats.syntax.all._
import crystal.ViewF
import crystal.react._
import crystal.react.reuse._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.boolean.And
import eu.timepit.refined.boolean.Not
import eu.timepit.refined.cats._
import eu.timepit.refined.char.LowerCase
import eu.timepit.refined.collection.Forall
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric._
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.Reusability._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.RightAscension
import lucuma.core.math.validation.MathValidators
import lucuma.core.validation._
import lucuma.refined.*
import lucuma.ui.forms.FormInputEV
import lucuma.ui.input.ChangeAuditor
import lucuma.ui.input.FilterMode
import lucuma.ui.reusability._
import monocle.Focus
import org.scalajs.dom
import react.common._
import react.common.style.Css
import react.semanticui.collections.form.Form
import react.semanticui.elements.label.LabelPointing

import scala.scalajs.js.annotation._

final case class FormComponent(root: Reuse[ViewF[CallbackTo, FormComponent.RootModel]])
    extends ReactProps[FormComponent, FormComponent.State, Unit](FormComponent.component)

object FormComponent {
  type Props = FormComponent

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
    optionalEpoch: Boolean = true,
    scientific:    Boolean = true
  )
  object State {
    val valid1        = Focus[State](_.valid1)
    val valid2        = Focus[State](_.valid2)
    val forcedUpper   = Focus[State](_.forcedUpper)
    val validJaI      = Focus[State](_.validJaI)
    val refinedInt    = Focus[State](_.refinedInt)
    val refinedOdd    = Focus[State](_.refinedOdd)
    val bigDecimal    = Focus[State](_.bigDecimal)
    val refinedBigDec = Focus[State](_.refinedBigDec)
    val ra            = Focus[State](_.ra)
    val dec           = Focus[State](_.dec)
    val epoch         = Focus[State](_.epoch)
    val optionalEpoch = Focus[State](_.optionalEpoch)
    val scientific    = Focus[State](_.scientific)
  }

  type UpperNEPred = And[NonEmpty, Forall[Not[LowerCase]]]
  type UpperNES    = String Refined UpperNEPred
  object UpperNES extends RefinedTypeOps[UpperNES, String]

  val upperNESValidator = InputValidWedge[UpperNES](
    s => UpperNES.from(s.toUpperCase).toEitherErrorsUnsafe,
    _.toString
  )

  val OneBD   = BigDecimal(1.0)
  val ThreeBD = BigDecimal(3.0)
  type OneToThree = Interval.Closed[OneBD.type, ThreeBD.type]
  type ZeroTo2048 = Interval.Closed[0, 2048]

  final case class RootModel(
    field1:        UpperNES,
    field2:        String,
    forcedUpper:   UpperNES,
    justAnInt:     Int,
    refinedInt:    Int Refined Interval.Closed[0, 2048],
    refinedOdd:    Int Refined Odd,
    bigDecimal:    BigDecimal,
    refinedBigDec: BigDecimal Refined OneToThree,
    ra:            RightAscension,
    dec:           Declination,
    epoch:         Epoch,
    optionalEpoch: Option[Epoch],
    scientific:    BigDecimal
  )

  object RootModel {
    implicit val modelReusability: Reusability[RootModel] = Reusability.by_==[RootModel]

    val field1        = Focus[RootModel](_.field1)
    val field2        = Focus[RootModel](_.field2)
    val forcedUpper   = Focus[RootModel](_.forcedUpper)
    val justAnInt     = Focus[RootModel](_.justAnInt)
    val refinedInt    = Focus[RootModel](_.refinedInt)
    val refinedOdd    = Focus[RootModel](_.refinedOdd)
    val bigDecimal    = Focus[RootModel](_.bigDecimal)
    val refinedBigDec = Focus[RootModel](_.refinedBigDec)
    val ra            = Focus[RootModel](_.ra)
    val dec           = Focus[RootModel](_.dec)
    val epoch         = Focus[RootModel](_.epoch)
    val optionalEpoch = Focus[RootModel](_.optionalEpoch)
    val scientific    = Focus[RootModel](_.scientific)
  }

  implicit val propsReuse: Reusability[Props] = Reusability.derive[Props]
  implicit val stateReuse: Reusability[State] = Reusability.derive[State]

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
            FormInputEV[ReuseView, UpperNES](
              id = "field1".refined,
              label = "field1 - uppercased on blur, can't be empty",
              value = $.props.root.zoom(RootModel.field1),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = upperNESValidator,
              onValidChange = v => $.setStateL(State.valid1)(v)
            ),
            FormInputEV[ReuseView, String](
              id = "field2".refined,
              label = "field2 - can't be empty",
              value = $.props.root.zoom(RootModel.field2),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              error = "This is another error".refined,
              validFormat = InputValidWedge(
                s =>
                  if (s.isEmpty)
                    NonEmptyChain("Can't be empty".refined[NonEmpty]).asLeft
                  else
                    s.toLowerCase.asRight,
                identity[String]
              ),
              onValidChange = v => $.setStateL(State.valid2)(v)
            ),
            FormInputEV[ReuseView, String Refined UpperNEPred](
              id = "forced-upper".refined,
              label = "forced uppercase",
              value = $.props.root.zoom(RootModel.forcedUpper),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi.refinedString[UpperNEPred],
              changeAuditor = ChangeAuditor.refinedString[UpperNEPred](
                formatFn = _.toUpperCase,
                filterMode = FilterMode.Lax
              ),
              onValidChange = v => $.setStateL(State.forcedUpper)(v)
            ),
            FormInputEV[ReuseView, Int](
              id = "just-an-int".refined,
              label = "Just An Int",
              value = $.props.root.zoom(RootModel.justAnInt),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi.int,
              changeAuditor = ChangeAuditor.int,
              onValidChange = v => $.setStateL(State.validJaI)(v)
            ),
            FormInputEV[ReuseView, Int Refined Interval.Closed[0, 2048]](
              id = "refined-int".refined,
              label = "refined Int - 0 to 2048, input constrained",
              value = $.props.root.zoom(RootModel.refinedInt),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi
                .refinedInt[ZeroTo2048]
                .withErrorMessage("Must be in range 0-2048".refined),
              changeAuditor = ChangeAuditor.refinedInt[ZeroTo2048](),
              onValidChange = v => $.setStateL(State.refinedInt)(v)
            ),
            FormInputEV[ReuseView, Int Refined Odd](
              id = "odd-int".refined,
              label = "odd Int - validated on blur",
              value = $.props.root.zoom(RootModel.refinedOdd),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi
                .refinedInt[Odd]
                .withErrorMessage("Must be an odd integer".refined),
              changeAuditor = ChangeAuditor.refinedInt[Odd](filterMode = FilterMode.Lax),
              onValidChange = v => $.setStateL(State.refinedOdd)(v)
            ),
            FormInputEV[ReuseView, BigDecimal](
              id = "big-decimal".refined,
              label = "Big Decimal, 4 decimal places",
              value = $.props.root.zoom(RootModel.bigDecimal),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi.bigDecimal,
              changeAuditor = ChangeAuditor.bigDecimal(4.refined),
              onValidChange = v => $.setStateL(State.bigDecimal)(v)
            ),
            // FormInputEV[ReuseView, BigDecimal Refined OneToThree](
            //   id = "refined-big-decimal".refined,
            //   label = "Refined Big Decimal - 1 decimal place",
            //   value = $.props.root.zoom(RootModel.refinedBigDec),
            //   errorClazz = Css("error-label"),
            //   errorPointing = LabelPointing.Below,
            //   validFormat = InputValidSplitEpi
            //     .refinedBigDecimal[OneToThree]
            //     .withErrorMessage("Must be 1.0 to 3.0".refined),
            //   changeAuditor = ChangeAuditor.accept.decimal(1.refined),
            //   onValidChange = v => $.setStateL(State.refinedBigDec)(v)
            // ),
            FormInputEV[ReuseView, RightAscension](
              id = "ra".refined,
              label = "RA",
              value = $.props.root.zoom(RootModel.ra),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = MathValidators.truncatedRA,
              changeAuditor = ChangeAuditor.truncatedRA,
              onValidChange = v => $.setStateL(State.ra)(v)
            ),
            FormInputEV[ReuseView, Declination](
              id = "dec".refined,
              label = "Dec",
              value = $.props.root.zoom(RootModel.dec),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = MathValidators.truncatedDec,
              changeAuditor = ChangeAuditor.truncatedDec,
              onValidChange = v => $.setStateL(State.dec)(v)
            ),
            FormInputEV[ReuseView, Epoch](
              id = "epoch".refined,
              label = "Epoch",
              value = $.props.root.zoom(RootModel.epoch),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat =
                InputValidWedge.fromFormat(Epoch.fromStringNoScheme, "Must be a number".refined),
              changeAuditor = ChangeAuditor.fromFormat(Epoch.fromStringNoScheme).decimal(3.refined),
              onValidChange = v => $.setStateL(State.epoch)(v)
            ),
            FormInputEV[ReuseView, Option[Epoch]](
              id = "opt-epoch".refined,
              label = "Optional Epoch",
              value = $.props.root.zoom(RootModel.optionalEpoch),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidWedge
                .fromFormat(Epoch.fromStringNoScheme, "Must be a number".refined)
                .optional,
              changeAuditor =
                ChangeAuditor.fromFormat(Epoch.fromStringNoScheme).decimal(3.refined).optional,
              onValidChange = v => $.setStateL(State.epoch)(v)
            ),
            FormInputEV[ReuseView, BigDecimal](
              id = "scientific".refined,
              label = "Scientific Notation",
              value = $.props.root.zoom(RootModel.scientific),
              errorClazz = Css("error-label"),
              errorPointing = LabelPointing.Below,
              validFormat = InputValidSplitEpi.bigDecimalWithScientificNotation,
              changeAuditor = ChangeAuditor.scientificNotation(),
              onValidChange = v => $.setStateL(State.scientific)(v)
            )
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build
}

trait AppMain extends IOApp.Simple {
  import FormComponent._

  protected def rootComponent(view: Reuse[ViewF[CallbackTo, RootModel]]): VdomElement

  @JSExport
  def runIOApp(): Unit = main(Array.empty)

  override final def run: IO[Unit] = IO {
    ReusabilityOverlay.overrideGloballyInDev()

    val initialModel =
      RootModel(
        refineV[UpperNEPred]("FIELD").getOrElse(sys.error("Shouldn't happen")),
        "",
        refineV[UpperNEPred]("UPPER").getOrElse(sys.error("Shouldn't happen")),
        0,
        0.refined,
        refineV[Odd](1).getOrElse(sys.error("Shouldn't happen")),
        0.123456,
        Refined.unsafeApply[BigDecimal, OneToThree](OneBD),
        RightAscension.fromStringHMS.getOption("12:34:56.789876").get,
        Declination.fromStringSignedDMS.getOption("-11:22:33.987654").get,
        Epoch.J2000,
        None,
        BigDecimal(1.234e-19)
      )

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    val AppStateProvider = StateProvider(initialModel)

    AppStateProvider((rootComponent _).reuseAlways).renderIntoDOM(container)

    ()
  }
}

@JSExportTopLevel("Demo")
object Demo extends AppMain {
  override protected def rootComponent(
    rootView: Reuse[ViewF[CallbackTo, FormComponent.RootModel]]
  ): VdomElement =
    FormComponent(rootView)
}
