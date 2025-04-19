// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.layout

import lucuma.react.common.Css

object LayoutStyles:
  val MainGrid: Css     = Css("main-grid")
  val MainHeader: Css   = Css("main-header")
  val MainBody: Css     = Css("main-body")
  val MainTitle: Css    = Css("main-title")
  val MainUserName: Css = Css("main-user-name")

  // used with MainBody to display a `Message` below the rest of the MainBody
  val WithMessage: Css = Css("with-message")
