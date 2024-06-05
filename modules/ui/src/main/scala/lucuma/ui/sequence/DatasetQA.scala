// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sequence

import cats.syntax.all.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.enums.DatasetQaState
import lucuma.core.util.Enumerated
import lucuma.react.common.*
import lucuma.react.fa.FontAwesomeIcon
import lucuma.react.primereact.MenuItem
import lucuma.react.primereact.PopupMenu
import lucuma.react.primereact.Tooltip
import lucuma.react.primereact.hooks.all.*
import lucuma.react.primereact.tooltip.*
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles

case class DatasetQa(
  value:    Option[DatasetQaState],
  onChange: Option[Option[DatasetQaState] => Callback]
) extends ReactFnProps[DatasetQa](DatasetQa.component):
  lazy val items: List[MenuItem] =
    (none +: Enumerated[DatasetQaState].all.map(_.some)).map: qa =>
      MenuItem.Item(
        label = DatasetQa.renderQALabel(qa),
        icon = DatasetQa.renderQAIcon(qa),
        command = onChange.map(_(qa)).orEmpty
      )

  lazy val isCallbackDefined: Boolean = onChange.isDefined

object DatasetQa:
  private type Props = DatasetQa

  private def renderQAIcon(qaState: Option[DatasetQaState]): FontAwesomeIcon =
    LucumaIcons.Circle.withClass:
      qaState match
        case Some(DatasetQaState.Pass)   => LucumaStyles.IndicatorOK
        case Some(DatasetQaState.Usable) => LucumaStyles.IndicatorWarning
        case Some(DatasetQaState.Fail)   => LucumaStyles.IndicatorFail
        case None                        => LucumaStyles.IndicatorUnknown

  private def renderQALabel(qaState: Option[DatasetQaState]): String =
    qaState.fold("QA Not Set")(_.shortName)

  private val component =
    ScalaFnComponent
      .withHooks[Props]
      .usePopupMenuRef
      .render: (props, menuRef) =>
        <.span(
          SequenceStyles.VisitStepExtraDatasetQAStatus |+|
            SequenceStyles.VisitStepExtraDatasetQAStatusEditable.when_(props.isCallbackDefined),
          (^.onClick ==> menuRef.toggle).when(props.isCallbackDefined)
        )(
          renderQAIcon(props.value),
          React
            .Fragment(
              PopupMenu(model = props.items).withRef(menuRef.ref),
              <.span(SequenceStyles.VisitStepExtraDatasetQAStatusSelect)(LucumaIcons.ChevronDown)
            )
            .when(props.isCallbackDefined)
        ).withTooltip(content = renderQALabel(props.value), position = Tooltip.Position.Top)
