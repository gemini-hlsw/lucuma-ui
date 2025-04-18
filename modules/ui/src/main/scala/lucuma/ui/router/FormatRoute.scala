// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.router

import japgolly.scalajs.react.extra.router.Path
import japgolly.scalajs.react.extra.router.StaticDsl.Route
import lucuma.core.optics.Format
import monocle.Iso

object optics:
  val pathIso: Iso[Path, String] = Iso[Path, String](_.value.stripPrefix("/"))(s => Path(s"/$s"))

object syntax:
  extension [A, B](iso: Iso[A, B]) // TODO Move to core?
    def andThen[C](format: Format[B, C]): Format[A, C] =
      format.imapA(iso.reverseGet, iso.get)

  extension (routeModule: Route.type)
    def forStringFormat[A](format: Format[String, A]): Route[A] =
      val pathFormat: Format[Path, A] = optics.pathIso.andThen(format)
      Route(pathFormat.getOption, pathFormat.reverseGet)
