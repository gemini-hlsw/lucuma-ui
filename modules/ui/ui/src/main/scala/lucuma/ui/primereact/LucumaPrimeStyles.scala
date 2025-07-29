// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import lucuma.react.common.*

trait LucumaPrimeStyles:
  // compact is used for form columns and buttons
  val Compact: Css     = Css("pl-compact")
  // not used for buttons, at least not at the moment
  val VeryCompact: Css = Css("pl-very-compact")

  // currently used for buttons
  val Mini: Css    = Css("pl-mini")
  val Tiny: Css    = Css("pl-tiny")
  val Small: Css   = Css("pl-small")
  val Medium: Css  = Css("pl-medium")
  val Large: Css   = Css("pl-large")
  val Big: Css     = Css("pl-big")
  val Huge: Css    = Css("pl-huge")
  val Massive: Css = Css("pl-massive")

  val FormColumn: Css            = Css("pl-form-column")
  val FormColumnCompact: Css     = FormColumn |+| Compact
  val FormColumnVeryCompact: Css = FormColumn |+| VeryCompact
  val LinearColumn: Css          = Css("pl-linear-column")

  val FormField: Css      = Css("pl-form-field")
  val FormFieldLabel: Css = Css("pl-form-field-label")

  val CheckboxWithLabel: Css    = Css("pl-checkbox-with-label")
  val RadioButtonWithLabel: Css = Css("pl-radiobutton-with-label")

  val BlendedAddon: Css = Css("pl-blended-addon")

  val IconPrefix: Css = Css("pi")
  val IconTimes: Css  = IconPrefix |+| Css("pi-times")

  object Dialog: // SUI has Mini, Tiny, Small, Large and Fullscreen.
    val Small: Css = Css("pl-dialog-small")
    val Large: Css = Css("pl-dialog-large")

  val TimeSpanInput: Css = Css("p-time-span-input")

object LucumaPrimeStyles extends LucumaPrimeStyles
