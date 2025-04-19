// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import cats.Eq
import cats.syntax.option.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css
import lucuma.react.common.ReactFnComponent
import lucuma.react.common.ReactFnProps
import lucuma.react.primereact.DropdownOptional
import lucuma.react.primereact.SelectItem
import lucuma.react.table.Column
import lucuma.ui.primereact.DebouncedInputText

object ColumnFilter:
  case class Text(
    col:         Column[?, ?, ?, ?, ?, String, ?],
    delayMillis: Int = 250,
    placeholder: String = "<Filter>",
    clazz:       Css = Css.Empty
  ) extends ReactFnProps(Text)

  object Text
      extends ReactFnComponent[Text](props =>
        val filterValue: String = props.col.getFilterValue().map(_.toString).getOrElse("")
        DebouncedInputText(
          id = NonEmptyString.unsafeFrom(s"${props.col.id.value}-filter"),
          delayMillis = props.delayMillis,
          placeholder = props.placeholder,
          value = filterValue,
          onChange = v => props.col.setFilterValue(v.some.filter(_.nonEmpty)),
          clazz = props.clazz
        ).withMods(^.width := "100%")
      )

  /** Requires table with facetedUniqueValues enabled */
  case class Select[A](
    col:         Column[?, A, ?, ?, ?, A, ?],
    display:     A => String = (a: A) => a.toString,
    placeholder: String = "<Filter>",
    showCount:   Boolean = true,
    clazz:       Css = Css.Empty
  )(using val eq: Eq[A])
      extends ReactFnProps(Select.component):
    protected[table] val options: List[(A, String)] =
      col
        .getFacetedUniqueValues()
        .map: (a, count) =>
          val name: String  = display(a)
          val label: String = if showCount then s"$name (${count})" else name
          (a, label)
        .toList
        .sortBy(_._2)

  trait SelectBuilder[A]:
    type Props = Select[A]

    val component = ScalaFnComponent[Props]: props =>
      import props.given

      DropdownOptional(
        id = s"${props.col.id.value}-filter",
        value = props.col.getFilterValue(),
        options = props.options.map: (a, label) =>
          SelectItem(value = a, label = label),
        onChange = props.col.setFilterValue(_),
        showClear = true,
        placeholder = props.placeholder,
        clazz = props.clazz
      ).withMods(^.width := "100%")

  object Select extends SelectBuilder[Any]
