// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.data.NonEmptyChain
import cats.data.NonEmptyList
import cats.data.Validated
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import lucuma.core.optics.Format
import lucuma.refined._

package object optics {
  type InputFormat[A]          = Format[String, A]
  type ValidFormatNec[E, T, A] = ValidFormat[NonEmptyChain[E], T, A]
  type ValidFormatNel[E, T, A] = ValidFormat[NonEmptyList[E], T, A]
  type ValidFormatInput[A]     = ValidFormatNec[NonEmptyString, String, A]

  sealed trait SeqGuard[L[_]]
  implicit object ListSeqGuard         extends SeqGuard[List]
  implicit object NonEmptyListSeqGuard extends SeqGuard[NonEmptyList]

  /**
   * Build `ValidFormatInput` from another one, but allow empty values to become `None`
   */
  implicit class ValidFormatInputOps[A](val self: ValidFormatInput[A]) extends AnyVal {
    def optional: ValidFormatInput[Option[A]] =
      ValidFormatInput(
        (a: String) =>
          if (a.isEmpty) Validated.validNec(None)
          else
            self.getValidated(a).map(_.some),
        (a: Option[A]) => a.foldMap(self.reverseGet)
      )

    def toNel(
      separator: NonEmptyString = ",".refined,
      error:     Option[NonEmptyString] = none // If not set, will show the list of individual errors
    ): ValidFormatInput[NonEmptyList[A]] =
      ValidFormatInput[NonEmptyList[A]](
        _.split(separator).toList.toNel
          .toRight[NonEmptyChain[NonEmptyString]](NonEmptyChain("Cannot be empty".refined))
          .flatMap(
            _.traverse(self.getValidated.andThen(_.toEither))
              .leftMap(errorNec => error.fold(errorNec)(e => NonEmptyChain(e)))
          )
          .toValidated,
        _.map(self.reverseGet).toList.mkString(separator)
      )
  }
}
