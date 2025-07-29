// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.react

import cats.Monoid
import japgolly.scalajs.react.React
import japgolly.scalajs.react.vdom.html_<^.*

given Monoid[VdomNode] = new Monoid[VdomNode]:
  val empty: VdomNode                             = EmptyVdom
  def combine(x: VdomNode, y: VdomNode): VdomNode = React.Fragment(x, y)

given Monoid[VdomElement] = new Monoid[VdomElement]:
  val empty: VdomElement                                   = React.Fragment()
  def combine(x: VdomElement, y: VdomElement): VdomElement = React.Fragment(x, y)

given Monoid[TagMod] = new Monoid[TagMod]:
  val empty: TagMod                         = TagMod.empty
  def combine(x: TagMod, y: TagMod): TagMod = TagMod(x, y)
