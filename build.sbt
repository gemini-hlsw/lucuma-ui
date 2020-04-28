import sbt._

val reactJS      = "16.7.0"
val scalaJsReact = "1.6.0"

parallelExecution in (ThisBuild, Test) := false

ThisBuild / turbo := true

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    homepage := Some(url("https://github.com/gemini-hlsw/gpp-ui")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/gemini-hlsw/gpp-ui"),
        "scm:git:git@github.com:gemini-hlsw/gpp-ui.git"
      )
    ),
    scalaVersion := "2.13.1"
  ) ++ gspPublishSettings
)

lazy val root: Project =
  project
    .in(file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "gpp-ui",
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "cats-core" % "2.1.1",
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReact,
        "edu.gemini" %%% "gsp-core-model" % "0.1.8",
        "io.github.cquiroz.react" %%% "common" % "0.7.1",
        "io.github.cquiroz.react" %%% "react-semantic-ui" % "0.4.11",
        "com.github.julien-truffaut" %%% "monocle-core" % "2.0.4"
      )
    )
