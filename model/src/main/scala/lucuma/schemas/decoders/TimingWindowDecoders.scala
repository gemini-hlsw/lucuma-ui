// Copyright (c) 2016-2025 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosInt
import io.circe.Decoder
import io.circe.refined.given
import lucuma.core.enums.TimingWindowInclusion
import lucuma.core.model.TimingWindow
import lucuma.core.model.TimingWindowEnd
import lucuma.core.model.TimingWindowRepeat
import lucuma.core.util.TimeSpan
import lucuma.core.util.Timestamp
import lucuma.odb.json.time.decoder.given

trait TimingWindowDecoders:
  given Decoder[TimingWindowRepeat] = Decoder.instance(c =>
    for
      period <- c.get[TimeSpan]("period")
      times  <- c.get[Option[PosInt]]("times")
    yield TimingWindowRepeat(period, times)
  )

  given Decoder[TimingWindowEnd.At] =
    Decoder.instance(c => c.get[Timestamp]("atUtc").map(TimingWindowEnd.At(_)))

  given Decoder[TimingWindowEnd.After] = Decoder.instance(c =>
    for
      duration <- c.get[TimeSpan]("after")
      repeat   <- c.get[Option[TimingWindowRepeat]]("repeat")
    yield TimingWindowEnd.After(duration, repeat)
  )

  given Decoder[TimingWindowEnd] =
    List[Decoder[TimingWindowEnd]](
      Decoder[TimingWindowEnd.At].widen,
      Decoder[TimingWindowEnd.After].widen
    ).reduceLeft(_ or _)

  given Decoder[TimingWindow] = Decoder.instance(c =>
    for
      inclusion <- c.get[TimingWindowInclusion]("inclusion")
      start     <- c.get[Timestamp]("startUtc")
      end       <- c.get[Option[TimingWindowEnd]]("end")
    yield TimingWindow(inclusion, start, end)
  )
