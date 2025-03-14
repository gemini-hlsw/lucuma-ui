// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.implicits.*
import crystal.react.ReuseView
import crystal.react.reuse.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.math.*
import lucuma.react.aladin.*
import lucuma.react.common.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.ui.aladin.*
import monocle.macros.GenLens

final case class AladinContainer(
  fov:         ReuseView[Fov],
  coordinates: Coordinates
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

object AladinContainer {
  type Props = AladinContainer

  val AladinComp = ReactAladin.component

  val coordinates = GenLens[AladinContainer](_.coordinates)

  implicit val propsReuse: Reusability[Props] =
    Reusability.by_==

  implicit val reuseDouble: Reusability[Double] = Reusability.double(0.00001)

  val component =
    ScalaFnComponent
      .withHooks[Props]
      // View coordinates (in case the user pans)
      .useStateBy(_.coordinates)
      // Ref to the aladin component
      .useState(none[Aladin])
      // resize detector
      .useResizeDetector()
      .useState(true)
      .render { (props, currentPos, aladinRef, resize, flip) =>
        /**
         * Called when the position changes, i.e. aladin pans. We want to offset the visualization
         * to keep the internal target correct
         */
        def onPositionChanged(u: PositionChanged): Callback =
          currentPos.setState(Coordinates(u.ra, u.dec))

        def onZoom = (v: Fov) => props.fov.set(v)

        def customizeAladin(v: Aladin): Callback =
          aladinRef.setState(Some(v)) *>
            v.fixLayoutDimensionsCB *>
            v.onZoomCB(onZoom) *> // re render on zoom
            v.onPositionChangedCB(onPositionChanged)

        val gs =
          props.coordinates.offsetBy(Angle.Angle0, GmosGeometry.guideStarOffset)

        <.div(
          Css("react-aladin-container"),
          // This happens during a second render. If we let the height to be zero, aladin
          // will take it as 1. This height ends up being a denominator, which, if low,
          // will make aladin request a large amount of tiles and end up freeze explore.
          if (resize.height.exists(_ >= 100))
            ReactFragment(
              (resize.width, resize.height)
                .mapN(
                  VisualizationOverlay(
                    _,
                    _,
                    props.fov.get,
                    currentPos.value.diff(props.coordinates).offset,
                    // screenOffset,
                    GmosGeometry.shapes
                  )
                ),
              (resize.width, resize.height)
                .mapN(
                  TargetsOverlay(
                    _,
                    _,
                    props.fov.get,
                    currentPos.value.diff(props.coordinates).offset,
                    props.coordinates,
                    List(
                      SVGTarget.CrosshairTarget(props.coordinates, Css("science-target"), 10).some,
                      gs.map(SVGTarget.CircleTarget(_, Css("guidestar"), 3))
                    ).flatten
                  )
                ),
              ReactAladin(
                Css("react-aladin") |+| Css("test").when_(flip.value),
                AladinOptions(
                  showReticle = true,
                  showLayersControl = true,
                  target = props.aladinCoordsStr,
                  fov = props.fov.get.x,
                  showGotoControl = false,
                  showCooLocation = true,
                  showFullscreenControl = false
                ),
                customize = customizeAladin(_)
              ),
              <.button(
                ^.onClick --> flip.setState(!flip.value),
                "Flip"
              )
            )
          else EmptyVdom
        )
          .withRef(resize.ref)
      }

}
