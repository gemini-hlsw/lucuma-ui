// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gpp.ui.forms

import cats.implicits._
import gsp.math.optics.Format
import monocle.Iso
import monocle.Prism

/**
  * Define an object that can convert from A to String and optionally viceversa
  * It is meant to be used for Input widgets targeting some A
  * Can be easily constructed from Monocle Optics
  */
trait InputOptics[A] {

  /** get the source as a string */
  def reverseGet(a: A): String

  /** convert a string to a possible output */
  def getOption(s: String): Option[A]
}

object InputOptics {
  val id: InputOptics[String] = fromIso(Iso.id[String])

  def apply[A](_getOption:  String => Option[A])(_reverseGet: A => String): InputOptics[A] =
    new InputOptics[A] {
      def reverseGet(a: A): String         = _reverseGet(a)
      def getOption(s:  String): Option[A] = _getOption(s)
    }

  /**
    * Build optics from a Prism
    */
  def fromPrism[A](prism:   Prism[String, A]) =
    new InputOptics[A] {
      def reverseGet(a: A): String         = prism.reverseGet(a)
      def getOption(s:  String): Option[A] = prism.getOption(s)
    }

  /**
    * Build optics from a Iso
    */
  def fromIso[A](iso:       Iso[String, A]): InputOptics[A] =
    new InputOptics[A] {
      def reverseGet(a: A): String         = iso.reverseGet(a)
      def getOption(s:  String): Option[A] = iso.get(s).some
    }

  /**
    * Build optics from a Format
    */
  def fromFormat[A](format: Format[String, A])              =
    new InputOptics[A] {
      def reverseGet(a: A): String         = format.reverseGet(a)
      def getOption(s:  String): Option[A] = format.getOption(s)
    }

}
