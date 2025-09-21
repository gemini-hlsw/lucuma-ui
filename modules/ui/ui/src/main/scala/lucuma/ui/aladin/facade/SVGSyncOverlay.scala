// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin.facade

import org.scalajs.dom

import scala.scalajs.js
import scala.util.Random

@js.native
trait AladinCoordinates extends js.Object:
  val ra: Double = js.native
  val dec: Double = js.native

class SVGSyncOverlay(
  val name: String = "SVG Synchronizer",
  val svgSelectors: js.Array[String] = js.Array(".visualization-overlay-svg", ".targets-overlay-svg"),
  val baseCoordinates: js.UndefOr[AladinCoordinates] = js.undefined
) extends js.Object:

  val uuid: String = s"svg-sync-${Random.alphanumeric.take(9).mkString}"
  val `type`: String = "svg-sync-overlay"
  var isShowing: Boolean = true

  // Internal state
  private var view: js.UndefOr[AladinView] = js.undefined
  private var aladin: js.UndefOr[JsAladin] = js.undefined
  private var dragStartCoords: js.UndefOr[AladinCoordinates] = js.undefined
  private var wasRecentlyDragging: Boolean = false

  def setView(v: AladinView): Unit =
    view = v
    aladin = v.aladin.asInstanceOf[JsAladin]
    // Apply initial positioning transform to account for current view offset
    applyInitialTransform()

  def show(): Unit =
    if (!isShowing) {
      isShowing = true
      reportChange()
    }

  def hide(): Unit =
    if (isShowing) {
      isShowing = false
      resetSVGTransforms()
      reportChange()
    }

  def setColor(color: String): Unit = () // No-op for this overlay type

  def draw(@annotation.unused ctx: js.Any): Unit =
    if (!isShowing || view.isEmpty || aladin.isEmpty) return

    try {
      synchronizeSVGElements()
    } catch {
      case e: Exception =>
        dom.console.warn("SVGSyncOverlay: Error during synchronization:", e.getMessage)
    }

  def destroy(): Unit =
    resetSVGTransforms()
    dragStartCoords = js.undefined
    view = js.undefined
    aladin = js.undefined

  private def synchronizeSVGElements(): Unit =
    val isDragging = view.get.realDragging.getOrElse(false)

    if (isDragging) {
      handleDragState()
    } else {
      handleNonDragState()
    }

  private def handleDragState(): Unit =
    val raDec = aladin.get.getRaDec()
    if (raDec.length < 2) return

    val currentCoords = js.Dynamic.literal(
      ra = raDec(0),
      dec = raDec(1)
    ).asInstanceOf[AladinCoordinates]

    // Initialize drag start coordinates
    if (dragStartCoords.isEmpty) {
      dragStartCoords = currentCoords
      return
    }

    // Apply direct CSS transform using Aladin's coordinate system
    applyDirectCSSTransform(dragStartCoords.get, currentCoords)
    wasRecentlyDragging = true

  private def handleNonDragState(): Unit =
    if (wasRecentlyDragging) {
      // Just finished dragging - recalculate positioning based on new Aladin center
      resetSVGTransforms()
      applyInitialTransform()
      dragStartCoords = js.undefined
      wasRecentlyDragging = false
    }

  private def applyDirectCSSTransform(startCoords: AladinCoordinates, currentCoords: AladinCoordinates): Unit =
    try {
      baseCoordinates.toOption match {
        case Some(base) =>
          // Calculate the current total offset from base coordinates
          val basePix = aladin.get.world2pix(base.ra, base.dec)
          val currentPix = aladin.get.world2pix(currentCoords.ra, currentCoords.dec)

          if (basePix.length < 2 || currentPix.length < 2) return

          // Calculate the total offset from base to current position
          val totalOffsetX = currentPix(0) - basePix(0)
          val totalOffsetY = currentPix(1) - basePix(1)

          // Apply the total offset (this includes both initial positioning + drag delta)
          applySVGTransforms(totalOffsetX, totalOffsetY)

        case None =>
          // Fallback to relative drag behavior if no base coordinates provided
          val startPix = aladin.get.world2pix(startCoords.ra, startCoords.dec)
          val currentPix = aladin.get.world2pix(currentCoords.ra, currentCoords.dec)

          if (startPix.length < 2 || currentPix.length < 2) return

          val deltaX = currentPix(0) - startPix(0)
          val deltaY = currentPix(1) - startPix(1)

          applySVGTransforms(deltaX, deltaY)
      }

    } catch {
      case e: Exception =>
        dom.console.warn("SVGSyncOverlay: Error applying CSS transform:", e.getMessage)
    }

  private def applyInitialTransform(): Unit =
    baseCoordinates.toOption.foreach { base =>
      try {
        // Get current view center coordinates
        val raDec = aladin.get.getRaDec()
        if (raDec.length >= 2) {
          val currentCoords = js.Dynamic.literal(
            ra = raDec(0),
            dec = raDec(1)
          ).asInstanceOf[AladinCoordinates]

          // Calculate the offset from base coordinates to current view position
          val basePix = aladin.get.world2pix(base.ra, base.dec)
          val currentPix = aladin.get.world2pix(currentCoords.ra, currentCoords.dec)

          if (basePix.length >= 2 && currentPix.length >= 2) {
            // Apply the offset that positions SVG overlays correctly for current view
            val offsetX = currentPix(0) - basePix(0)
            val offsetY = currentPix(1) - basePix(1)

            applySVGTransforms(offsetX, offsetY)
          }
        }
      } catch {
        case e: Exception =>
          dom.console.warn("SVGSyncOverlay: Error applying initial transform:", e.getMessage)
      }
    }


  private def applySVGTransforms(deltaX: Double, deltaY: Double): Unit =
    svgSelectors.foreach { selector =>
      val element = dom.document.querySelector(selector)
      if (element != null) {
        val transform = s"translate3d(${deltaX}px, ${deltaY}px, 0)"
        element.asInstanceOf[js.Dynamic].style.transform = transform
      }
    }

  private def resetSVGTransforms(): Unit =
    svgSelectors.foreach { selector =>
      val element = dom.document.querySelector(selector)
      if (element != null) {
        element.asInstanceOf[js.Dynamic].style.transform = ""
      }
    }

  private def reportChange(): Unit =
    view.foreach { v =>
      if (js.typeOf(v.asInstanceOf[js.Dynamic].requestRedraw) == "function") {
        val _: js.Dynamic = v.asInstanceOf[js.Dynamic].requestRedraw()
      }
    }