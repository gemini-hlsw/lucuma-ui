// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*

import scalajs.js

object FormLabel {
  def apply(
    htmlFor: NonEmptyString,
    size:    js.UndefOr[PlSize] = js.undefined,
    clazz:   js.UndefOr[Css] = js.undefined
  ) = <.label(
    LucumaPrimeStyles.FormFieldLabel |+| clazz.getOrElse(Css.Empty),
    size.toOption.map(_.cls).orEmpty,
    ^.htmlFor := htmlFor.value
  )
}
