// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import cats.effect._
import io.circe.Decoder
import io.circe.Json
import io.circe.parser._
import munit.CatsEffectSuite

import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths

trait InputStreamSuite extends CatsEffectSuite {
  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO.blocking(new FileInputStream(f)) // build
    } { inStream =>
      IO.blocking(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def jsonResult(jsonFile: String): IO[Json] = {
    val url  = getClass.getResource(jsonFile)
    val file = Paths.get(url.toURI).toFile

    inputStream(file).use { inStream =>
      for {
        str  <- IO.blocking(scala.io.Source.fromInputStream(inStream).mkString)
        json <- IO.fromEither(parse(str))
      } yield json
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
