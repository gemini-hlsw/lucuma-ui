// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all.*
import eu.timepit.refined.types.numeric.NonNegInt
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.optics.SplitEpi
import lucuma.core.syntax.time.*
import lucuma.core.util.TimeSpan
import monocle.Iso
import monocle.Lens

import scala.collection.immutable.SortedSet

// This only behaves as a lawful lens as long as A and B are both null or both set.
def unsafeDisjointOptionZip[S, A, B](
  l1: Lens[S, Option[A]],
  l2: Lens[S, Option[B]]
): Lens[S, Option[(A, B)]] =
  Lens((s: S) => (l1.get(s), l2.get(s)).tupled)((ab: Option[(A, B)]) =>
    (s: S) => l2.replace(ab.map(_._2))(l1.replace(ab.map(_._1))(s))
  )

extension [A, B](iso: Iso[A, B])
  def option: Iso[Option[A], Option[B]] =
    Iso[Option[A], Option[B]](_.map(iso.get))(_.map(iso.reverseGet))

val OptionNonEmptyStringIso: Iso[Option[NonEmptyString], String] =
  Iso[Option[NonEmptyString], String](_.foldMap(_.value))(s => NonEmptyString.from(s).toOption)

// Note: truncates to Int.MaxValue - shouldn't have durations longer than that...
val TimeSpanSecondsSplitEpi: SplitEpi[TimeSpan, NonNegInt] = SplitEpi(
  ts => NonNegInt.unsafeFrom(math.min(ts.toSeconds.longValue, Int.MaxValue.toLong).toInt),
  secs => TimeSpan.unsafeFromDuration(secs.value.toLong.seconds)
)

def SortedSetFromList[A: Ordering]: SplitEpi[List[A], SortedSet[A]] =
  SplitEpi[List[A], SortedSet[A]](SortedSet.from(_), _.toList)
