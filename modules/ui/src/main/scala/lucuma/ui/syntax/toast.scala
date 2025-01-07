// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.syntax

import cats.Monoid
import cats.effect.Sync
import cats.effect.std.UUIDGen
import cats.effect.syntax.all.*
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

    def showDuring[F[_]: Sync: UUIDGen](fa: F[Unit])(
      text:         String,
      completeText: Option[String] = none,
      errorText:    Option[String] = none
    )(using Monoid[F[Unit]]): F[Unit] =
      for
        id  <- UUIDGen[F].randomUUID
        item = MessageItem(
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
        _   <- toastRef.show(item).to[F]
        _   <- fa.guarantee(toastRef.remove(item).to[F])
                 .flatMap(_ => completeText.map(show(_).to[F]).orEmpty)
                 .onError: e =>
                   show(
                     text = errorText.getOrElse(s"Error during operation: ${e.getMessage}"),
                     severity = Message.Severity.Error,
                     sticky = true
                   ).to[F]
      yield ()

object toast extends toast
