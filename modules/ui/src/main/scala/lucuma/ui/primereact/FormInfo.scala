// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*

import scalajs.js
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.tooltip.*
import eu.timepit.refined.types.string.NonEmptyString

case class FormInfo(
  value:           TagMod,
  label:           js.UndefOr[TagMod] = js.undefined,
  size:            js.UndefOr[PlSize] = js.undefined,
  tooltip:         js.UndefOr[VdomNode] = js.undefined,
  tooltipPosiiton: js.UndefOr[Tooltip.Position] = js.undefined
) extends ReactFnProps(FormInfo.component)

object FormInfo:
  private type Props = FormInfo

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .useId
      .render: (props, id) =>
        val value = <.span(^.id := id)(props.value)

        React.Fragment(
          props.label
            .map: l =>
              FormLabel(htmlFor = NonEmptyString.unsafeFrom(id), size = props.size)(l),
          props.tooltip.fold(value): tt =>
            value.withTooltip(content = tt, position = props.tooltipPosiiton)
        )
