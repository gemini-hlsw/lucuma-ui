// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CtorType
import japgolly.scalajs.react.component.Scala
import japgolly.scalajs.react.component.ScalaFn
import japgolly.scalajs.react.component.ScalaForwardRef
import japgolly.scalajs.react.vdom.*
import japgolly.scalajs.react.vdom.html_<^.*
import react.common.GenericComponentP
import react.common.GenericComponentPA
import react.common.GenericComponentPAC
import react.common.GenericComponentPACF
import react.common.GenericComponentPC
import react.common.GenericFnComponentP
import react.common.GenericFnComponentPA
import react.common.GenericFnComponentPAC
import react.common.GenericFnComponentPC
import react.common.ReactRender

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait mod:
  // Syntaxis for apply
  extension [P <: js.Object, A](c: GenericFnComponentPC[P, A])
    inline def apply(children:     VdomNode*): A = c.withChildren(children)

  extension [P <: js.Object, A](c: GenericFnComponentPA[P, A])
    inline def apply(modifiers:    TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericFnComponentPAC[P, A])
    inline def apply(modifiers:    TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericComponentPAC[P, A])
    inline def apply(modifiers:    TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericComponentPC[P, A])
    inline def apply(children:     VdomNode*): A = c.withChildren(children)

  given propsForwardRef2Component[Props, R, CT[-p, +u] <: CtorType[p, u]]
    : Conversion[ReactRender[Props, CT, ScalaForwardRef.Unmounted[Props, R]], VdomNode] =
    _.toUnmounted

  extension [A](c: js.UndefOr[A => Callback])
    def toJs: js.UndefOr[js.Function1[A, Unit]] = c.map(x => (a: A) => x(a).runNow())

  given fnProps2Component[Props, CT[-p, +u] <: CtorType[p, u]]
    : Conversion[ReactRender[Props, CT, ScalaFn.Unmounted[Props]], VdomElement] =
    _.toUnmounted

  given props2Component[Props, S, B, CT[-p, +u] <: CtorType[p, u]]
    : Conversion[ReactRender[Props, CT, Scala.Unmounted[Props, S, B]], VdomElement] =
    _.toUnmounted

object mod extends mod
