// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.effect._
import fs2.io.file._
import io.circe.Decoder
import io.circe.Json
import io.circe.parser._
import munit.CatsEffectSuite

import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths

trait InputStreamSuite extends CatsEffectSuite {

  def jsonResult(jsonFile: String): IO[Json] = {
    val path = Path(s"lucuma-schemas/src/test/resources") / jsonFile

    Files[IO].readUtf8(path).compile.string.flatMap { str =>
      IO.fromEither(parse(str))
    }
  }

  def parsedResult[A: Decoder](jsonFile: String): IO[Decoder.Result[A]] =
    jsonResult(jsonFile).map(_.as[A])

  def assertParsedStreamEquals[A: Decoder](jsonFile: String, expected: A): IO[Unit] =
    for {
      r <- parsedResult[A](jsonFile)
      o <- IO.fromEither(r)
    } yield assertEquals(o, expected)

  def assertParsedStreamFails[A: Decoder](jsonFile: String, fail: String): IO[Unit] =
    for {
      r <- parsedResult[A](jsonFile)
      o <- IO.fromOption(r.swap.toOption)(
             new RuntimeException(s"Expected a decoding failure, but was successful: $r")
           )
    } yield assertEquals(o.message, fail)

}
