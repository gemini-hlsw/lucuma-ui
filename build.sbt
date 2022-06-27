ThisBuild / tlBaseVersion       := "0.36"
ThisBuild / tlCiReleaseBranches := Seq("master")

lazy val reactJS = "17.0.2"
lazy val FUILess = "2.8.7"

lazy val catsVersion            = "2.8.0"
lazy val crystalVersion         = "0.28.1"
lazy val lucumaCoreVersion      = "0.42.1"
lazy val monocleVersion         = "3.1.0"
lazy val mouseVersion           = "1.1.0"
lazy val reactCommonVersion     = "0.17.0"
lazy val reactSemanticUIVersion = "0.15.1"
lazy val scalaJsReactVersion    = "2.1.1"

lazy val kindProjectorVersion = "0.13.2"
lazy val singletonOpsVersion  = "0.5.2"

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports; scalafmtAll"
)

ThisBuild / scalacOptions ++= Seq(
  "-Ymacro-annotations"
)

ThisBuild / turbo                    := true
ThisBuild / Test / parallelExecution := false

enablePlugins(NoPublishPlugin)

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS/stopWebpackDevServer; demo/fastOptJS/startWebpackDevServer; ~demo/fastOptJS"
)

lazy val demo =
  project
    .in(file("modules/demo"))
    .enablePlugins(ScalaJSBundlerPlugin, NoPublishPlugin)
    .settings(
      webpack / version                     := "4.44.1",
      startWebpackDevServer / version       := "3.11.0",
      fastOptJS / webpackConfigFile         := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      fullOptJS / webpackConfigFile         := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (Compile / resourceDirectory).value,
      webpackResources                      := (baseDirectory.value / "webpack") * "*.js",
      webpackMonitoredFiles / includeFilter := "*",
      useYarn                               := true,
      fastOptJS / webpackBundlingMode       := BundlingMode.LibraryOnly(),
      fullOptJS / webpackBundlingMode       := BundlingMode.Application,
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      test                                  := {},
      Compile / doc / sources               := Seq.empty,
      libraryDependencies ++= List(
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats" % scalaJsReactVersion,
        "com.rpiaggio"                      %%% "log4cats-loglevel" % "0.3.1"
      )
    )
    .dependsOn(ui)

lazy val ui =
  project
    .in(file("modules/ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"           % catsVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"   % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"  % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"         % lucumaCoreVersion,
        "eu.timepit"                        %%% "singleton-ops"       % singletonOpsVersion,
        "io.github.cquiroz.react"           %%% "common"              % reactCommonVersion,
        "io.github.cquiroz.react"           %%% "cats"                % reactCommonVersion,
        "io.github.cquiroz.react"           %%% "react-semantic-ui"   % reactSemanticUIVersion,
        "dev.optics"                        %%% "monocle-core"        % monocleVersion,
        "dev.optics"                        %%% "monocle-macro"       % monocleVersion,
        "com.rpiaggio"                      %%% "crystal"             % crystalVersion,
        "org.typelevel"                     %%% "mouse"               % mouseVersion,
        "edu.gemini"                        %%% "lucuma-core-testkit" % lucumaCoreVersion % Test,
        "org.scalameta"                     %%% "munit"               % "0.7.29"          % Test,
        "org.typelevel"                     %%% "discipline-munit"    % "1.0.9"           % Test
      )
    )
