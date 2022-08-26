// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.enums

import cats.effect.Sync
import cats.syntax.all.given
import lucuma.core.util.Enumerated
import org.scalajs.dom
import react.common.style.Css

enum Theme(val tag: String, val clazz: Css):
  case Light extends Theme("light", Css("light-theme"))
  case Dark  extends Theme("dark", Css("dark-theme"))

  def setup[F[_]](using F: Sync[F]): F[Unit] =
    F.delay {
      dom.document.body.classList.add(this.clazz.htmlClass)
      Theme.values
        .filterNot(_ === this)
        .foreach(otherTheme => dom.document.body.classList.remove(otherTheme.clazz.htmlClass))
    }

object Theme:
  val Default: Theme = Theme.Light

  def init[F[_]](using F: Sync[F]): F[Theme] =
    Default.setup >> F.pure(Default)

  def current[F[_]](using F: Sync[F]): F[Theme] =
    F.delay(
      Theme.values.find(theme => dom.document.body.classList.contains(theme.clazz.htmlClass))
    ).flatMap(_.fold(init)(F.pure))

  given Enumerated[Theme] = Enumerated.from(Light, Dark).withTag(_.tag)
