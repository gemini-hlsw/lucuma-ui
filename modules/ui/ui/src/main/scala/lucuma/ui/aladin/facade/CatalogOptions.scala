// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Image

import scala.scalajs.js

// This will be the props object used from JS-land
@js.native
trait CatalogOptions extends js.Object {
  var name: js.UndefOr[String]
  var color: js.UndefOr[String]
  var sourceSize: js.UndefOr[Double]
  var shape: js.UndefOr[String | Image | CatalogOptions.RawDrawFunction]
  var limit: js.UndefOr[Double]
  var raField: js.UndefOr[String]
  var decField: js.UndefOr[String]
  var displayLabel: js.UndefOr[String]
  var labelColor: js.UndefOr[String]
  var labelFont: js.UndefOr[String]
  var labelColumn: js.UndefOr[String]
  var onClick: js.UndefOr[String | CatalogOptions.RawOnClick]
}

object CatalogOptions {
  type RawOnClick      = js.Function1[AladinSource, Unit]
  type DrawFunction    = (AladinSource, CanvasRenderingContext2D, SourceDraw) => Unit
  type RawDrawFunction = js.Function3[AladinSource, CanvasRenderingContext2D, SourceDraw, Unit]
  type OnClick         = AladinSource => Unit

  def apply(
    name:         js.UndefOr[String] = js.undefined,
    color:        js.UndefOr[String] = js.undefined,
    sourceSize:   js.UndefOr[Double] = js.undefined,
    shape:        js.UndefOr[String | Image | DrawFunction] = js.undefined,
    limit:        js.UndefOr[Double] = js.undefined,
    raField:      js.UndefOr[String] = js.undefined,
    decField:     js.UndefOr[String] = js.undefined,
    displayLabel: js.UndefOr[String] = js.undefined,
    labelColor:   js.UndefOr[String] = js.undefined,
    labelFont:    js.UndefOr[String] = js.undefined,
    labelColumn:  js.UndefOr[String] = js.undefined,
    onClick:      js.UndefOr[String | OnClick] = js.undefined
  ): CatalogOptions = {
    val p = (new js.Object()).asInstanceOf[CatalogOptions]
    p.name = name
    p.color = color.map(c => c: String)
    p.sourceSize = sourceSize
    p.shape = shape.map((_: Any) match {
      case s: String => s
      case i: Image  => i
      case f         =>
        (
          (
            s: AladinSource,
            c: CanvasRenderingContext2D,
            p: SourceDraw
          ) => f.asInstanceOf[DrawFunction](s, c, p)
        ): RawDrawFunction
    })
    p.limit = limit
    p.raField = raField
    p.decField = decField
    p.displayLabel = displayLabel
    p.labelColor = labelColor
    p.labelFont = labelFont
    p.labelColumn = labelColumn
    p.onClick = onClick.map((_: Any) match {
      case s: String => s
      case r         => ((s: AladinSource) => r.asInstanceOf[OnClick](s)): RawOnClick
    })
    p
  }
}
