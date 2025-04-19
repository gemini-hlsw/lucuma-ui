// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.enums

import cats.syntax.all.*
import japgolly.scalajs.react.ReactCats.*
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.util.DefaultEffects.Sync as DefaultS
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.style.Css
import mouse.boolean.given
import org.scalajs.dom

private def bodyClassList = dom.document.body.classList

private val LightThemeClass  = Css("light-theme")
private val DarkThemeClass   = Css("dark-theme")
private val SystemThemeClass = Css("system-theme")

enum Theme(private val tag: String, val name: String) derives Enumerated:
  case Light  extends Theme("light", "Light")
  case Dark   extends Theme("dark", "Dark")
  case System extends Theme("system", "System")

  lazy val mount: DefaultS[Unit] = this match
    case Light  => Theme.mountLight(false)
    case Dark   => Theme.mountDark(false)
    case System =>
      Theme.preferredLight.flatMap(_.fold(Theme.mountLight(true), Theme.mountDark(true)))

object Theme:
  private lazy val preferredLightQuery: DefaultS[dom.MediaQueryList] =
    DefaultS.delay:
      dom.window.matchMedia("(prefers-color-scheme: light)")

  private lazy val preferredLight: DefaultS[Boolean] =
    preferredLightQuery.map(_.matches)

  private def adjustClasses(add: Css, remove: Css, systemPreferred: Boolean): DefaultS[Unit] =
    DefaultS.delay:
      bodyClassList.remove(SystemThemeClass.htmlClass)
      (add |+| SystemThemeClass.when_(systemPreferred)).value.foreach(bodyClassList.add)
      remove.value.foreach(bodyClassList.remove)

  private def mountLight(systemPreferred: Boolean): DefaultS[Unit] =
    adjustClasses(LightThemeClass, DarkThemeClass, systemPreferred)

  private def mountDark(systemPreferred: Boolean): DefaultS[Unit] =
    adjustClasses(DarkThemeClass, LightThemeClass, systemPreferred)

  private lazy val currentClasses: DefaultS[Option[(Boolean, Boolean)]] = // (isLight, isSystem)
    DefaultS.delay:
      val isLight = bodyClassList.contains(LightThemeClass.htmlClass)
      val isDark  = bodyClassList.contains(DarkThemeClass.htmlClass)
      if (isLight || isDark)
        (isLight, bodyClassList.contains(SystemThemeClass.htmlClass)).some
      else
        none

  private lazy val mountListener: DefaultS[Unit] =
    preferredLightQuery.flatMap: query =>
      DefaultS.delay:
        query.addEventListener(
          "change",
          (e: dom.MediaQueryList) =>
            DefaultS.runSync:
              currentClasses.flatMap:
                case Some((_, isSystem)) =>
                  isSystem.fold(e.matches.fold(mountLight(true), mountDark(true)), DefaultS.empty)
                case _                   => DefaultS.empty
        )

  lazy val current: DefaultS[Option[Theme]] =
    currentClasses.map:
      _.map: (isLight, isSystem) =>
        if (isSystem) Theme.System
        else if (isLight) Theme.Light
        else Theme.Dark

  def init(default: Theme = Theme.System): DefaultS[Theme] =
    current.flatMap:
      _.fold(mountListener >> default.mount.as(default))(DefaultS.pure(_))

  given Display[Theme] = Display.byShortName(_.name)

  given Reusability[Theme] = Reusability.byEq
