// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import org.scalajs.dom.html

extension (a: JsAladin)
  def size: Size = Size(a.getSize()(0), a.getSize()(1))

  def fov: Fov =
    Fov(Angle.fromDoubleDegrees(a.getFov()(0)), Angle.fromDoubleDegrees(a.getFov()(1)))

  def onPositionChanged(cb: PositionChanged => Callback): Callback =
    Callback(
      a.on("positionChanged", (o: JsPositionChanged) => cb(PositionChanged.fromJs(o)).runNow())
    )

  def onZoom(cb: Fov => Callback): Callback =
    Callback(a.on("zoomChanged", (_: Double) => cb(fov).runNow()))

  def onZoom(cb: => Callback): Callback =
    Callback(a.on("zoomChanged", (_: Double) => cb.runNow()))

  def onFullScreenToggle(cb: Boolean => Callback): Callback =
    Callback(a.on("fullScreenToggled", (t: Boolean) => cb(t).runNow()))

  def onFullScreenToggle(cb: => Callback): Callback =
    Callback(a.on("fullScreenToggled", (_: Boolean) => cb.runNow()))

  def onMouseMove(cb: MouseMoved => Callback): Callback =
    Callback(a.on("mouseMove", (t: JsMouseMoved) => cb(MouseMoved.fromJs(t)).runNow()))

  def pixelScale: PixelScale =
    PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))

case class Aladin(
  mountNodeClass:           Css,
  target:                   js.UndefOr[String] = js.undefined,
  fov:                      js.UndefOr[Angle] = js.undefined,
  survey:                   js.UndefOr[String] = js.undefined,
  cooFrame:                 js.UndefOr[CooFrame] = js.undefined,
  showReticle:              js.UndefOr[Boolean] = js.undefined,
  showZoomControl:          js.UndefOr[Boolean] = js.undefined,
  showFullscreenControl:    js.UndefOr[Boolean] = js.undefined,
  showLayersControl:        js.UndefOr[Boolean] = js.undefined,
  showGotoControl:          js.UndefOr[Boolean] = js.undefined,
  showShareControl:         js.UndefOr[Boolean] = js.undefined,
  showSimbadPointerControl: js.UndefOr[Boolean] = js.undefined,
  showFrame:                js.UndefOr[Boolean] = js.undefined,
  showCoordinates:          js.UndefOr[Boolean] = js.undefined,
  showFov:                  js.UndefOr[Boolean] = js.undefined,
  fullScreen:               js.UndefOr[Boolean] = js.undefined,
  reticleColor:             js.UndefOr[String] = js.undefined,
  reticleSize:              js.UndefOr[Double] = js.undefined,
  imageSurvey:              js.UndefOr[String] = js.undefined,
  baseImageLayer:           js.UndefOr[String] = js.undefined,
  customize:                js.UndefOr[JsAladin => Callback] = js.undefined
) extends ReactFnProps(Aladin.component):
  def render = Aladin.component(this)

object Aladin:

  type Props = Aladin

  case class State(a: Option[JsAladin])

  // def runOnAladinOpt[A](f: JsAladin => A): CallbackOption[A] =
  //   bs.state.map {
  //     case State(Some(a)) => f(a).some
  //     case _              => none
  //   }.asCBO
  //
  // def runOnAladinCB[A](f: JsAladin => CallbackTo[A]): Callback =
  //   bs.state.flatMap {
  //     case State(Some(a)) => f(a).void
  //     case _              => Callback.empty
  //   }
  //
  // def runOnAladin[A](f: JsAladin => A): Callback =
  //   bs.state.flatMap {
  //     case State(Some(a)) => CallbackTo(f(a)).void
  //     case _              => Callback.empty
  //   }

  // def render(props: Props): VdomElement = <.div(props.mountNodeClass)
  //
  // def gotoRaDec(ra: Double, dec: Double): Callback = runOnAladin(_.gotoRaDec(ra, dec))
  //
  // def box: Callback = runOnAladin(_.box())
  //
  // def getFovForObject(
  //   objectName: String,
  //   cb:         Double => Callback = _ => Callback.empty
  // ): Callback =
  //   runOnAladin(_.getFovForObject(objectName, (fov: Double) => cb(fov).runNow()))
  //
  // def world2pixFn: CallbackTo[Coordinates => Option[(Double, Double)]] =
  //   runOnAladinOpt { j => (c: Coordinates) =>
  //     val ra  = c.ra.toAngle.toDoubleDegrees
  //     val dec = c.dec.toAngle.toSignedDoubleDegrees
  //     val p   = j.world2pix(ra, dec)
  //     Option(p).filter(_.length == 2).map(p => (p(0), p(1)))
  //   }.getOrElse((_: Coordinates) => None)
  //
  // def pix2worldFn: CallbackTo[(Int, Int) => Option[Coordinates]] =
  //   runOnAladinOpt { j => (x: Int, y: Int) =>
  //     {
  //       val p = j.pix2world(x.toDouble, y.toDouble)
  //       Option(p).filter(_.length == 2).flatMap { p =>
  //         val ra  = RightAscension.fromDoubleDegrees(p(0))
  //         val dec = Declination.fromDoubleDegrees(p(1))
  //         dec.map(Coordinates(ra, _))
  //       }
  //     }
  //   }.getOrElse((_, _) => None)
  //
  // def world2pix(c: Coordinates): CallbackTo[Option[(Double, Double)]] =
  //   runOnAladinOpt { j =>
  //     val ra  = c.ra.toAngle.toDoubleDegrees
  //     val dec = c.dec.toAngle.toSignedDoubleDegrees
  //     val p   = j.world2pix(ra, dec)
  //     Option(p).filter(_.length == 2).map(p => (p(0), p(1)))
  //   }.getOrElse(None)
  //
  // def pix2world(x: Int, y: Int): CallbackTo[Option[Coordinates]] =
  //   runOnAladinOpt { j =>
  //     val p = j.pix2world(x.toDouble, y.toDouble)
  //     Option(p).filter(_.length == 2).flatMap { p =>
  //       val ra  = RightAscension.fromDoubleDegrees(p(0))
  //       val dec = Declination.fromDoubleDegrees(p(1))
  //       dec.map(Coordinates(ra, _))
  //     }
  //   }.getOrElse(None)

  // def getRaDec: CallbackTo[Coordinates] =
  //   runOnAladinOpt(_.getRaDec())
  //     .flatMapOption { a =>
  //       (RightAscension.fromHourAngle
  //          .get(Angle.hourAngle.get(Angle.fromDoubleDegrees(a(0))))
  //          .some,
  //        Declination.fromAngle.getOption(Angle.fromDoubleDegrees(a(1)))
  //       ).mapN(Coordinates.apply)
  //     }
  //     .getOrElse(Coordinates.Zero)
  //
  // def gotoObject(q: String, cb: (Double, Double) => Callback, er: Callback): Callback =
  //   runOnAladin(_.gotoObject(q, new GoToObjectCallback(cb, er)))
  //
  // def recalculateView: Callback =
  //   runOnAladin(_.recalculateView())
  //
  // def fixLayoutDimensions: Callback =
  //   runOnAladin(_.fixLayoutDimensions())
  //
  // def requestRedraw: Callback =
  //   runOnAladin(_.requestRedraw())
  //
  // def increaseZoom: Callback =
  //   runOnAladin(_.increaseZoom())
  //
  // def decreaseZoom: Callback =
  //   runOnAladin(_.decreaseZoom())
  //
  // def setZoom(fovDegrees: Double): Callback =
  //   runOnAladin(_.setZoom(fovDegrees))
  //
  // def toggleFullscreen: Callback =
  //   runOnAladin(_.toggleFullscreen())
  //
  // def addCatalog(cat: AladinCatalog): Callback =
  //   runOnAladin(_.addCatalog(cat))
  //
  // def pixelScale: CallbackTo[PixelScale] =
  //   runOnAladinOpt(a => PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1)))
  //     .getOrElse(PixelScale.Default)

  // class Backend(bs: BackendScope[Aladin, State]) {
  //   def runOnAladinOpt[A](f: JsAladin => A): CallbackOption[A] =
  //     bs.state.map {
  //       case State(Some(a)) => f(a).some
  //       case _              => none
  //     }.asCBO
  //
  //   def runOnAladinCB[A](f: JsAladin => CallbackTo[A]): Callback =
  //     bs.state.flatMap {
  //       case State(Some(a)) => f(a).void
  //       case _              => Callback.empty
  //     }
  //
  //   def runOnAladin[A](f: JsAladin => A): Callback =
  //     bs.state.flatMap {
  //       case State(Some(a)) => CallbackTo(f(a)).void
  //       case _              => Callback.empty
  //     }
  //
  //   def render(props: Props): VdomElement = <.div(props.mountNodeClass)
  //
  //   def gotoRaDec(ra: Double, dec: Double): Callback = runOnAladin(_.gotoRaDec(ra, dec))
  //
  //   def box: Callback = runOnAladin(_.box())
  //
  //   def getFovForObject(
  //     objectName: String,
  //     cb:         Double => Callback = _ => Callback.empty
  //   ): Callback =
  //     runOnAladin(_.getFovForObject(objectName, (fov: Double) => cb(fov).runNow()))
  //
  //   def world2pixFn: CallbackTo[Coordinates => Option[(Double, Double)]] =
  //     runOnAladinOpt { j => (c: Coordinates) =>
  //       val ra  = c.ra.toAngle.toDoubleDegrees
  //       val dec = c.dec.toAngle.toSignedDoubleDegrees
  //       val p   = j.world2pix(ra, dec)
  //       Option(p).filter(_.length == 2).map(p => (p(0), p(1)))
  //     }.getOrElse((_: Coordinates) => None)
  //
  //   def pix2worldFn: CallbackTo[(Int, Int) => Option[Coordinates]] =
  //     runOnAladinOpt { j => (x: Int, y: Int) =>
  //       {
  //         val p = j.pix2world(x.toDouble, y.toDouble)
  //         Option(p).filter(_.length == 2).flatMap { p =>
  //           val ra  = RightAscension.fromDoubleDegrees(p(0))
  //           val dec = Declination.fromDoubleDegrees(p(1))
  //           dec.map(Coordinates(ra, _))
  //         }
  //       }
  //     }.getOrElse((_, _) => None)
  //
  //   def world2pix(c: Coordinates): CallbackTo[Option[(Double, Double)]] =
  //     runOnAladinOpt { j =>
  //       val ra  = c.ra.toAngle.toDoubleDegrees
  //       val dec = c.dec.toAngle.toSignedDoubleDegrees
  //       val p   = j.world2pix(ra, dec)
  //       Option(p).filter(_.length == 2).map(p => (p(0), p(1)))
  //     }.getOrElse(None)
  //
  //   def pix2world(x: Int, y: Int): CallbackTo[Option[Coordinates]] =
  //     runOnAladinOpt { j =>
  //       val p = j.pix2world(x.toDouble, y.toDouble)
  //       Option(p).filter(_.length == 2).flatMap { p =>
  //         val ra  = RightAscension.fromDoubleDegrees(p(0))
  //         val dec = Declination.fromDoubleDegrees(p(1))
  //         dec.map(Coordinates(ra, _))
  //       }
  //     }.getOrElse(None)
  //
  //   def getRaDec: CallbackTo[Coordinates] =
  //     runOnAladinOpt(_.getRaDec())
  //       .flatMapOption { a =>
  //         (RightAscension.fromHourAngle
  //            .get(Angle.hourAngle.get(Angle.fromDoubleDegrees(a(0))))
  //            .some,
  //          Declination.fromAngle.getOption(Angle.fromDoubleDegrees(a(1)))
  //         ).mapN(Coordinates.apply)
  //       }
  //       .getOrElse(Coordinates.Zero)
  //
  //   def gotoObject(q: String, cb: (Double, Double) => Callback, er: Callback): Callback =
  //     runOnAladin(_.gotoObject(q, new GoToObjectCallback(cb, er)))
  //
  //   def recalculateView: Callback =
  //     runOnAladin(_.recalculateView())
  //
  //   def fixLayoutDimensions: Callback =
  //     runOnAladin(_.fixLayoutDimensions())
  //
  //   def requestRedraw: Callback =
  //     runOnAladin(_.requestRedraw())
  //
  //   def increaseZoom: Callback =
  //     runOnAladin(_.increaseZoom())
  //
  //   def decreaseZoom: Callback =
  //     runOnAladin(_.decreaseZoom())
  //
  //   def setZoom(fovDegrees: Double): Callback =
  //     runOnAladin(_.setZoom(fovDegrees))
  //
  //   def toggleFullscreen: Callback =
  //     runOnAladin(_.toggleFullscreen())
  //
  //   def addCatalog(cat: AladinCatalog): Callback =
  //     runOnAladin(_.addCatalog(cat))
  //
  //   def pixelScale: CallbackTo[PixelScale] =
  //     runOnAladinOpt(a =>
  //       PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))
  //     ).getOrElse(PixelScale.Default)
  // }

  // Say this is the Scala component you want to share
  val component =
    ScalaFnComponent[Props]: props =>
      for {
        state <- useState(none[JsAladin])
        ref   <- useRefToVdom[html.Div]
        _     <- useEffectOnMount {
                   ref.get.flatMap {
                     case Some(e: Element) =>
                       CallbackTo {
                         A.aladin(e, fromProps(props))
                       }.flatTap { a =>
                         Callback(props.imageSurvey.toOption.map(a.setImageSurvey)) *>
                           Callback(props.baseImageLayer.toOption.map(a.setBaseImageLayer)) *>
                           Callback(props.customize.toOption.map(_(a).runNow()))
                       }.flatMap(a => state.setState(a.some))
                     case _                => Callback.empty
                   }
                 }
      } yield <.div(props.mountNodeClass, ^.untypedRef := ref)

  def fromProps(q: AladinProps): Props =
    Aladin(
      Css(q.mountNodeClass),
      q.target,
      q.fov.map(f => Angle.fromDoubleDegrees(f)),
      q.survey,
      q.cooFrame.flatMap(CooFrame.fromString(_).orUndefined),
      q.showReticle,
      q.showZoomControl,
      q.showFullscreenControl,
      q.showLayersControl,
      q.showGotoControl,
      q.showShareControl,
      q.showSimbadPointerControl,
      q.showFrame,
      q.showCoordinates,
      q.showFov,
      q.fullScreen,
      q.reticleColor,
      q.reticleSize,
      q.imageSurvey,
      q.baseImageLayer,
      q.customize.map(f => (j: JsAladin) => Callback(f(j)))
    )

  def fromProps(q: Props): AladinProps = {
    val p = new js.Object().asInstanceOf[AladinProps]
    q.fov.foreach(v => p.fov = v.toDoubleDegrees)
    q.target.foreach(v => p.target = v)
    q.survey.foreach(v => p.survey = v)
    // q.cooFrame.foreach(v => p.cooFrame = v.toJs)
    q.reticleColor.foreach(v => p.reticleColor = v: String)
    q.reticleSize.foreach(v => p.reticleSize = v)
    q.imageSurvey.foreach(v => p.imageSurvey = v)
    q.baseImageLayer.foreach(v => p.baseImageLayer = v)
    q.customize.foreach(v => p.customize = (j: JsAladin) => v(j).runNow())
    q.showReticle.foreach(v => p.showReticle = v)
    q.showZoomControl.foreach(v => p.showZoomControl = v)
    q.showFullscreenControl.foreach(v => p.showFullscreenControl = v)
    q.showLayersControl.foreach(v => p.showLayersControl = v)
    q.showGotoControl.foreach(v => p.showGotoControl = v)
    q.showShareControl.foreach(v => p.showShareControl = v)
    q.showSimbadPointerControl.foreach(v => p.showSimbadPointerControl = v)
    q.showFrame.foreach(v => p.showFrame = v)
    q.showCoordinates.foreach(v => p.showCoordinates = v)
    q.showFov.foreach(v => p.showFov = v)
    q.fullScreen.foreach(v => p.fullScreen = v)
    p
  }
