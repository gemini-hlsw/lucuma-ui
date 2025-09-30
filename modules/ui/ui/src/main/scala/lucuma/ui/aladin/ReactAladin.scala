// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.*
import japgolly.scalajs.react.hooks.Hooks.UseState
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*
import org.scalajs.dom.html
import cats.syntax.option.*

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import org.scalajs.dom.AbortController
import org.scalajs.dom.MouseEvent

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

      def resetAladin(
        r:         CallbackTo[Option[html.Div]],
        state:     UseState[Boolean],
        aladinRef: UseState[Option[Aladin]],
        props:     ReactAladin,
        force:     Boolean
      ): Callback =
        r.flatMap {
          case Some(e) if force || !state.value =>
            CallbackTo(A.aladin(e, props.options)).flatMap { a =>
              state.setState(true) *>
                aladinRef.setState(Some(a)) *>
                props.customize.fold(Callback.empty)(f => f(a))
            }
          case _                                => Callback.empty
        }

      for {
        init               <- useState(false)
        aladinRef          <- useState(none[Aladin])
        r                  <- useRefToVdom[html.Div]
        abortControllerRef <- useRef(none[AbortController])
        _                  <- useEffectWithDeps(props) { _ =>
                                init.setState(true) *> resetAladin(r.get, init, aladinRef, props, true)
                              }
        _                  <- useLayoutEffectOnMount {
                                AsyncCallback.fromCallbackToJsPromise(CallbackTo(A.init)).toCallback *>
                                  resetAladin(r.get, init, aladinRef, props, false)
                              }
        _                  <- {
                                given Reusability[Aladin] = Reusability.byRef
                                useEffectWithDeps((props.panningEnabled, aladinRef.value)) {
                                  case (enabled, Some(aladin)) =>
                                    val catalogCanvas = aladin.view.catalogCanvas
                                    abortControllerRef.get.flatMap { current =>
                                      if (!enabled) {
                                        // Disable panning - add event listener with AbortController
                                        current match {
                                          case None =>
                                            val controller = new AbortController()
                                            val listener: js.Function1[MouseEvent, Unit] =
                                              (e: MouseEvent) => {
                                                e.stopImmediatePropagation()
                                                e.preventDefault()
                                              }

                                            val options = js.Dynamic.literal(
                                              capture = true,
                                              signal = controller.signal
                                            ).asInstanceOf[org.scalajs.dom.EventListenerOptions]

                                            Callback(catalogCanvas.addEventListener("mousedown", listener, options)) *>
                                              abortControllerRef.set(Some(controller))
                                          case Some(_) => Callback.empty
                                        }
                                      } else {
                                        // Enable panning - abort the controller to remove the listener
                                        current.fold(Callback.empty) { controller =>
                                          Callback(controller.abort()) *>
                                            abortControllerRef.set(none)
                                        }
                                      }
                                    }
                                  case _ => Callback.empty
                                }
                              }
      } yield <.div(props.clazz, ^.untypedRef := r)
    )
