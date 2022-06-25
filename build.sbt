ThisBuild / tlCiReleaseBranches := Seq("master")

lazy val reactJS = "17.0.2"
lazy val FUILess = "2.8.7"

lazy val catsVersion          = "2.8.0"
lazy val crystalVersion       = "0.29.0"
lazy val lucumaCoreVersion    = "0.41-8517424-SNAPSHOT"
lazy val monocleVersion       = "3.1.0"
lazy val mouseVersion         = "1.1.0"
lazy val lucumaReactVersion   = "1.0-2f1c8d8-SNAPSHOT"
lazy val lucumaRefinedVersion = "0.0-21cb1ca-SNAPSHOT"
lazy val scalaJsReactVersion  = "2.1.1"

lazy val kindProjectorVersion = "0.13.2"
lazy val singletonOpsVersion  = "0.5.2"

ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

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
ThisBuild / scalaVersion             := "3.1.3"
ThisBuild / crossScalaVersions       := Seq("3.1.3")
ThisBuild / scalacOptions ++= Seq(
  "-language:implicitConversions"
)
ThisBuild / tlBaseVersion            := "0.37"

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
        "org.typelevel"                     %%% "cats-core"                % catsVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"        % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"       % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"              % lucumaCoreVersion,
        // "eu.timepit"                        %%% "singleton-ops"            % singletonOpsVersion,
        "edu.gemini"                        %%% "lucuma-react-common"      % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-cats"        % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-semantic-ui" % lucumaReactVersion,
        "dev.optics"                        %%% "monocle-core"             % monocleVersion,
        "dev.optics"                        %%% "monocle-macro"            % monocleVersion,
        "com.rpiaggio"                      %%% "crystal"                  % crystalVersion,
        // "org.typelevel"                     %%% "mouse"                    % mouseVersion,
        "edu.gemini"                        %%% "lucuma-core-testkit"      % lucumaCoreVersion % Test,
        "edu.gemini"                        %%% "lucuma-refined"           % lucumaRefinedVersion,
        "org.scalameta"                     %%% "munit"                    % "0.7.29"          % Test,
        "org.typelevel"                     %%% "discipline-munit"         % "1.0.9"           % Test
      )
    )
