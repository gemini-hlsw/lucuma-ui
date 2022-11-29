// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*
import coulomb.Quantity
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.ReuseViewOptF
import crystal.react.reuse.*
import lucuma.core.optics.*
import lucuma.core.util.Enumerated
import monocle.function.At.at
import monocle.function.Index.index

import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import scala.annotation.targetName

def abbreviate(s: String, maxLength: Int): String =
  if (s.length > maxLength) s"${s.substring(0, maxLength)}\u2026" else s

implicit class ListOps[A](val list: List[A]) extends AnyVal {
  def modFirstWhere(find: A => Boolean, mod: A => A): List[A] =
    list.indexWhere(find) match
      case -1 => list
      case n  => (list.take(n) :+ mod(list(n))) ++ list.drop(n + 1)

  def removeFirstWhere(find: A => Boolean): List[A] =
    list.indexWhere(find) match
      case -1 => list
      case n  => list.take(n) ++ list.drop(n + 1)
}

val versionDateFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.from(ZoneOffset.UTC))

val versionDateTimeFormatter: DateTimeFormatter =
  DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.from(ZoneOffset.UTC))

extension [A, B, C, D](list: List[(A, B, C, D)]) // TODO Move to utils
  def unzip4: (List[A], List[B], List[C], List[D]) =
    list.foldRight((List.empty[A], List.empty[B], List.empty[C], List.empty[D]))((tuple, accum) =>
      (tuple._1 :: accum._1, tuple._2 :: accum._2, tuple._3 :: accum._3, tuple._4 :: accum._4)
    )

extension [F[_], A](listView: ViewF[F, List[A]])
  @targetName("ListView_toListOfViews")
  def toListOfViews: List[ViewF[F, A]] =
    // It's safe to "get" since we are only invoking for existing indices.
    listView.get.indices.toList.map { i =>
      val atIndex = index[List[A], Int, A](i)
      listView
        .zoom((atIndex.getOption _).andThen(_.get))(atIndex.modify)
    }

extension [F[_], K, V](mapView: ViewF[F, Map[K, V]])
  @targetName("MapView_toListOfViews")
  def toListOfViews: List[(K, ViewF[F, V])] =
    // It's safe to "get" since we are only invoking for existing keys.
    mapView.get.keys.toList.map(k =>
      k -> mapView.zoom(at[Map[K, V], K, Option[V]](k)).zoom(_.get)(f => _.map(f))
    )

given [A: Enumerated, B: Enumerated]: Enumerated[(A, B)] =
  Enumerated
    .fromNEL(NonEmptyList.fromListUnsafe((Enumerated[A].all, Enumerated[B].all).tupled))
    .withTag { case (a, b) => s"${Enumerated[A].tag(a)}, ${Enumerated[B].tag(b)} " }

// Coulomb implicits
extension [F[_], N, U](self:    ViewF[F, Quantity[N, U]])
  def stripQuantity: ViewF[F, N] = self.as(quantityIso[N, U])

extension [F[_], N, U](self: ViewOptF[F, Quantity[N, U]])
  def stripQuantity: ViewOptF[F, N] = self.as(quantityIso[N, U])

import crystal.implicits.*

extension [F[_], N, U](self:    ReuseViewOptF[F, Quantity[N, U]])
  def stripQuantity(implicit F: Monad[F]): ReuseViewOptF[F, N] = self.as(quantityIso[N, U])
