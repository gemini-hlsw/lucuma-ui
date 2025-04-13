// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import lucuma.react.fa.*

import scala.scalajs.js
import scala.scalajs.js.annotation.*

object LucumaIcons:
  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCircle")
  private val faCircle: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCircleNotch")
  private val faCircleNotch: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-regular-svg-icons", "faCircleInfo")
  val faCircleInfo: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-duotone-svg-icons", "faGears")
  val faGears: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-solid-svg-icons", "faCircleSmall")
  val faCircleSmall: FAIcon = js.native

  FontAwesome.library.add(
    faCircle,
    faCircleNotch,
    faCircleInfo,
    faGears,
    faCircleSmall
  )

  inline def Circle      = FontAwesomeIcon(faCircle)
  inline def CircleNotch = FontAwesomeIcon(faCircleNotch).withSpin()
  inline def CircleInfo  = FontAwesomeIcon(faCircleInfo)
  inline def Gears       = FontAwesomeIcon(faGears)
  inline def CircleSmall = FontAwesomeIcon(faCircleSmall)
