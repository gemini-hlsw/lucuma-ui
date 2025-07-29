// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.primereact

import cats.syntax.all.*
import crystal.react.*
import japgolly.scalajs.react.Callback

trait ViewLike[VL[_]]:
  extension [A](v: VL[A])
    def get: Option[A]
    def set(a: A): Callback

given ViewLike[View] with
  extension [A](v: View[A])
    def get: Option[A]      = v.get.some
    def set(a: A): Callback = v.set(a)

// Note that any ViewLike[ViewOpt] where the ViewOpt is None will never get set, it only provides
// a way to have a UI until something else makes the ViewOpt a Some. Any control using
// ViewLike[ViewOpt] should probably disabled when the ViewOpt is empty, or handled in some other way.
given ViewLike[ViewOpt] with
  extension [A](v: ViewOpt[A])
    def get: Option[A]      = v.get
    def set(a: A): Callback = v.set(a)
