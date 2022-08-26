// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.implicits

import cats.Monad
import cats.Monoid
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.reuse._
import japgolly.scalajs.react.React
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.optics.SplitEpi
import react.common.EnumValue

import scala.annotation.targetName
import scala.scalajs.js

extension [F[_], A](self: ViewF[F, A])
  def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewF[F, B] =
    self.zoom(splitEpi.get)(splitEpi.modify)

extension [F[_], A](self: ViewOptF[F, A])
  def zoomSplitEpi[B](splitEpi: SplitEpi[A, B]): ViewOptF[F, B] =
    self.zoom(splitEpi.get)(splitEpi.modify)

extension [F[_], A](self: Reuse[ViewF[F, A]])
  def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewF[F, B]] =
    self.zoom(splitEpi.get)(splitEpi.modify)

extension [F[_], A](self: Reuse[ViewOptF[F, A]])
  @targetName("zoomSplitEpiOpt")
  def zoomSplitEpi[B](splitEpi: SplitEpi[A, B])(implicit ev: Monad[F]): Reuse[ViewOptF[F, B]] =
    self.zoom(splitEpi.get)(splitEpi.modify)

extension [A](a: A | Unit)(using ev: EnumValue[A])
  def undefToJs: js.UndefOr[String] = a.map(ev.value)

given Monoid[VdomNode] = new Monoid[VdomNode]:
  val empty: VdomNode                             = EmptyVdom
  def combine(x: VdomNode, y: VdomNode): VdomNode = React.Fragment(x, y)
