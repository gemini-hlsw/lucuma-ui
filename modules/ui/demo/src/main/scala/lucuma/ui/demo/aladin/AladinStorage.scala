// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react.*
import lucuma.core.math.*
import lucuma.core.syntax.string.*
import lucuma.ui.aladin.Fov
import org.scalajs.dom

trait AladinStorage:
  private val OffsetKey = "aladin-view-offset"
  private val FovKey    = "aladin-view-fov"

  val DefaultFov = Fov(Angle.fromDMS(0, 15, 0, 0, 0), Angle.fromDMS(0, 15, 0, 0, 0))

  def saveOffset(offset: Offset): Callback =
    Callback {
      dom.window.localStorage.setItem(
        OffsetKey,
        s"${Offset.P.signedDecimalArcseconds.get(offset.p)},${Offset.Q.signedDecimalArcseconds.get(offset.q)}"
      )
    }

  def loadOffset: Offset =
    Option(dom.window.localStorage.getItem(OffsetKey))
      .flatMap { str =>
        str.split(",").toList match {
          case List(pStr, qStr) =>
            for {
              p <- pStr.toDoubleOption
              q <- qStr.toDoubleOption
            } yield Offset(
              Offset.P(Angle.fromDoubleArcseconds(p)),
              Offset.Q(Angle.fromDoubleArcseconds(q))
            )
          case _                => None
        }
      }
      .getOrElse(Offset.Zero)

  def saveFov(fov: Fov): Callback =
    Callback {
      dom.window.localStorage.setItem(
        FovKey,
        s"${fov.x.toMicroarcseconds},${fov.y.toMicroarcseconds}"
      )
    }

  def loadFov: Fov =
    Option(dom.window.localStorage.getItem(FovKey))
      .flatMap { str =>
        str.split(",").toList match {
          case List(xStr, yStr) =>
            for {
              x <- xStr.parseLongOption
              y <- yStr.parseLongOption
            } yield Fov(Angle.fromMicroarcseconds(x), Angle.fromMicroarcseconds(y))
          case _                => None
        }
      }
      .getOrElse(DefaultFov)

  def resetFov = saveFov(DefaultFov)

object AladinStorage extends AladinStorage
