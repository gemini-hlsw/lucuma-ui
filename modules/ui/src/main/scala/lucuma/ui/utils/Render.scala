// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.syntax.display.*
import lucuma.core.util.Display

trait Render[A]:
  def renderVdom(value: A): VdomNode

object Render:
  def apply[A: Render]: Render[A] = summon[Render[A]]

  def by[A](f: A => VdomNode): Render[A] =
    new Render:
      def renderVdom(value: A) = f(value)

  def byShortName[A: Display]: Render[A] = by(_.shortName)

  def byLongName[A: Display]: Render[A] = by(_.longName)
