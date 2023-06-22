// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.vdom.*
import react.common.GenericComponentP
import react.common.GenericComponentPA
import react.common.GenericComponentPAC
import react.common.GenericComponentPACF
import react.common.GenericComponentPC
import react.common.GenericFnComponentP
import react.common.GenericFnComponentPA
import react.common.GenericFnComponentPAC

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait components:
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

object components extends components
