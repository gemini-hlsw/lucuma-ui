// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.CtorType
import japgolly.scalajs.react.component.ScalaFn
import japgolly.scalajs.react.vdom._
import japgolly.scalajs.react.vdom.html_<^._
import react.common.Css
import react.common.GenericComponentPA
import react.common.GenericComponentPAC
import react.common.GenericComponentPACF
import react.common.GenericComponentPC
import react.common.GenericFnComponentPA
import react.common.GenericFnComponentPAC
import react.common.GenericFnComponentPC
import react.common.ReactRender
import react.common.implicits._

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
end render

trait css:
  given Conversion[Css, TagMod] =
    ^.className := _.htmlClass
end css

trait mod:
  // Syntaxis for apply
  extension [P <: js.Object, A](c: GenericFnComponentPC[P, A])
    def apply(children: VdomNode*): A = c.withChildren(children)

  extension [P <: js.Object, A](c: GenericFnComponentPA[P, A])
    def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericFnComponentPAC[P, A])
    def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  extension [P <: js.Object, A](c: GenericComponentPAC[P, A])
    def apply(modifiers: TagMod*): A = c.addModifiers(modifiers)

  // type FnReactRender[P] = // , CT[-p, +u] <: CtorType[p, u]] =
  //   ReactRender[P, ?, ScalaFn.Unmounted[P]]
  // given Conversion[FnReactRender[?], VdomNode] = _.toUnmounted
  @inline implicit def fnProps2Component[Props, CT[-p, +u] <: CtorType[p, u]](
    p: ReactRender[Props, CT, ScalaFn.Unmounted[Props]]
  ): VdomElement =
    p.toUnmounted
end mod
