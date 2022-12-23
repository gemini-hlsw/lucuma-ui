// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import crystal.react.View
import japgolly.scalajs.react.vdom.html_<^.*
import react.common.Css
import react.primereact.Button
import react.primereact.InputText
import reactST.StBuildingComponent

import scalajs.js

enum PlSize(val cls: Css) derives Eq:
  case Compact extends PlSize(LucumaStyles.Compact)
  case Mini    extends PlSize(LucumaStyles.Mini)
  case Tiny    extends PlSize(LucumaStyles.Tiny)
  case Small   extends PlSize(LucumaStyles.Small)
  case Medium  extends PlSize(LucumaStyles.Medium)
  case Large   extends PlSize(LucumaStyles.Large)
  case Huge    extends PlSize(LucumaStyles.Huge)
  case Massive extends PlSize(LucumaStyles.Massive)

extension (button: Button)
  def compact = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Compact)

  def mini    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Mini)
  def tiny    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Tiny)
  def small   = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Small)
  def medium  = button // medium is the default
  def large   = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Large)
  def big     = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Big)
  def huge    = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Huge)
  def massive = button.copy(clazz = button.clazz.toOption.orEmpty |+| LucumaStyles.Massive)

extension (input: InputText)
  def mini    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Mini)
  def tiny    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Tiny)
  def small   = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Small)
  def medium  = input // medium is the default
  def large   = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Large)
  def big     = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Big)
  def huge    = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Huge)
  def massive = input.copy(clazz = input.clazz.toOption.orEmpty |+| LucumaStyles.Massive)

extension [A](
  input: FormInputTextView[View, Option[A]]
)(using Eq[Option[A]])
  def clearable: FormInputTextView[View, Option[A]] =
    input.value.get.filter(_ => input.disabled.contains(false)).fold(input) { _ =>
      val newAddon =
        <.span(^.cls := (LucumaStyles.BlendedAddon |+| LucumaStyles.IconTimes).htmlClass,
               ^.onClick --> input.value.set(none)
        )
      // will go before other addons, but the units will still be first.
      input.copy(postAddons = newAddon :: input.postAddons)
    }
