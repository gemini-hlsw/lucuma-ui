// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.table

import lucuma.react.common.ReactFnProps
import lucuma.react.common.ReactFnComponent
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.table.Column
import lucuma.ui.primereact.DebouncedInputText
import cats.syntax.option.*
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.react.primereact.DropdownOptional
import lucuma.react.primereact.SelectItem
import lucuma.react.common.Css

object ColumnFilter:
  case class Text(
    col:         Column[?, ?, ?, ?, ?, String, ?],
    delayMillis: Int = 250,
    placeholder: String = "Filter",
    clazz:       Css = Css.Empty
  ) extends ReactFnProps(Text)

  object Text
      extends ReactFnComponent[Text](props =>
        DebouncedInputText(
          id = NonEmptyString.unsafeFrom(s"${props.col.id.value}-filter"),
          delayMillis = props.delayMillis,
          placeholder = props.placeholder,
          value = props.col.getFilterValue().map(_.toString).getOrElse(""),
          onChange = v => props.col.setFilterValue(v.some.filter(_.nonEmpty)),
          // showClear = true, // !!!!!
          clazz = props.clazz
        ).withMods(^.width := "100%")
      )

  /** Requires table with facetedUniqueValues enabled */
  case class Select[A](
    col:         Column[?, A, ?, ?, ?, String, ?],
    display:     A => String = (a: A) => a.toString,
    placeholder: String = "Filter",
    showCount:   Boolean = false,
    clazz:       Css = Css.Empty
  ) extends ReactFnProps(Select):
    val options: List[(String, Int)] =
      col.getFacetedUniqueValues().map((k, v) => (k.toString, v)).toList.sortBy(_._1)

  object Select
      extends ReactFnComponent[Select[Any]](props =>
        DropdownOptional[String](
          id = s"${props.col.id.value}-filter",
          value = props.col.getFilterValue(),
          options = props.options.map: (a, count) =>
            SelectItem(value = a, label = if props.showCount then s"$a (${count})" else a),
          onChange = props.col.setFilterValue(_),
          showClear = true,
          placeholder = props.placeholder,
          clazz = props.clazz
        ).withMods(^.width := "100%")
      )
