// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.enums

import cats.effect.Sync
import cats.syntax.all.given
import lucuma.core.util.Enumerated
import lucuma.react.common.style.Css
import org.scalajs.dom

enum Theme(private val tag: String, val clazz: Css) derives Enumerated:
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
  val Default: Theme = Theme.Dark

  def init[F[_]](using F: Sync[F]): F[Theme] =
    Default.setup >> F.pure(Default)

  def current[F[_]](using F: Sync[F]): F[Theme] =
    F.delay(
      Theme.values.find(theme => dom.document.body.classList.contains(theme.clazz.htmlClass))
    ).flatMap(_.fold(init)(F.pure))
