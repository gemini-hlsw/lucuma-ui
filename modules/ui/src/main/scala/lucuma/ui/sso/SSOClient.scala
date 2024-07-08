// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso

import cats.Applicative
import cats.effect.*
import cats.implicits.*
import eu.timepit.refined.*
import eu.timepit.refined.collection.NonEmpty
import io.circe.Decoder
import io.circe.parser.*
import lucuma.core.model.StandardRole
import lucuma.core.model.User
import lucuma.sso.client.codec.user.*
import lucuma.ui.utils.RetryHelpers.*
import org.http4s.*
import org.http4s.dom.FetchClientBuilder
import org.scalajs.dom.RequestCredentials
import org.scalajs.dom.window
import org.typelevel.log4cats.Logger
import retry.*

import java.time.Instant
import java.util as ju

case class JwtOrcidProfile(exp: Long, `lucuma-user`: User) derives Decoder

case class SSOClient[F[_]: Async: Logger](config: SSOConfig):
  private val client = FetchClientBuilder[F]
    .withRequestTimeout(config.readTimeout)
    .withCredentials(RequestCredentials.include)
    .resource

  // Does a client side redirect to the sso site
  val redirectToLogin: F[Unit] =
    Sync[F].delay {
      val returnUrl = window.location
      window.location.href =
        (config.uri / "auth" / "v1" / "stage1").withQueryParam("state", returnUrl.toString).toString
    }

  val guest: F[UserVault] =
    retryingOnAllErrors(retryPolicy[F], logError[F]("Switching to guest")) {
      client.use(
        _.expect[String](Request[F](Method.POST, config.uri / "api" / "v1" / "auth-as-guest"))
          .map(body =>
            (for {
              k <- Either.catchNonFatal(
                     ju.Base64.getDecoder.decode(body.split('.')(1).replace("-", "+"))
                   )
              j  = new String(k)
              p <- parse(j)
              u <- p.as[JwtOrcidProfile]
              t <- refineV[NonEmpty](body)
            } yield UserVault(u.`lucuma-user`, Instant.ofEpochSecond(u.exp), t))
              .getOrElse(throw new RuntimeException("Error decoding the token"))
          )
      )
    }

  val whoami: F[Option[UserVault]] =
    retryingOnAllErrors(retryPolicy[F], logError[F]("Calling whoami")) {
      client
        .flatMap(_.run(Request[F](Method.POST, config.uri / "api" / "v1" / "refresh-token")))
        .use {
          case Status.Successful(r) =>
            r.attemptAs[String]
              .leftMap(_.message)
              .value
              .map(
                _.flatMap(body =>
                  for {
                    k <- Either
                           .catchNonFatal(
                             ju.Base64.getDecoder.decode(body.split('.')(1).replace("-", "+"))
                           )
                           .leftMap(_.getMessage)
                    j  = new String(k)
                    p <- parse(j).leftMap(_.message)
                    u <- p.as[JwtOrcidProfile].leftMap(_.message)
                    t <- refineV[NonEmpty](body)
                  } yield UserVault(u.`lucuma-user`, Instant.ofEpochSecond(u.exp), t)
                ).fold(msg => throw new RuntimeException(s"Error decoding the token: $msg"), _.some)
              )
          case _                    =>
            Applicative[F].pure(none[UserVault])
        }
    }
      .adaptError { case t =>
        new Exception("Error connecting to authentication server.", t)
      }

  def switchRole(roleId: StandardRole.Id): F[Option[UserVault]] =
    retryingOnAllErrors(retryPolicy[F], logError[F]("Switching role")) {
      client
        .flatMap(
          _.run(
            Request(Method.GET,
                    (config.uri / "auth" / "v1" / "set-role").withQueryParam("role", roleId.show)
            )
          )
        )
        .use_ *> whoami

    }

  val logout: F[Unit] =
    retryingOnAllErrors(retryPolicy[F], logError[F]("Calling logout")) {
      client.flatMap(_.run(Request(Method.POST, config.uri / "api" / "v1" / "logout"))).use_
    }

  val switchToORCID: F[Unit] =
    logout.attempt >> redirectToLogin
