// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.TimeSpan
import lucuma.react.common.ReactFnProps
import lucuma.ui.syntax.time.*

/**
 * A view of a TimeSpan that shows hours and minutes.
 *
 * Hovering over the view will show the full (unrounded) TimeSpan in hours, minutes, and seconds.
 */
case class TimeSpanView(
  timespan:   TimeSpan,
  shortUnits: Boolean = false,
  modifiers:  Seq[TagMod] = Seq.empty
) extends ReactFnProps(TimeSpanView.component):
  def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  def withMods(mods:          TagMod*)     = addModifiers(mods)

object TimeSpanView:
  private type Props = TimeSpanView

  private val component = ScalaFnComponent[Props]: props =>
    val ts = props.timespan

    <.span(
      props.modifiers.toTagMod,
      ^.title := s"${ts.toHoursPart} hours, ${ts.toMinutesPart} minutes, ${ts.toSecondsPart} seconds"
    )(
      ts.toHoursMinutes(props.shortUnits)
    )
