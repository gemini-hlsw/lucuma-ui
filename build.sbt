import sbt._

lazy val reactJS                = "16.13.1"
lazy val scalaJsReactVersion    = "1.7.5"
lazy val gspCoreModelVersion    = "0.2.8"
lazy val gspMathVersion         = "0.3.0"
lazy val monocleVersion         = "2.1.0"
lazy val crystalVersion         = "0.7.1"
lazy val catsVersion            = "2.1.1"
lazy val reactCommonVersion     = "0.9.7"
lazy val reactSemanticUIVersion = "0.6.3"
lazy val kindProjectorVersion   = "0.11.0"

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
    scalaVersion := "2.13.3",
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
        "org.typelevel"                     %%% "cats-core"         % catsVersion,
        "com.github.japgolly.scalajs-react" %%% "core"              % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "ext-monocle-cats"  % scalaJsReactVersion,
        "edu.gemini"                        %%% "gsp-core-model"    % gspCoreModelVersion,
        "edu.gemini"                        %%% "gsp-math"          % gspMathVersion,
        "io.github.cquiroz.react"           %%% "common"            % reactCommonVersion,
        "io.github.cquiroz.react"           %%% "react-semantic-ui" % reactSemanticUIVersion,
        "com.github.julien-truffaut"        %%% "monocle-core"      % monocleVersion,
        "com.rpiaggio"                      %%% "crystal"           % crystalVersion
      ),
      addCompilerPlugin(
        ("org.typelevel" %% "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)
      )
    )
