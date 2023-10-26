// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.optics

import cats.syntax.all.*
import monocle.Iso
import monocle.Lens

// Lenses must be disjoint (not overlap), or the result will be unsafe.
// See https://github.com/optics-dev/Monocle/issues/545
def disjointZip[S, A, B](l1: Lens[S, A], l2: Lens[S, B]): Lens[S, (A, B)] =
  Lens((s: S) => (l1.get(s), l2.get(s)))((ab: (A, B)) =>
    (s: S) => l2.replace(ab._2)(l1.replace(ab._1)(s))
  )

def disjointZip[S, A, B, C](l1: Lens[S, A], l2: Lens[S, B], l3: Lens[S, C]): Lens[S, (A, B, C)] =
  Lens((s: S) => (l1.get(s), l2.get(s), l3.get(s)))((abc: (A, B, C)) =>
    (s: S) => l3.replace(abc._3)(l2.replace(abc._2)(l1.replace(abc._1)(s)))
  )

def disjointZip[S, A, B, C, D](
  l1: Lens[S, A],
  l2: Lens[S, B],
  l3: Lens[S, C],
  l4: Lens[S, D]
): Lens[S, (A, B, C, D)] =
  Lens((s: S) => (l1.get(s), l2.get(s), l3.get(s), l4.get(s)))((abc: (A, B, C, D)) =>
    (s: S) => l4.replace(abc._4)(l3.replace(abc._3)(l2.replace(abc._2)(l1.replace(abc._1)(s))))
  )

def disjointZip[S, A, B, C, D, E](
  l1: Lens[S, A],
  l2: Lens[S, B],
  l3: Lens[S, C],
  l4: Lens[S, D],
  l5: Lens[S, E]
): Lens[S, (A, B, C, D, E)] =
  Lens((s: S) => (l1.get(s), l2.get(s), l3.get(s), l4.get(s), l5.get(s)))((abc: (A, B, C, D, E)) =>
    (s: S) =>
      l5.replace(abc._5)(
        l4.replace(abc._4)(l3.replace(abc._3)(l2.replace(abc._2)(l1.replace(abc._1)(s))))
      )
  )

// This only behaves as a lawful lens as long as A and B are both null or both set.
def unsafeDisjointOptionZip[S, A, B](
  l1: Lens[S, Option[A]],
  l2: Lens[S, Option[B]]
): Lens[S, Option[(A, B)]] =
  Lens((s: S) => (l1.get(s), l2.get(s)).tupled)((ab: Option[(A, B)]) =>
    (s: S) => l2.replace(ab.map(_._2))(l1.replace(ab.map(_._1))(s))
  )

def optionIso[A, B](iso: Iso[A, B]): Iso[Option[A], Option[B]] =
  Iso[Option[A], Option[B]](_.map(iso.get))(_.map(iso.reverseGet))
