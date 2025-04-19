// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.Reusable
import lucuma.react.common.EnumValue
import org.scalajs.dom

import scala.scalajs.js

trait util:
  extension [A](a: A | Unit)(using ev: EnumValue[A])
    def undefToJs: js.UndefOr[String] = a.map(ev.value)

  extension [A](reusableList: Reusable[List[A]])
    def sequenceList: List[Reusable[A]] =
      reusableList.value.map(x => reusableList.map(_ => x))

  extension (element: dom.Element)
    /**
     * Scroll the element into view if it is not fully visible.
     */
    def scrollIfNeeded: Callback = Callback.lift(() =>
      val rect = element.getBoundingClientRect()
      if (rect.top < 0) element.scrollIntoView()
      if (rect.bottom > dom.window.innerHeight) element.scrollIntoView(false)
    )

object util extends util
