// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import japgolly.scalajs.react.Reusable
import lucuma.react.common.EnumValue

import scala.scalajs.js

trait util:
  extension [A](a: A | Unit)(using ev: EnumValue[A])
    def undefToJs: js.UndefOr[String] = a.map(ev.value)

  extension [A](reusableList: Reusable[List[A]])
    def sequenceList: List[Reusable[A]] =
      reusableList.value.map(x => reusableList.map(_ => x))

object util extends util
