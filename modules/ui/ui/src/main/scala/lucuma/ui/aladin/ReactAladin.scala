// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import cats.syntax.option.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.*
import japgolly.scalajs.react.hooks.Hooks.UseState
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*
import org.scalajs.dom.AbortController
import org.scalajs.dom.AbortSignal
import org.scalajs.dom.EventListenerOptions
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html

import scala.scalajs.js

object EventListenerOptions:
  def apply(
    capture: Boolean = false,
    signal:  js.UndefOr[AbortSignal] = js.undefined
  ): EventListenerOptions =
    js.Dynamic
      .literal(
        capture = capture,
        signal = signal
      )
      .asInstanceOf[EventListenerOptions]

extension (a: Aladin)
  def size: Size = Size(a.getSize()(0), a.getSize()(1))

  def isZooming: Boolean =
    a.view.zoom.isZooming.toOption.getOrElse(false)

  def fov: Fov =
    Fov(Angle.fromDoubleDegrees(a.getFov()(0)), Angle.fromDoubleDegrees(a.getFov()(1)))

  def onPositionChangedCB(cb: PositionChanged => Callback): Callback =
    Callback(
      a.on("positionChanged", (o: JsPositionChanged) => cb(PositionChanged.fromJs(o)).runNow())
    )

  def onZoomCB(cb: Fov => Callback): Callback =
    Callback(a.on("zoomChanged", (_: Double) => cb(fov).runNow()))

  def onZoomCB(cb: => Callback): Callback =
    Callback(a.on("zoomChanged", (_: Double) => cb.runNow()))

  def onFullScreenToggleCB(cb: Boolean => Callback): Callback =
    Callback(a.on("fullScreenToggled", (t: Boolean) => cb(t).runNow()))

  def onFullScreenToggleCB(cb: => Callback): Callback =
    Callback(a.on("fullScreenToggled", (_: Boolean) => cb.runNow()))

  def onMouseMoveCB(cb: MouseMoved => Callback): Callback =
    Callback(a.on("mouseMove", (t: JsMouseMoved) => cb(MouseMoved.fromJs(t)).runNow()))

  def pixelScale: PixelScale =
    PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))

  def applyZoom(zoomFactor: Double, duration: Int = 200): Callback =
    Callback(
      a.view.zoom.applyZoom(ZoomTo(zoomFactor, duration))
    )

  def increaseZoomCB: Callback =
    Callback(a.increaseZoom())

  def increaseZoomCB(f: Double): Callback =
    applyZoom(a.getZoomFactor() / f)

  def increaseZoomCB(f: Double, duration: Int): Callback =
    applyZoom(a.getZoomFactor() / f, duration)

  def decreaseZoomCB: Callback =
    Callback(a.decreaseZoom())

  def decreaseZoomCB(f: Double): Callback =
    applyZoom(a.getZoomFactor() * f)

  def decreaseZoomCB(f: Double, duration: Int): Callback =
    applyZoom(a.getZoomFactor() * f, duration)

  def recalculateViewCB: Callback =
    Callback(a.recalculateView())

  def requestRedrawCB: Callback =
    Callback(a.requestRedraw())

  def gotoRaDecCB(c: Coordinates): Callback =
    Callback(a.gotoRaDec(c.ra.toAngle.toDoubleDegrees, c.dec.toAngle.toSignedDoubleDegrees))

  def setFovCB(f: Fov): Callback =
    Callback(a.setFov(f.x.toDoubleDegrees))

  def setViewMode(m: ViewMode): Callback =
    Callback(a.view.setMode(m.value))

case class ReactAladin(
  clazz:          Css = Css.Empty,
  options:        AladinOptions = AladinOptions.Default,
  customize:      js.UndefOr[Aladin => Callback] = js.undefined,
  panningEnabled: Boolean = true,
  modifiers:      Seq[TagMod] = Seq.empty
)(using val R: Reusability[AladinOptions])
    extends ReactFnProps(ReactAladin):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object ReactAladin
    extends ReactFnComponent[ReactAladin](props =>

      given Reusability[ReactAladin] = {
        given Reusability[AladinOptions] = props.R
        Reusability.by[ReactAladin, (Css, AladinOptions)](x => (x.clazz, x.options))
      }

      def cleanupListener(
        abortRef: UseState[Option[AbortController]]
      ): Callback =
        abortRef.value
          .map: c =>
            Callback(c.abort()) *> abortRef.setState(none)
          .getOrEmpty

      def resetAladin(
        r:         CallbackTo[Option[html.Div]],
        state:     UseState[Boolean],
        aladinRef: UseState[Option[Aladin]],
        abortRef:  UseState[Option[AbortController]],
        props:     ReactAladin,
        force:     Boolean
      ): Callback =
        r.flatMap {
          case Some(e) if force || !state.value =>
            // Clean up the listener before creating new Aladin instance
            cleanupListener(abortRef) *>
              CallbackTo(A.aladin(e, props.options)).flatMap { a =>
                state.setState(true) *>
                  aladinRef.setState(Some(a)) *>
                  props.customize.fold(Callback.empty)(f => f(a))
              }
          case _                                => Callback.empty
        }

      for {
        init      <- useState(false)
        aladinRef <- useState(none[Aladin])
        r         <- useRefToVdom[html.Div]
        abortRef  <- useState(none[AbortController])
        _         <- useEffectWithDeps(props) { _ =>
                       init.setState(true) *> resetAladin(r.get, init, aladinRef, abortRef, props, true)
                     }
        _         <- useLayoutEffectOnMount {
                       AsyncCallback.fromCallbackToJsPromise(CallbackTo(A.init)).toCallback *>
                         resetAladin(r.get, init, aladinRef, abortRef, props, false)
                     }
        _         <-
          useEffectWithDeps(props.panningEnabled): enabled =>
            aladinRef.value
              .map: aladin =>
                val catalogCanvas = aladin.view.catalogCanvas
                if (!enabled) {
                  // Disable panning
                  abortRef.value match {
                    case Some(_) => Callback.empty
                    case None    =>
                      // we can use abort controller to remove the listener cleanly
                      val controller = new AbortController()

                      // don'l let mouse down propagate
                      val listener: js.Function1[MouseEvent, Unit] = (e: MouseEvent) => {
                        e.stopImmediatePropagation()
                        e.preventDefault()
                      }

                      val options =
                        EventListenerOptions(capture = true, signal = controller.signal)

                      Callback(catalogCanvas.addEventListener("mousedown", listener, options)) *>
                        Callback(catalogCanvas.addEventListener("wheel", listener, options)) *>
                        abortRef.setState(Some(controller))
                  }
                } else {
                  // Enable panning
                  cleanupListener(abortRef)
                }
              .getOrEmpty
      } yield <.div(props.clazz, ^.untypedRef := r)
    )
