// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso

import cats.Eq
import cats.derived.*
import cats.syntax.all.*
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.model.Access
import lucuma.core.model.StandardRole
import lucuma.core.model.StandardUser
import lucuma.core.model.User
import monocle.Focus
import org.http4s.AuthScheme
import org.http4s.Credentials
import org.http4s.headers.Authorization
import org.typelevel.cats.time.instances.instant.*

import java.time.Instant

case class UserVault(user: User, expiration: Instant, token: NonEmptyString) derives Eq:
  lazy val authorizationHeader: Authorization =
    Authorization:
      Credentials.Token(AuthScheme.Bearer, token.value)

  def extractRoles: (Option[StandardRole], List[StandardRole]) =
    user match
      case StandardUser(_, role, other, _) => (role.some, other)
      case _                               => (none, Nil)

  def roleNames: List[String] =
    user match
      case StandardUser(_, role, other, _) =>
        (role :: other).map(_.access.tag)
      case _                               => Nil

  def isStaff: Boolean        = user.role.access === Access.Staff
  def isAdmin: Boolean        = user.role.access === Access.Admin
  def isStaffOrAdmin: Boolean = user.role.access >= Access.Staff

object UserVault:
  val user  = Focus[UserVault](_.user)
  val token = Focus[UserVault](_.token)
