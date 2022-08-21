// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CtorType
import japgolly.scalajs.react.component.Scala
import japgolly.scalajs.react.component.ScalaFn
import japgolly.scalajs.react.component.ScalaForwardRef
import japgolly.scalajs.react.vdom._
import japgolly.scalajs.react.vdom.html_<^._
import react.common.Css
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

object all extends render with mod with css

trait render:
  // Conversion of common components to VdomNode
  type FnPA[P <: js.Object] = GenericFnComponentPA[P, ?]
  given Conversion[FnPA[?], UndefOr[VdomNode]] = _.render
  given Conversion[FnPA[?], VdomNode]          = _.render

  type FnPAC[P <: js.Object] = GenericFnComponentPAC[P, ?]
  given Conversion[FnPAC[?], UndefOr[VdomNode]] = _.render
  given Conversion[FnPAC[?], VdomNode]          = _.render

  type ClassPAC[P <: js.Object] = GenericComponentPAC[P, ?]
  // Without the explicit `vdomElement` this produces a compiler exception
  given Conversion[ClassPAC[?], UndefOr[VdomNode]] = _.render.vdomElement
  given Conversion[ClassPAC[?], VdomNode]          = _.render.vdomElement

  type ClassPC[P <: js.Object] = GenericComponentPC[P, ?]
  given Conversion[ClassPC[?], UndefOr[VdomNode]] = _.render.vdomElement
  given Conversion[ClassPC[?], VdomNode]          = _.render.vdomElement

  type ClassPA[P <: js.Object] = GenericComponentPA[P, ?]
  given Conversion[ClassPA[?], UndefOr[VdomNode]] = _.render.vdomElement
  given Conversion[ClassPA[?], VdomNode]          = _.render.vdomElement

  type ClassPACF[P <: js.Object, F <: js.Object] = GenericComponentPACF[P, ?, F]
  given Conversion[ClassPACF[?, ?], VdomNode] = _.render.vdomElement

  type ClassP[P <: js.Object] = GenericComponentP[P]
  given Conversion[ClassP[?], UndefOr[VdomNode]] = _.render.vdomElement
  given Conversion[ClassP[?], VdomNode]          = _.render.vdomElement

  type FnP[P <: js.Object] = GenericFnComponentP[P]
  given Conversion[FnP[?], VdomNode] = _.render

end render

object render extends render

trait css:
  given Conversion[Css, TagMod] =
    ^.className := _.htmlClass
end css

object css extends css

trait mod:
  // Syntaxis for apply
  extension [P <: js.Object, A](c: GenericFnComponentPC[P, A])
    inline def apply(children: VdomNode*): A = c.withChildren(children)

  extension [P <: js.Object, A](c: GenericFnComponentPA[P, A])
    inline def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericFnComponentPAC[P, A])
    inline def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericComponentPAC[P, A])
    inline def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericComponentPC[P, A])
    inline def apply(children: VdomNode*): A = c.withChildren(children)

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

end mod

object mod extends mod
