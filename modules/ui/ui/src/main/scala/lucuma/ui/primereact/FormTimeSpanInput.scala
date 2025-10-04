// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.data.NonEmptyList
import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import eu.timepit.refined.cats.given
import eu.timepit.refined.types.numeric.NonNegLong
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.syntax.display.*
import lucuma.core.util.Enumerated
import lucuma.core.util.TimeSpan
import lucuma.core.validation.*
import lucuma.react.common.Css
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.*
import lucuma.react.primereact.tooltip.*
import lucuma.ui.display.given
import lucuma.ui.input.ChangeAuditor
import lucuma.ui.reusability.given
import lucuma.ui.utils.*

import java.util.concurrent.TimeUnit
import scala.collection.immutable.SortedMap
import scala.scalajs.js

case class FormTimeSpanInput[V[_]](
  id:       NonEmptyString,
  value:    V[TimeSpan],
  units:    NonEmptyList[TimeUnit] = NonEmptyList.of(TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES),
  label:    js.UndefOr[TagMod] = js.undefined,
  min:      js.UndefOr[TimeSpan] = js.undefined,
  max:      js.UndefOr[TimeSpan] = js.undefined,
  disabled: Boolean = false,
  clazz:    js.UndefOr[Css] = js.undefined,
  tooltip:  js.UndefOr[VdomNode] = js.undefined,
  error:    js.UndefOr[NonEmptyString] = js.undefined
)(using val vl: ViewLike[V])
    extends ReactFnProps(FormTimeSpanInput.component)

object FormTimeSpanInput:
  private type AnyF[_]     = Any
  private type Props[V[_]] = FormTimeSpanInput[V]

  private def componentBuilder[V[_]] = ScalaFnComponent[Props[V]]: props =>
    import props.given

    for {
      timeUnitValues <- useStateView(makeTimeUnitsMap(props.units, props.value.get.orEmpty))
      _              <- useEffectWithDeps((timeUnitValues.reuseByValue, props.value.get)):
                          (timeUnitValues, value) =>
                            val newValues = makeTimeUnitsMap(props.units, value.orEmpty)
                            if (newValues != timeUnitValues.get) timeUnitValues.set(newValues)
                            else Callback.empty
    } yield

      val input = <.div(
        ^.id := props.id.value,
        LucumaPrimeStyles.TimeSpanInput |+| props.clazz.getOrElse(Css.Empty),
        timeUnitValues
          .withOnMod(vs =>
            props.value.set(vs.toClampedTimeSpan(props.min.toOption, props.max.toOption))
          )
          .toListOfViews
          .toVdomArray(using
            (unit, valueView) =>
              val unitName = unit.shortName
              FormInputTextView(
                id = NonEmptyString.unsafeFrom(s"${props.id.value}-${unitName}-input"),
                disabled = props.disabled,
                value = valueView,
                validFormat = InputValidSplitEpi.nonNegLong,
                changeAuditor = ChangeAuditor.int,
                units = unitName,
                error = props.error
              ).withMods(^.size := Math.max(valueView.get.toString.length, 2))
                .withKey(unitName)
                .toUnmounted
          )
      )

      React.Fragment(
        props.label.map(l => FormLabel(htmlFor = props.id)(l)),
        props.tooltip.fold(input)(tt => input.withTooltip(content = tt))
      )

  protected val component = componentBuilder[AnyF]

  /**
   * Create a map of time units and their values for the given timespan
   *
   * E.g. a timespan of 25.5 hours would be 1 (day), 1 (hour), 30 (minutes)
   */
  private def makeTimeUnitsMap(
    units: NonEmptyList[TimeUnit],
    value: TimeSpan
  ): SortedMap[TimeUnit, NonNegLong] =
    units.distinct.sorted
      .foldLeft((SortedMap.empty[TimeUnit, NonNegLong], value.toMicroseconds)):
        case ((acc, rest), unit) =>
          val result = unit.convert(rest, TimeUnit.MICROSECONDS)
          val diff   = rest - TimeUnit.MICROSECONDS.convert(result, unit)

          (acc + (unit -> NonNegLong.unsafeFrom(result)), diff)
      ._1

  extension (values: SortedMap[TimeUnit, NonNegLong])
    private def toTimeSpan: TimeSpan                                                      =
      values.toList
        .foldMap { case (unit, value) =>
          TimeSpan.unsafeFromMicroseconds(TimeUnit.MICROSECONDS.convert(value.value, unit))
        }
    private def toClampedTimeSpan(min: Option[TimeSpan], max: Option[TimeSpan]): TimeSpan =
      clampTimeSpan(toTimeSpan, min, max)

  // Custom ordering to go from biggest to smallest
  private given Ordering[TimeUnit]   = Ordering.by[TimeUnit, Int](_.ordinal).reverse
  private given Enumerated[TimeUnit] = Enumerated
    .fromNEL(NonEmptyList.fromListUnsafe(TimeUnit.values().reverse.toList))
    .withTag(_.shortName)

  /** Clamp a time span to between the given min and max, if they are defined */
  private def clampTimeSpan(ts: TimeSpan, min: Option[TimeSpan], max: Option[TimeSpan]): TimeSpan =
    if min.forall(_ <= ts) && max.forall(_ >= ts) then ts
    else
      val minClamped = min.fold(ts)(ts.max)
      max.fold(minClamped)(minClamped.min)
