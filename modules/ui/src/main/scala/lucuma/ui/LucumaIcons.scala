// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
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

  @js.native
  @JSImport("@fortawesome/pro-light-svg-icons", "faPlus")
  val faThinPlus: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-light-svg-icons", "faHyphen")
  val faThinMinus: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-thin-svg-icons", "faArrowUpRightAndArrowDownLeftFromCenter")
  val faExpandDiagonal: FAIcon = js.native

  @js.native
  @JSImport("@fortawesome/pro-thin-svg-icons", "faArrowDownLeftAndArrowUpRightToCenter")
  val faContractDiagonal: FAIcon = js.native

  FontAwesome.library.add(
    faCircle,
    faCircleNotch,
    faCircleInfo,
    faGears,
    faCircleSmall,
    faThinPlus,
    faThinMinus,
    faExpandDiagonal,
    faContractDiagonal
  )

  inline def Circle           = FontAwesomeIcon(faCircle)
  inline def CircleNotch      = FontAwesomeIcon(faCircleNotch).withSpin()
  inline def CircleInfo       = FontAwesomeIcon(faCircleInfo)
  inline def Gears            = FontAwesomeIcon(faGears)
  inline def CircleSmall      = FontAwesomeIcon(faCircleSmall)
  inline def ThinPlus         = FontAwesomeIcon(faThinPlus)
  inline def ThinMinus        = FontAwesomeIcon(faThinMinus)
  inline def ExpandDiagonal   = FontAwesomeIcon(faExpandDiagonal)
  inline def ContractDiagonal = FontAwesomeIcon(faContractDiagonal)
