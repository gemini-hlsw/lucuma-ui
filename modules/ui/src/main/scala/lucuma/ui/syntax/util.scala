// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.Monoid
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.React
import japgolly.scalajs.react.Reusable
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.EnumValue

import scala.scalajs.js

trait util:
  extension [A](a: A | Unit)(using ev: EnumValue[A])
    def undefToJs: js.UndefOr[String] = a.map(ev.value)

  given Monoid[VdomNode] = new Monoid[VdomNode]:
    val empty: VdomNode = EmptyVdom
    def combine(x: VdomNode, y: VdomNode): VdomNode = React.Fragment(x, y)

  given Monoid[TagMod] = new Monoid[TagMod]:
    val empty: TagMod = TagMod.empty
    def combine(x: TagMod, y: TagMod): TagMod = TagMod(x, y)

  extension (c: Callback.type) def pprintln[T](a: T): Callback = Callback(_root_.pprint.pprintln(a))

  extension [A](reusableList: Reusable[List[A]])
    def sequenceList: List[Reusable[A]] =
      reusableList.value.map(x => reusableList.map(_ => x))

object util extends util
