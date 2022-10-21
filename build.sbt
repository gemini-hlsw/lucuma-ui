ThisBuild / tlBaseVersion       := "0.50"
ThisBuild / tlCiReleaseBranches := Seq("master")

lazy val reactJS = "17.0.2"
lazy val FUILess = "2.8.7"
val reactSUI     = "2.0.4"

lazy val catsVersion              = "2.8.0"
lazy val crystalVersion           = "0.33.2"
lazy val kittensVersion           = "3.0.0"
lazy val lucumaCoreVersion        = "0.57.0"
lazy val monocleVersion           = "3.1.0"
lazy val mouseVersion             = "1.1.0"
lazy val lucumaPrimeStylesVersion = "0.2.4"
lazy val lucumaRefinedVersion     = "0.1.0"
lazy val lucumaReactVersion       = "0.15.0"
lazy val scalaJsReactVersion      = "2.1.1"
lazy val pprintVersion            = "0.8.0"

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports"
)

ThisBuild / turbo                    := true
ThisBuild / Test / parallelExecution := false
ThisBuild / scalaVersion             := "3.2.1-RC2"
ThisBuild / crossScalaVersions       := Seq("3.2.1-RC2")
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions")

enablePlugins(NoPublishPlugin)

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS/stopWebpackDevServer; demo/fastOptJS/startWebpackDevServer; ~demo/fastOptJS"
)

addCommandAlias(
  "stopWDS",
  "; demo/fastOptJS/stopWebpackDevServer"
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
      webpackDevServerPort                  := 7800,
      Compile / npmDependencies ++= Seq(
        "react"             -> reactJS,
        "react-dom"         -> reactJS,
        "semantic-ui-react" -> reactSUI
      ),
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
        "org.typelevel"                     %%% "cats-core"                    % catsVersion,
        "org.typelevel"                     %%% "kittens"                      % kittensVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"                  % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-react-common"          % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-semantic-ui"     % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-floatingui"      % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-prime-react"     % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-prime-styles"          % lucumaPrimeStylesVersion,
        "dev.optics"                        %%% "monocle-core"                 % monocleVersion,
        "dev.optics"                        %%% "monocle-macro"                % monocleVersion,
        "edu.gemini"                        %%% "crystal"                      % crystalVersion,
        "com.lihaoyi"                       %%% "pprint"                       % pprintVersion,
        "edu.gemini"                        %%% "lucuma-core-testkit"          % lucumaCoreVersion % Test,
        "org.scalameta"                     %%% "munit"                        % "0.7.29"          % Test,
        "org.typelevel"                     %%% "discipline-munit"             % "1.0.9"           % Test
      )
    )
