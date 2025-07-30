// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import cats.syntax.all.*
import crystal.react.View
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.Display
import lucuma.core.util.Enumerated
import lucuma.react.common.Css
import lucuma.react.common.ReactFnProps
import lucuma.ui.primereact.*
import lucuma.ui.primereact.given
import lucuma.ui.syntax.all.given

/**
 * Component to display side tabs based on an enumerated (Order is important) Each tab can be
 * selected and will be highlighted and it will push a page possibly via Routing
 *
 * The `separatorAfter` function is used to determine if a separator should be displayed after the
 * tab, allowing for the existence of groups
 */
case class SideTabs[A](
  id:             NonEmptyString,
  tab:            View[A],
  pageUrl:        A => String,
  separatorAfter: A => Boolean,
  filterPred:     A => Boolean = (_: A) => true
)(using val enumerated: Enumerated[A], val display: Display[A])
    extends ReactFnProps(SideTabs.component)

object SideTabs:
  private type AnyF[_] = Any

  private type Props[A] = SideTabs[A]

  private def buildComponent[V[_], A] =
    ScalaFnComponent[Props[A]]: p =>
      import p.given

      <.div(
        SideTabsStyles.SideTabs,
        <.div(
          SideTabsStyles.SideTabsVertical,
          SelectButtonEnumView(
            p.id,
            p.tab,
            buttonClass = SideTabsStyles.SideButton,
            filterPred = p.filterPred,
            itemTemplate = tab =>
              <.div(
                SideTabsStyles.RotationWrapperOuter |+|
                  SideTabsStyles.SideTabGroup.when_(p.separatorAfter(tab.value)),
                <.div(
                  SideTabsStyles.RotationWrapperInner,
                  <.a(
                    ^.onClick ==> ((e: ReactEvent) => e.preventDefaultCB),
                    ^.href := p.pageUrl(tab.value),
                    SideTabsStyles.VerticalButton,
                    p.display.shortName(tab.value)
                  )
                )
              )
          )
        ),
        <.div(
          SideTabsStyles.SideTabsHorizontalContainer,
          SelectButtonEnumView(
            p.id,
            p.tab,
            groupClass = SideTabsStyles.SideTabsHorizontal,
            buttonClass = SideTabsStyles.TabSelector,
            filterPred = p.filterPred,
            itemTemplate = tab =>
              <.a(
                ^.onClick ==> ((e: ReactEvent) => e.preventDefaultCB),
                ^.href := p.pageUrl(tab.value),
                p.display.shortName(tab.value)
              )
          )
        )
      )

  private val component = buildComponent[AnyF, Any]

object SideTabsStyles:
  val SideTabs: Css                    = Css("sidetabs")
  val SideTabsVertical: Css            = Css("sidetabs-body-vertical")
  val SideButton: Css                  = Css("side-button")
  val RotationWrapperOuter: Css        = Css("rotation-wrapper-outer")
  val RotationWrapperInner: Css        = Css("rotation-wrapper-inner")
  val SideTabGroup: Css                = Css("side-tabs-group")
  val SideTabsHorizontalContainer: Css = Css("sidetabs-body-horizontal-container")
  val VerticalButton: Css              = Css("vertical-button")
  val SideTabsHorizontal: Css          = Css("sidetabs-body-horizontal")
  val TabSelector: Css                 = Css("bottom-tab-selector")
