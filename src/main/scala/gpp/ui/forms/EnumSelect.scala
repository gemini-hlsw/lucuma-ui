// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import scala.scalajs.js.JSConverters._

import cats.Show
import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.util.Enumerated
import react.common.ReactProps
import react.semanticui.addons.select.Select
import react.semanticui.modules.dropdown._

/**
 * Produces a dropdown menu, similar to a combobox
 */
final case class EnumSelect[A](
  label:       String,
  value:       Option[A],
  placeholder: String,
  disabled:    Boolean,
  onChange:    A => Callback = (_: A) => Callback.empty
)(implicit
  val enum:    Enumerated[A],
  val show:    Show[A]
) extends ReactProps[EnumSelect[Any]](EnumSelect.component)

object EnumSelect {
  type Props[A] = EnumSelect[A]

  implicit protected def propsReuse[A]: Reusability[Props[A]] =
    Reusability.by(p => (p.label, p.value.map(p.show.show), p.placeholder, p.disabled))

  protected val component =
    ScalaComponent
      .builder[Props[Any]]
      .stateless
      .render_P { p =>
        implicit val show = p.show

        <.div(
          ^.cls := "field",
          <.label(p.label),
          Select(
            placeholder = p.placeholder,
            fluid = true,
            disabled = p.disabled,
            value = p.value.map(i => p.enum.tag(i)).orUndefined,
            options = p.enum.all
              .map(i => DropdownItem(text = i.show, value = p.enum.tag(i))),
            onChange = (ddp: Dropdown.DropdownProps) =>
              ddp.value.toOption
                .flatMap(v => p.enum.fromTag(v.asInstanceOf[String]))
                .map(v => p.onChange(v))
                .getOrEmpty
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build
}
