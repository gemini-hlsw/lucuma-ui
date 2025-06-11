// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.TimeSpan
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.tooltip.*
import lucuma.ui.format.TimeSpanFormatter

import scalajs.js.JSConverters.*

/**
 * A view of a TimeSpan that formats the value with the given formatter, with hours and minutes as
 * the default.
 *
 * Hovering over the view will show the full (unrounded) TimeSpan in hours, minutes, and seconds,
 * unless a different tooltip is supplied.
 */
case class TimeSpanView(
  timespan:        TimeSpan,
  formatter:       TimeSpanFormatter = TimeSpanFormatter.HoursMinutesAbbreviation,
  tooltip:         Option[VdomNode] = None,
  tooltipPosition: Option[Tooltip.Position] = None,
  modifiers:       Seq[TagMod] = Seq.empty
) extends ReactFnProps(TimeSpanView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)

object TimeSpanView:
  private type Props = TimeSpanView

  private val component = ScalaFnComponent[Props]: props =>
    val ts                = props.timespan
    val tooltip: VdomNode = props.tooltip.getOrElse(
      s"${ts.toHours.longValue} hours, ${ts.toMinutesPart} minutes, ${ts.toSecondsPart} seconds"
    )

    <.span(
      props.modifiers.toTagMod
    )(
      props.formatter.format(ts)
    ).withTooltip(content = tooltip, position = props.tooltipPosition.orUndefined)
