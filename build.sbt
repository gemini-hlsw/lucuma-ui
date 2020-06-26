import sbt._

val reactJS      = "16.13.1"
val scalaJsReact = "1.7.2"

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
    scalaVersion := "2.13.2",
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    )
  ) ++ gspPublishSettings
)

lazy val root: Project =
  project
    .in(file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "gpp-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"         % "2.1.1",
        "com.github.japgolly.scalajs-react" %%% "core"              % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "ext-monocle-cats"  % scalaJsReact,
        "edu.gemini"                        %%% "gsp-core-model"    % "0.2.4",
        "io.github.cquiroz.react"           %%% "common"            % "0.9.3",
        "io.github.cquiroz.react"           %%% "react-semantic-ui" % "0.5.8",
        "com.github.julien-truffaut"        %%% "monocle-core"      % "2.0.5",
        "com.rpiaggio"                      %%% "crystal"           % "0.7.1"
      ),
      addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full))
    )
