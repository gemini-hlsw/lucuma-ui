// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.syntax.display.*
import lucuma.core.util.Enumerated
import lucuma.core.util.TimeSpan
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.*
import lucuma.ui.display.given
import lucuma.ui.reusability.given

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
  disabled: Boolean = false
)(using ViewLike[V])
    extends ReactFnProps(FormTimeSpanInput.component)

object FormTimeSpanInput:
  private type Props[V[_]] = FormTimeSpanInput[V]

  def component[V[_]](using ViewLike[V]) = ScalaFnComponent
    .withHooks[Props[V]]
    .useMemoBy(props => (props.units, props.value.get))(_ =>
      (u, v) => makeTimeUnitsMap(u, v.orEmpty)
    )
    .render: (props, timeUnitValues) =>
      val input = <.div(
        ^.id := props.id.value,
        LucumaPrimeStyles.TimeSpanInput,
        timeUnitValues.value.toVdomArray: (unit, value) =>
          val unitName = unit.shortName
          InputNumber(
            id = s"${props.id.value}-${unitName}-input",
            maxFractionDigits = 0,
            disabled = props.disabled,
            value = value,
            suffix = unitName,
            min = 0,
            onValueChange = e =>
              // Calculate the total timespan from the individual time units
              val newValue = timeUnitValues.value
                .updated(unit, e.valueOption.orEmpty)
                .toList
                .foldMap { case (unit, value) =>
                  TimeSpan.unsafeFromMicroseconds(
                    TimeUnit.MICROSECONDS.convert(value.toLong, unit)
                  )
                }

              val newValueClamped = clampTimeSpan(newValue, props.min.toOption, props.max.toOption)

              props.value
                .set(newValueClamped)
                .when_(newValueClamped =!= props.value.get.orEmpty || newValue =!= newValueClamped)
          ).withMods(LucumaPrimeStyles.TimeSpanInputItem,
                     ^.size := Math.max(unitName.length + value.toString.length, 3)
          ).withKey(unitName)
            .toUnmounted
      )

      React.Fragment(
        props.label.map(l => FormLabel(htmlFor = props.id)(l)),
        input
      )

  /**
   * Create a map of time units and their values for the given timespan
   *
   * E.g. a timespan of 25.5 hours would be 1 (day), 1 (hour), 30 (minutes)
   */
  private def makeTimeUnitsMap(
    units: NonEmptyList[TimeUnit],
    value: TimeSpan
  ): SortedMap[TimeUnit, Double] =
    units.distinct.sorted
      .foldLeft((SortedMap.empty[TimeUnit, Double], value.toMicroseconds)):
        case ((acc, rest), unit) =>
          val result = unit.convert(rest, TimeUnit.MICROSECONDS)
          val diff   = rest - TimeUnit.MICROSECONDS.convert(result, unit)

          (acc + (unit -> result.toDouble), diff)
      ._1

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
