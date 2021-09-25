import sbt._

lazy val reactJS                = "17.0.2"
lazy val FUILess                = "2.8.7"
lazy val scalaJsReactVersion    = "2.0.0-RC3"
lazy val lucumaCoreVersion      = "0.13.2"
lazy val monocleVersion         = "3.1.0"
lazy val crystalVersion         = "0.15.3"
lazy val catsVersion            = "2.6.1"
lazy val mouseVersion           = "1.0.4"
lazy val reactCommonVersion     = "0.13.1"
lazy val reactSemanticUIVersion = "0.11.1"
lazy val kindProjectorVersion   = "0.13.2"
lazy val singletonOpsVersion    = "0.5.2"

Global / onChangedBuildSource           := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports; scalafmtAll"
)

inThisBuild(
  Seq(
    homepage                 := Some(url("https://github.com/gemini-hlsw/lucuma-ui")),
    scmInfo                  := Some(
      ScmInfo(
        url("https://github.com/gemini-hlsw/lucuma-ui"),
        "scm:git:git@github.com:gemini-hlsw/lucuma-ui.git"
      )
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    ),
    turbo                    := true,
    Test / parallelExecution := false
  ) ++ lucumaPublishSettings
)

publish / skip := true

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS/stopWebpackDevServer; demo/fastOptJS/startWebpackDevServer; ~demo/fastOptJS"
)

lazy val demo =
  project
    .in(file("modules/demo"))
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      webpack / version                      := "4.44.1",
      startWebpackDevServer / version        := "3.11.0",
      fastOptJS / webpackConfigFile          := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      fullOptJS / webpackConfigFile          := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (Compile / resourceDirectory).value,
      webpackResources                       := (baseDirectory.value / "webpack") * "*.js",
      Compile / includeFilter                := "*",
      useYarn                                := true,
      fastOptJS / webpackBundlingMode        := BundlingMode.LibraryOnly(),
      fullOptJS / webpackBundlingMode        := BundlingMode.Application,
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      test                                   := {},
      libraryDependencies += "com.rpiaggio" %%% "log4cats-loglevel" % "0.3.0",
      // NPM libs for development, mostly to let webpack do its magic
      Compile / npmDevDependencies ++= Seq(
        "postcss"                       -> "8.1.1",
        "postcss-loader"                -> "4.0.3",
        "autoprefixer"                  -> "10.0.1",
        "url-loader"                    -> "4.1.0",
        "file-loader"                   -> "6.0.0",
        "css-loader"                    -> "3.5.3",
        "style-loader"                  -> "1.2.1",
        // Don't upgrade less until https://github.com/less/less.js/issues/3434 is fixed
        "less"                          -> "3.9.0",
        "less-loader"                   -> "7.0.1",
        "sass"                          -> "1.26.11",
        "sass-loader"                   -> "9.0.2",
        "webpack-merge"                 -> "4.2.2",
        "mini-css-extract-plugin"       -> "0.9.0",
        "webpack-dev-server-status-bar" -> "1.1.2",
        "cssnano"                       -> "4.1.10",
        "terser-webpack-plugin"         -> "4.2.2",
        "html-webpack-plugin"           -> "4.3.0",
        "css-minimizer-webpack-plugin"  -> "1.1.5",
        "favicons-webpack-plugin"       -> "4.2.0"
      ),
      Compile / npmDependencies ++= Seq(
        "react"            -> reactJS,
        "react-dom"        -> reactJS,
        "fomantic-ui-less" -> FUILess
      ),
      publish / skip                         := true
    )
    .dependsOn(ui)

lazy val ui   =
  project
    .in(file("modules/ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(lucumaScalaJsSettings: _*)
    .settings(
      name               := "lucuma-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"           % catsVersion,
        "com.github.japgolly.scalajs-react" %%% "core"                % scalaJsReactVersion,
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
      ),
      addCompilerPlugin(
        ("org.typelevel" %% "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)
      ),
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
      testFrameworks += new TestFramework("munit.Framework")
    )
