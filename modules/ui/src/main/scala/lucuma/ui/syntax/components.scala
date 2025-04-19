// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.GenericComponentP
import lucuma.react.common.GenericComponentPA
import lucuma.react.common.GenericComponentPAC
import lucuma.react.common.GenericComponentPACF
import lucuma.react.common.GenericComponentPC
import lucuma.react.common.GenericFnComponentP
import lucuma.react.common.GenericFnComponentPA
import lucuma.react.common.GenericFnComponentPAC

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait components:
  // Conversion of common components to VdomNode
  type FnPA[P <: js.Object] = GenericFnComponentPA[P, ?]
  given [P <: js.Object]: Conversion[FnPA[P], UndefOr[VdomNode]] = _.render
  given [P <: js.Object]: Conversion[FnPA[P], VdomNode]          = _.render

  type FnPAC[P <: js.Object] = GenericFnComponentPAC[P, ?]
  given [P <: js.Object]: Conversion[FnPAC[P], UndefOr[VdomNode]] = _.render
  given [P <: js.Object]: Conversion[FnPAC[P], VdomNode]          = _.render

  type ClassPAC[P <: js.Object] = GenericComponentPAC[P, ?]
  // Without the explicit `vdomElement` this produces a compiler exception
  given [P <: js.Object]: Conversion[ClassPAC[P], UndefOr[VdomNode]] = _.render.vdomElement
  given [P <: js.Object]: Conversion[ClassPAC[P], VdomNode]          = _.render.vdomElement

  type ClassPC[P <: js.Object] = GenericComponentPC[P, ?]
  given [P <: js.Object]: Conversion[ClassPC[P], UndefOr[VdomNode]] = _.render.vdomElement
  given [P <: js.Object]: Conversion[ClassPC[P], VdomNode]          = _.render.vdomElement

  type ClassPA[P <: js.Object] = GenericComponentPA[P, ?]
  given [P <: js.Object]: Conversion[ClassPA[P], UndefOr[VdomNode]] = _.render.vdomElement
  given [P <: js.Object]: Conversion[ClassPA[P], VdomNode]          = _.render.vdomElement

  type ClassPACF[P <: js.Object, F <: js.Object] = GenericComponentPACF[P, ?, F]
  given [P <: js.Object, F <: js.Object]: Conversion[ClassPACF[P, F], VdomNode] =
    _.render.vdomElement

  type ClassP[P <: js.Object] = GenericComponentP[P]
  given [P <: js.Object]: Conversion[ClassP[P], UndefOr[VdomNode]] = _.render.vdomElement
  given [P <: js.Object]: Conversion[ClassP[P], VdomNode]          = _.render.vdomElement

  type FnP[P <: js.Object] = GenericFnComponentP[P]
  given [P <: js.Object]: Conversion[FnP[P], VdomNode] = _.render

object components extends components
