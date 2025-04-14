// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyMap
import cats.implicits.*
import crystal.react.ReuseView
import crystal.react.reuse.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.hooks.Hooks.UseState
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.GmosSouthFilter
import lucuma.core.enums.GmosSouthFpu
import lucuma.core.enums.GmosSouthGrating
import lucuma.core.enums.PortDisposition
import lucuma.core.math.*
import lucuma.react.common.*
import lucuma.react.resizeDetector.hooks.*
import lucuma.schemas.model.BasicConfiguration
import lucuma.schemas.model.CentralWavelength
import lucuma.ui.aladin.*
import lucuma.ui.primereact.EnumDropdown
import lucuma.ui.visualization.*
import lucuma.ui.display.given
import monocle.macros.GenLens
import lucuma.core.util.Enumerated
import lucuma.core.enums.GmosSouthFpu

final case class AladinContainer(
  fov:         ReuseView[Fov],
  coordinates: Coordinates
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

object AladinContainer {
  type Props = AladinContainer

  val coordinates = GenLens[AladinContainer](_.coordinates)

  implicit val propsReuse: Reusability[Props] =
    Reusability.by_==

  implicit val reuseDouble: Reusability[Double] = Reusability.double(0.00001)

  val allFpu = Enumerated[GmosSouthFpu]
  // println(allFpu)

  val component = ScalaFnComponent[Props]: props =>
    for {
      // View coordinates (in case the user pans)
      currentPos         <- useState(props.coordinates)
      // Ref to the aladin component
      aladinRef          <- useState(none[Aladin])
      // resize detector
      resize             <- useResizeDetector
      flip               <- useState(true)
      // Toggle state for SVGVisualizationOverlay CSS
      fpuVisible         <- useState(true)
      ccdVisible         <- useState(true)
      candidatesVisible  <- useState(true)
      patrolFieldVisible <- useState(true)
      conf               <- useState(
                              BasicConfiguration.GmosSouthLongSlit(
                                grating = GmosSouthGrating.R400_G5325,
                                filter = GmosSouthFilter.HeII.some,
                                fpu = GmosSouthFpu.LongSlit_2_00,
                                centralWavelength = CentralWavelength(Wavelength.fromIntNanometers(500).get)
                              )
                            )

    } yield
      /**
       * Called when the position changes, i.e. aladin pans. We want to offset the visualization to
       * keep the internal target correct
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
        props.coordinates // .offsetBy(Angle.Angle0, GmosGeometry.guideStarOffset)

      val shapes = GmosGeometry.gmosGeometry(
        currentPos.value,
        None,
        None,
        Angle.Angle0.some,
        conf.value.some,
        PortDisposition.Side,
        None,
        VisualizationStyles.GuideStarCandidateVisible
      )

      def toggler(
        id:    String,
        item:  String,
        state: UseState[Boolean],
        clazz: Css = Css("toggle-container")
      ) =
        <.div(
          clazz,
          <.input(
            ^.`type`  := "checkbox",
            ^.id      := s"overlay-toggle-$id",
            ^.checked := state.value,
            ^.onChange --> state.setState(!state.value)
          ),
          <.label(
            ^.htmlFor := s"overlay-toggle-$id",
            s"Show/Hide $item"
          )
        )

      <.div(
        Css("react-aladin-container"),
        // This happens during a second render. If we let the height to be zero, aladin
        // will take it as 1. This height ends up being a denominator, which, if low,
        // will make aladin request a large amount of tiles and end up freezing the demo.
        if (resize.height.exists(_ >= 100))
          ReactFragment(
            (resize.width, resize.height, shapes.flatMap(NonEmptyMap.fromMap))
              .mapN((w, h, s) =>
                SVGVisualizationOverlay(
                  w,
                  h,
                  props.fov.get,
                  currentPos.value.diff(props.coordinates).offset,
                  s,
                  clazz = VisualizationStyles.GmosFpuVisible.when_(fpuVisible.value) |+|
                    VisualizationStyles.GmosCcdVisible.when_(ccdVisible.value) |+|
                    VisualizationStyles.GmosCandidatesAreaVisible.when_(candidatesVisible.value) |+|
                    VisualizationStyles.GmosPatrolFieldVisible.when_(patrolFieldVisible.value)
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
                    gs.some.map(SVGTarget.CircleTarget(_, Css("guidestar"), 3))
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
            toggler("fpu", "FPU", fpuVisible),
            toggler("ccd", "Science CCD", ccdVisible),
            toggler("candidates", "Candidates Field", candidatesVisible),
            toggler("patrol-field", "Patrol Field", patrolFieldVisible),
            <.div(
              Css("config-controls"),
              <.label(^.htmlFor := "fpu-selector", "Select FPU:"),
              <.select(
                ^.id    := "fpu-selector",
                ^.value := conf.value.fpu.tag,
                ^.onChange ==> ((r: ReactUIEventFromInput) =>
                  Enumerated[GmosSouthFpu]
                    .fromTag(r.target.value)
                    .map(fpu =>
                      conf.setState(
                        conf.value.copy(fpu = fpu)
                      )
                    )
                    .getOrElse(Callback.empty)
                )
              )(
                Enumerated[GmosSouthFpu].all
                  .filter(_.tag.startsWith("LongSlit"))
                  .map(fpu =>
                    <.option(
                      ^.key   := fpu.tag,
                      ^.value := fpu.tag,
                      fpu.longName
                    )
                  )
                  .toTagMod
              )
            )
          )
        else EmptyVdom
      )
        .withRef(resize.ref)
}
