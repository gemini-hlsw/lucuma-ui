// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.sso

import cats.Eq
import cats.derived.*
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.model.User
import monocle.Focus
import org.typelevel.cats.time.instances.instant.*

import java.time.Instant

case class UserVault(user: User, expiration: Instant, token: NonEmptyString) derives Eq:
  lazy val authorizationHeader: String = s"Bearer ${token.value}"

object UserVault:
  val user  = Focus[UserVault](_.user)
  val token = Focus[UserVault](_.token)
