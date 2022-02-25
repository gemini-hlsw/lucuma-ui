// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.syntax.all._
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import react.common.ReactProps
import react.semanticui.addons.select.Select
import react.semanticui.modules.dropdown._

import scala.scalajs.js.JSConverters._

/**
 * Produces a dropdown menu, similar to a combobox
 */
final case class EnumSelect[A](
  label:          String,
  value:          Option[A],
  placeholder:    String = "",
  disabled:       Boolean = false,
  onChange:       A => Callback = (_: A) => Callback.empty,
  disabledItems:  Set[A] = Set.empty[A]
)(implicit
  val enumerated: Enumerated[A],
  val display:    Display[A]
) extends ReactProps[EnumSelect[Any]](EnumSelect.component)

object EnumSelect {
  type Props[A] = EnumSelect[A]

  implicit protected def propsReuse[A]: Reusability[Props[A]] =
    Reusability.by(p => (p.label, p.value.map(p.display.shortName), p.placeholder, p.disabled))

  protected val component =
    ScalaComponent
      .builder[Props[Any]]
      .stateless
      .render_P { p =>
        implicit val display = p.display

        <.div(
          ^.cls := "field",
          <.label(p.label),
          Select(
            placeholder = p.placeholder,
            fluid = true,
            disabled = p.disabled,
            value = p.value.map(i => p.enumerated.tag(i)).orUndefined,
            options = p.enumerated.all
              .map(i =>
                DropdownItem(
                  text = i.shortName,
                  value = p.enumerated.tag(i),
                  disabled = p.disabledItems.contains(i)
                )
              ),
            onChange = (ddp: Dropdown.DropdownProps) =>
              ddp.value.toOption
                .flatMap(v => p.enumerated.fromTag(v.asInstanceOf[String]))
                .map(v => p.onChange(v))
                .getOrEmpty
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build
}
