// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import lucuma.react.common.Css
import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object SequenceIcons:
  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCircle")
  val faCircle: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faSquare")
  val faSquare: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCrosshairs")
  val faCrosshairs: FAIcon = js.native

  // This is tedious but lets us do proper tree-shaking
  FontAwesome.library.add(
    faCircle,
    faSquare,
    faCrosshairs
  )

  // TODO Color
  private def letterLayeredIcon(icon: FontAwesomeIcon, letter: Char, clazz: Css): LayeredIcon =
    LayeredIcon(clazz = clazz, fixedWidth = true)(
      icon,
      TextLayer(letter.toString).withInverse().withSize(IconSize.SM)
    )

  val Circle     = FontAwesomeIcon(faCircle)
  val Crosshairs = FontAwesomeIcon(faCrosshairs)
  val Square     = FontAwesomeIcon(faSquare)

  object StepType:
    val Bias   = letterLayeredIcon(Square, 'B', SequenceStyles.StepType.Bias)
    val Dark   = letterLayeredIcon(Square, 'D', SequenceStyles.StepType.Dark)
    val Arc    = letterLayeredIcon(Square, 'A', SequenceStyles.StepType.Arc)
    val Flat   = letterLayeredIcon(Square, 'F', SequenceStyles.StepType.Flat)
    val Object = letterLayeredIcon(Circle, 'O', SequenceStyles.StepType.Object)
