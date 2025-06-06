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
import lucuma.ui.reusability.given
import org.scalajs.dom.html

import scala.scalajs.js

type Aladin = lucuma.ui.aladin.facade.JsAladin

extension (a: Aladin)
  def size: Size = Size(a.getSize()(0), a.getSize()(1))

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

  def increaseZoomCB: Callback =
    Callback(a.increaseZoom())

  def decreaseZoomCB: Callback =
    Callback(a.decreaseZoom())

  def fixLayoutDimensionsCB: Callback =
    Callback(a.fixLayoutDimensions())

  def recalculateViewCB: Callback =
    Callback(a.recalculateView())

  def requestRedrawCB: Callback =
    Callback(a.requestRedraw())

  def gotoRaDecCB(c: Coordinates): Callback =
    Callback(a.gotoRaDec(c.ra.toAngle.toDoubleDegrees, c.dec.toAngle.toSignedDoubleDegrees))

case class ReactAladin(
  clazz:     Css = Css.Empty,
  options:   AladinOptions = AladinOptions.Default,
  target:    js.UndefOr[String] = js.undefined,
  customize: js.UndefOr[Aladin => Callback] = js.undefined,
  modifiers: Seq[TagMod] = Seq.empty
) extends ReactFnProps(ReactAladin):
  inline def addModifiers(modifiers: Seq[TagMod]) = copy(modifiers = this.modifiers ++ modifiers)
  inline def withMods(mods:          TagMod*)     = addModifiers(mods)
  inline def apply(mods:             TagMod*)     = addModifiers(mods)

object ReactAladin
    extends ReactFnComponent[ReactAladin](props =>

      type Props = ReactAladin

      def resetAladin(
        r:     CallbackTo[Option[html.Div]],
        state: UseState[Boolean],
        props: Props,
        force: Boolean
      ): Callback =
        r.flatMap {
          case Some(e) if force || !state.value =>
            CallbackTo(new JsAladin(e, props.options)).flatMap { a =>
              state.setState(true) *>
                props.customize.fold(Callback.empty)(f => f(a))
            }
          case _                                => Callback.empty
        }

      for {
        init <- useState(false)
        r    <- useRefToVdom[html.Div]
        _    <- useLayoutEffectWithDeps((props.clazz, props.options)) { _ =>
                  init.setState(true) *> resetAladin(r.get, init, props, true)
                }
        _    <- useLayoutEffectOnMount {
                  resetAladin(r.get, init, props, false)
                }
      } yield <.div(props.clazz, ^.untypedRef := r)
    )
