// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.*
import lucuma.ui.syntax.all.given

case class Logo(systemName: NonEmptyString, systemNameStyle: Css)
    extends ReactFnProps(Logo.component)

object Logo:
  private val component =
    ScalaFnComponent[Logo]: props =>
      <.div(LoginStyles.LoginTitleWrapper)(
        <.div(LoginStyles.LoginTitle, props.systemNameStyle)(props.systemName.value)
      )
