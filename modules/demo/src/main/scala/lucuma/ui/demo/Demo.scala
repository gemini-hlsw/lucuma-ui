// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.demo

import cats.data.NonEmptyChain
import cats.effect._
import cats.syntax.all._
import crystal.ViewF
import crystal.react._
import crystal.react.hooks._
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
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.ReusabilityOverlay
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math.Declination
import lucuma.core.math.Epoch
import lucuma.core.math.RightAscension
import lucuma.core.math.validation.MathValidators
import lucuma.core.validation._
import lucuma.refined.*
import lucuma.ui.input.ChangeAuditor
import lucuma.ui.input.FilterMode
import lucuma.ui.primereact.FormInputTextView
import lucuma.ui.primereact.LucumaStyles
import lucuma.ui.primereact.given
import lucuma.ui.syntax.all.*
import lucuma.ui.syntax.all.given
import monocle.Focus
import org.scalajs.dom
import react.common.ReactFnProps
import react.common.style.Css

import scala.scalajs.js.annotation._

final case class FormComponent() extends ReactFnProps[FormComponent](FormComponent.component)

object FormComponent {
  protected type Props = FormComponent

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
    val Initial: RootModel =
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

  val component =
    ScalaFnComponent
      .withHooks[Props]
      .useStateView(RootModel.Initial)
      .render((_, root) =>
        <.div(^.paddingTop := "20px")(
          s"MODEL: ${root.get}",
          <.br,
          <.div(
            LucumaStyles.FormColumn,
            FormInputTextView(
              id = "field1".refined,
              label = "field1 - uppercased on blur, can't be empty",
              value = root.zoom(RootModel.field1),
              validFormat = upperNESValidator
            ),
            FormInputTextView(
              id = "field2".refined,
              label = "field2 - can't be empty",
              value = root.zoom(RootModel.field2),
              error = "This is another error".refined,
              validFormat = InputValidWedge(
                s =>
                  if (s.isEmpty)
                    NonEmptyChain("Can't be empty".refined[NonEmpty]).asLeft
                  else
                    s.toLowerCase.asRight,
                identity[String]
              )
            ),
            FormInputTextView(
              id = "forced-upper".refined,
              label = "forced uppercase",
              value = root.zoom(RootModel.forcedUpper),
              validFormat = InputValidSplitEpi.refinedString[UpperNEPred],
              changeAuditor = ChangeAuditor.refinedString[UpperNEPred](
                formatFn = _.toUpperCase,
                filterMode = FilterMode.Lax
              )
            ),
            FormInputTextView(
              id = "just-an-int".refined,
              label = "Just An Int",
              value = root.zoom(RootModel.justAnInt),
              validFormat = InputValidSplitEpi.int,
              changeAuditor = ChangeAuditor.int
            ),
            FormInputTextView(
              id = "refined-int".refined,
              label = "refined Int - 0 to 2048, input constrained",
              value = root.zoom(RootModel.refinedInt),
              validFormat = InputValidSplitEpi
                .refinedInt[ZeroTo2048]
                .withErrorMessage("Must be in range 0-2048".refined),
              changeAuditor = ChangeAuditor.refinedInt[ZeroTo2048]()
            ),
            FormInputTextView(
              id = "odd-int".refined,
              label = "odd Int - validated on blur",
              value = root.zoom(RootModel.refinedOdd),
              validFormat = InputValidSplitEpi
                .refinedInt[Odd]
                .withErrorMessage("Must be an odd integer".refined),
              changeAuditor = ChangeAuditor.refinedInt[Odd](filterMode = FilterMode.Lax)
            ),
            FormInputTextView(
              id = "big-decimal".refined,
              label = "Big Decimal, 4 decimal places",
              value = root.zoom(RootModel.bigDecimal),
              validFormat = InputValidSplitEpi.bigDecimal,
              changeAuditor = ChangeAuditor.bigDecimal(4.refined)
            ),
            // FormInputTextView(
            //   id = "refined-big-decimal".refined,
            //   label = "Refined Big Decimal - 1 decimal place",
            //   value = root.zoom(RootModel.refinedBigDec),
            //   validFormat = InputValidSplitEpi
            //     .refinedBigDecimal[OneToThree]
            //     .withErrorMessage("Must be 1.0 to 3.0".refined),
            //   changeAuditor = ChangeAuditor.accept.decimal(1.refined)
            // ),
            FormInputTextView(
              id = "ra".refined,
              label = "RA",
              value = root.zoom(RootModel.ra),
              validFormat = MathValidators.truncatedRA,
              changeAuditor = ChangeAuditor.truncatedRA
            ),
            FormInputTextView(
              id = "dec".refined,
              label = "Dec",
              value = root.zoom(RootModel.dec),
              validFormat = MathValidators.truncatedDec,
              changeAuditor = ChangeAuditor.truncatedDec
            ),
            FormInputTextView(
              id = "epoch".refined,
              label = "Epoch",
              value = root.zoom(RootModel.epoch),
              validFormat = MathValidators.epochNoScheme,
              changeAuditor = ChangeAuditor.maxLength(8.refined).decimal(3.refined).denyNeg
            ),
            FormInputTextView(
              id = "opt-epoch".refined,
              label = "Optional Epoch",
              value = root.zoom(RootModel.optionalEpoch),
              validFormat = MathValidators.epochNoScheme.optional,
              changeAuditor =
                ChangeAuditor.maxLength(8.refined).decimal(3.refined).denyNeg.optional,
              placeholder = "Make it Epoch!"
            ),
            FormInputTextView(
              id = "scientific".refined,
              label = "Scientific Notation",
              value = root.zoom(RootModel.scientific),
              validFormat = InputValidSplitEpi.bigDecimalWithScientificNotation,
              changeAuditor = ChangeAuditor.scientificNotation()
            )
          )
        )
      )
}

trait AppMain extends IOApp.Simple {
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

    rootComponent.renderIntoDOM(container)

    ()
  }
}

@JSExportTopLevel("Demo")
object Demo extends AppMain {
  override protected val rootComponent: VdomElement =
    FormComponent()
}
