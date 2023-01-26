// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.Order._
import cats.syntax.all._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.refined._
import lucuma.core.enums.TacCategory
import lucuma.core.enums.ToOActivation
import lucuma.core.model.IntPercent
import lucuma.core.model.NonNegDuration
import lucuma.core.model.Partner
import lucuma.core.model.Proposal
import lucuma.core.model.ProposalClass
import lucuma.core.model.ProposalClass._
import lucuma.core.util.TimeSpan

import scala.collection.immutable.SortedMap

trait ProposalDecoders {
  private def validateTypename(expected: String)(value: String): Decoder.Result[Unit] =
    if (value === expected) Right(())
    else Left(DecodingFailure(s"Unexpected __typename `$value`", List()))

  implicit val PartnerSplitDecoder: Decoder[(Partner, IntPercent)] = Decoder.instance(c =>
    for {
      partner <- c.downField("partner").as[Partner]
      percent <- c.downField("percent").as[IntPercent]
    } yield (partner, percent)
  )

  implicit val classicalDecoder: Decoder[Classical] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("Classical"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield Classical(minPct)
  )

  implicit val demoScienceDecoder: Decoder[DemoScience] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("DemoScience"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield DemoScience(minPct)
  )

  implicit val directorsTimeDecoder: Decoder[DirectorsTime] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("DirectorsTime"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield DirectorsTime(minPct)
  )

  implicit val exchangeDecoder: Decoder[Exchange] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("Exchange"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield Exchange(minPct)
  )

  implicit val fastTurnaroundDecoder: Decoder[FastTurnaround] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("FastTurnaround"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield FastTurnaround(minPct)
  )

  implicit val poorWeatherDecoder: Decoder[PoorWeather] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("PoorWeather"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield PoorWeather(minPct)
  )

  implicit val queueDecoder: Decoder[Queue] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("Queue"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield Queue(minPct)
  )

  implicit val systemVerificationDecoder: Decoder[SystemVerification] = Decoder.instance(c =>
    for {
      _      <- c.downField("__typename").as[String].flatMap(validateTypename("SystemVerification"))
      minPct <- c.downField("minPercentTime").as[IntPercent]
    } yield SystemVerification(minPct)
  )

  implicit val largeProgramDecoder: Decoder[LargeProgram] = Decoder.instance(c =>
    for {
      _         <- c.downField("__typename").as[String].flatMap(validateTypename("LargeProgram"))
      minPct    <- c.downField("minPercentTime").as[IntPercent]
      minPctTot <- c.downField("minPercentTotalTime").as[IntPercent]
      totTime   <- c.downField("totalTime").as[TimeSpan]
    } yield LargeProgram(minPct, minPctTot, totTime)
  )

  implicit val intensiveDecoder: Decoder[Intensive] = Decoder.instance(c =>
    for {
      _         <- c.downField("__typename").as[String].flatMap(validateTypename("Intensive"))
      minPct    <- c.downField("minPercentTime").as[IntPercent]
      minPctTot <- c.downField("minPercentTotalTime").as[IntPercent]
      totTime   <- c.downField("totalTime").as[TimeSpan]
    } yield Intensive(minPct, minPctTot, totTime)
  )

  implicit val proposalClassDecoder: Decoder[ProposalClass] =
    List[Decoder[ProposalClass]](
      Decoder[Classical].widen,
      Decoder[DemoScience].widen,
      Decoder[DirectorsTime].widen,
      Decoder[Exchange].widen,
      Decoder[FastTurnaround].widen,
      Decoder[PoorWeather].widen,
      Decoder[Queue].widen,
      Decoder[SystemVerification].widen,
      Decoder[LargeProgram].widen,
      Decoder[Intensive].widen
    ).reduceLeft(_ or _)

  implicit val proposalDecoder: Decoder[Proposal] = Decoder.instance(c =>
    for {
      title <- c.downField("title").as[Option[NonEmptyString]]
      pc    <- c.downField("proposalClass").as[ProposalClass]
      cat   <- c.downField("category").as[Option[TacCategory]]
      too   <- c.downField("toOActivation").as[ToOActivation]
      abs   <- c.downField("abstract").as[Option[NonEmptyString]]
      ps    <- c.downField("partnerSplits").as[List[(Partner, IntPercent)]]
    } yield Proposal(title, pc, cat, too, abs, SortedMap.from(ps))
  )

}
