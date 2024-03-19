// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import scala.annotation.nowarn
import scala.scalajs.js
import scala.scalajs.js.annotation.*

@nowarn
@js.native
trait Browser extends js.Object {
  val name: String    = js.native
  val version: String = js.native
}

sealed trait UAParser extends js.Object {
  def getBrowser(): Browser
}

object UAParser {
  def apply(ua: String): UAParser =
    if (scala.scalajs.runtime.linkingInfo.productionMode) new UAParserProd(ua)
    else new UAParserDev(ua)

  @js.native
  @JSImport("ua-parser-js", JSImport.Namespace)
  class UAParserDev(val ua: String) extends js.Object with UAParser {
    def getBrowser(): Browser = js.native
  }

  @js.native
  @JSImport("ua-parser-js", JSImport.Default)
  class UAParserProd(val ua: String) extends js.Object with UAParser {
    def getBrowser(): Browser = js.native
  }

}
