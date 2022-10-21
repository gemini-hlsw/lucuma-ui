// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import react.common.Css
import react.primereact.Button
import reactST.StBuildingComponent

import scalajs.js

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
