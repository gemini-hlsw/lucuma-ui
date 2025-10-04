// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.components

import cats.syntax.all.*
import crystal.react.*
import crystal.react.hooks.*
import eu.timepit.refined.types.string.NonEmptyString
import japgolly.scalajs.react.*
import japgolly.scalajs.react.util.DefaultEffects.Async as DefaultA
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.core.util.NewType
import lucuma.react.common.*
import lucuma.react.primereact.Button
import lucuma.react.primereact.Dialog
import lucuma.react.primereact.Image
import lucuma.react.primereact.Message
import lucuma.ui.Resources
import lucuma.ui.primereact.*
import lucuma.ui.sso.SSOClient
import lucuma.ui.sso.UserVault
import lucuma.ui.syntax.all.*
import lucuma.ui.utils.UAParser
import org.scalajs.dom
import org.typelevel.log4cats.Logger

case class UserSelectionForm(
  systemName:      NonEmptyString,
  systemNameStyle: Css,
  ssoClient:       SSOClient[DefaultA],
  vault:           View[Option[UserVault]],
  message:         View[Option[NonEmptyString]],
  allowGuest:      Boolean
)(using val logger: Logger[DefaultA])
    extends ReactFnProps(UserSelectionForm.component)

object UserSelectionForm:
  private type Props = UserSelectionForm

  private object IsOpen extends NewType[Boolean]

  private case class BrowserInfo(supportedOrcidBrowser: Boolean, warnBrowser: Boolean):
    inline def showButtons: Boolean = supportedOrcidBrowser

  private object BrowserInfo:
    def supportedOrcidBrowser(using logger: Logger[DefaultA]): DefaultA[BrowserInfo] =
      DefaultA.handleError(
        DefaultA.delay:
          val browser  = new UAParser(dom.window.navigator.userAgent).getBrowser()
          val verRegex = raw"(\d{0,3}).(\d{0,3})\.?(.*)?".r

          (browser.name, browser.version) match {
            case ("Safari", verRegex(major, _, _)) if major.toInt <= 13 =>
              BrowserInfo(false, false)
            case ("Safari", _)                                          => BrowserInfo(true, true)
            case _                                                      => BrowserInfo(true, false)
          }
      )(e =>
        logger.error(e)("Error checking browser compatibility") *> DefaultA.delay(
          BrowserInfo(true, false)
        )
      )

  private val component =
    ScalaFnComponent[Props]: props =>
      for {
        isOpen         <- useState(IsOpen(true))
        browserInfoPot <-
          useEffectResultOnMount(BrowserInfo.supportedOrcidBrowser(using props.logger))
      } yield
        import props.given

        val guest: Callback =
          props.ssoClient.guest.flatMap(v => props.vault.set(v.some).toAsync).runAsync

        val login: Callback = props.ssoClient.redirectToLogin.runAsync

        Dialog(
          closable = false,
          visible = isOpen.value.value,
          onHide = isOpen.setState(IsOpen(false)),
          resizable = false,
          draggable = false,
          showHeader = false,
          clazz = LucumaPrimeStyles.Dialog.Small
        )(
          browserInfoPot.value.renderPot(browserInfo =>
            React.Fragment(
              <.div(
                LoginStyles.LoginBoxLayout,
                Logo(props.systemName, props.systemNameStyle),
                <.div(
                  LoginStyles.UserSelectionButtons,
                  Button(
                    label = "Login with ORCID",
                    icon = Image(src = Resources.OrcidLogo, clazz = LoginStyles.LoginOrcidIcon),
                    clazz = LoginStyles.LoginBoxButton,
                    severity = Button.Severity.Secondary,
                    onClick = login >> props.message.set(none) >> isOpen.setState(IsOpen(false))
                  ).big.when(browserInfo.showButtons),
                  Button(
                    label = "Continue as Guest",
                    icon = LoginIcons.UserAstronaut.withClass(LoginStyles.LoginOrcidIcon),
                    clazz = LoginStyles.LoginBoxButton,
                    severity = Button.Severity.Secondary,
                    onClick = guest >> props.message.set(none) >> isOpen.setState(IsOpen(false))
                  ).big.when(browserInfo.showButtons && props.allowGuest)
                )
              ),
              <.div(LoginStyles.LoginMessagesLayout)(
                props.message.get
                  .whenDefined(using
                    message => Message(text = message.value, severity = Message.Severity.Error)
                  ),
                Message(
                  text =
                    "This version of Safari isn't supported. Try a newer version (≥14.0.1) or a recent version of Chrome or Firefox.",
                  severity = Message.Severity.Error,
                  icon = LoginIcons.SkullCrossBones
                ).unless(browserInfo.supportedOrcidBrowser),
                Message(
                  text =
                    "ORCID authentication does not work with some configurations of Safari and MacOS. If it doesn't work for you please try Chrome or Firefox.",
                  severity = Message.Severity.Warning,
                  icon = LoginIcons.ExclamationTriangle
                ).when(browserInfo.warnBrowser)
              )
            )
          )
        )
