// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import crystal.react.View
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.typed.StBuildingComponent
import lucuma.typed.primereact.components.{Button => CButton}
import react.common.Css
import react.primereact.Button
import react.primereact.InputText
import react.primereact.PrimeStyles

import scalajs.js

enum PlSize(val cls: Css) derives Eq:
  case Compact extends PlSize(LucumaPrimeStyles.Compact)
  case Mini    extends PlSize(LucumaPrimeStyles.Mini)
  case Tiny    extends PlSize(LucumaPrimeStyles.Tiny)
  case Small   extends PlSize(LucumaPrimeStyles.Small)
  case Medium  extends PlSize(LucumaPrimeStyles.Medium)
  case Large   extends PlSize(LucumaPrimeStyles.Large)
  case Huge    extends PlSize(LucumaPrimeStyles.Huge)
  case Massive extends PlSize(LucumaPrimeStyles.Massive)

extension (button: Button)
  def compact = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Compact)

  def mini    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Mini)
  def tiny    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Tiny)
  def small   = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Small)
  def medium  = button // medium is the default
  def large   = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Large)
  def big     = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Big)
  def huge    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Huge)
  def massive = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Massive)

extension (input: InputText)
  def mini    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Mini)
  def tiny    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Tiny)
  def small   = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Small)
  def medium  = input // medium is the default
  def large   = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Large)
  def big     = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Big)
  def huge    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Huge)
  def massive = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaPrimeStyles.Massive)

extension [A](
  input: FormInputTextView[View, Option[A]]
)(using Eq[Option[A]])
  def clearable: FormInputTextView[View, Option[A]] =
    input.value.get.filter(_ => input.disabled.forall(_ === false)).fold(input) { _ =>
      val newAddon =
        <.span(^.cls := (LucumaPrimeStyles.BlendedAddon |+| LucumaPrimeStyles.IconTimes).htmlClass,
               ^.onClick --> input.value.set(none)
        )
      // will go before other addons, but the units will still be first.
      input.copy(postAddons = newAddon :: input.postAddons)
    }

extension (addons: List[TagMod | CButton.Builder])
  private[primereact] def build(size: js.UndefOr[PlSize]): TagMod = addons.toTagMod(a =>
    a match {
      case b: CButton.Builder => b.build
      case t: TagMod          => <.span(t, PrimeStyles.InputGroupAddon |+| size.toOption.map(_.cls).orEmpty)
    }
  )
