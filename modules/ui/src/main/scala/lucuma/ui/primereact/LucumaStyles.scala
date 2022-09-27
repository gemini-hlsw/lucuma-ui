// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all._
import react.common._

trait LucumaStyles {

  val Compact: Css     = Css("pl-compact")
  val VeryCompact: Css = Css("pl-very-compact")

  val FormColumn: Css            = Css("pl-form-column")
  val FormColumnCompact: Css     = FormColumn |+| Compact
  val FormColumnVeryCompact: Css = FormColumn |+| VeryCompact

  val FormField: Css      = Css("pl-form-field")
  val FormFieldLabel: Css = Css("pl-form-field-label")
}

object LucumaStyles extends LucumaStyles
