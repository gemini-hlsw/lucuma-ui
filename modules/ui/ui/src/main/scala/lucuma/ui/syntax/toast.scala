// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.Monoid
import cats.effect.Resource
import cats.effect.Resource.ExitCase
import cats.effect.Sync
import cats.effect.std.UUIDGen
import cats.syntax.all.*
import crystal.react.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import lucuma.react.common.Css
import lucuma.react.fa.IconSize
import lucuma.react.primereact.Message
import lucuma.react.primereact.MessageItem
import lucuma.react.primereact.ToastRef
import lucuma.ui.LucumaIcons
import lucuma.ui.LucumaStyles

trait toast:
  extension (toastRef: ToastRef)
    def show(
      text:     String,
      severity: Message.Severity = Message.Severity.Info,
      sticky:   Boolean = false
    ): Callback =
      toastRef.show(
        MessageItem(
          content = <.span(LucumaIcons.CircleInfo.withSize(IconSize.LG), text),
          severity = severity,
          sticky = sticky,
          clazz = LucumaStyles.Toast
        )
      )

    def showDuring[F[_]: Sync: UUIDGen](
      text:         String,
      completeText: Option[String] = none,
      errorText:    Option[String] = none
    )(using
      Monoid[F[Unit]]
    ): Resource[F, Unit] =
      for
        item  <- Resource.eval:
                   UUIDGen[F].randomUUID.map: id =>
                     MessageItem(
                       id = id.toString,
                       content = <.span(
                         LucumaIcons.CircleNotch.withSize(IconSize.LG).withFixedWidth(true),
                         text
                       ),
                       severity = Message.Severity.Info,
                       clazz = LucumaStyles.Toast,
                       sticky = true,
                       closable = false
                     )
        toast <- Resource.makeCase(
                   toastRef.show(item).to[F]
                 ): (_, exitCase) =>
                   toastRef.remove(item).to[F] >> (
                     exitCase match
                       case ExitCase.Succeeded  =>
                         completeText.map(show(_).to[F]).orEmpty
                       case ExitCase.Errored(e) =>
                         show(
                           text = errorText.getOrElse(s"Error during operation: ${e.getMessage}"),
                           severity = Message.Severity.Error,
                           sticky = true
                         ).to[F]
                       case ExitCase.Canceled   =>
                         show(
                           text = errorText.getOrElse(s"Operation canceled"),
                           severity = Message.Severity.Error,
                           sticky = true
                         ).to[F]
                   )
      yield toast

object toast extends toast
