// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all._
import react.common._
import react.primereact.PrimeStyles

trait LucumaStyles {

  val FormColumn: Css            = Css("form-column")
  val FormColumnCompact: Css     = FormColumn |+| PrimeStyles.Compact
  val FormColumnVeryCompact: Css = FormColumn |+| PrimeStyles.VeryCompact

  val FormField: Css      = Css("form-field")
  val FormFieldLabel: Css = Css("form-field-label")
}

object LucumaStyles extends LucumaStyles
