// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.aladin

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.ui.aladin.facade.*
import org.scalajs.dom.Element

import scala.scalajs.js
import org.scalajs.dom.html

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
    Callback.log(a) *>
      Callback(a.increaseZoom())

  def decreaseZoomCB: Callback =
    Callback(a.decreaseZoom())

  def fixLayoutDimensionsCB: Callback =
    Callback.log("FIX") *>
      Callback.log(a.view.fixLayoutDimensions())

  def gotoRaDecCB(c: Coordinates): Callback =
    Callback(a.gotoRaDec(c.ra.toAngle.toDoubleDegrees, c.dec.toAngle.toSignedDoubleDegrees))

extension (a: AladinOptions)
  def withCustomize(f: Aladin => Callback): AladinOptions =
    a.customize = (j: Aladin) => f(j).runNow()
    a

case class ReactAladin(
  mountNodeClass: Css,
  options:        AladinOptions = AladinOptions.Default,
  target:         js.UndefOr[String] = js.undefined,
  customize:      js.UndefOr[Aladin => Callback] = js.undefined
) extends ReactFnProps(ReactAladin.component):
  def render = ReactAladin.component(this)

object ReactAladin:

  type Props = ReactAladin

  case class State(a: Option[Aladin])

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
  // def requestRedraw: Callback =
  //   runOnAladin(_.requestRedraw())
  //
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
        state <- useState(none[Aladin])
        ref   <- useRefToVdom[html.Div]
        _     <- useEffectWithDeps(props.mountNodeClass) { _ =>
                   ref.get.flatMap {
                     case Some(e: Element) =>
                       CallbackTo {
                         A.aladin(e, props.options)
                       }.flatTap { a =>
                         Callback.log(s"Aladin created ${props.mountNodeClass}") *>
                           Callback(props.options.imageSurvey.toOption.map(a.setImageSurvey)) *>
                           Callback(props.options.baseImageLayer.toOption.map(a.setBaseImageLayer)) *>
                           Callback(props.customize.toOption.map(_(a).runNow()))
                       }.flatMap(a => state.setState(a.some))
                     case _                => Callback.empty
                   }
                 }
      } yield <.div(props.mountNodeClass, ^.untypedRef := ref)
