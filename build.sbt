import sbt._

lazy val reactJS                = "16.13.1"
lazy val FUILess                = "2.8.7"
lazy val scalaJsReactVersion    = "1.7.5"
lazy val lucumaCoreVersion      = "0.6.4"
lazy val monocleVersion         = "2.1.0"
lazy val crystalVersion         = "0.8.1"
lazy val catsVersion            = "2.2.0"
lazy val reactCommonVersion     = "0.11.0"
lazy val reactSemanticUIVersion = "0.9.0"
lazy val kindProjectorVersion   = "0.11.0"

parallelExecution in (ThisBuild, Test) := false

ThisBuild / turbo := true

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    homepage := Some(url("https://github.com/gemini-hlsw/lucuma-ui")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/gemini-hlsw/lucuma-ui"),
        "scm:git:git@github.com:gemini-hlsw/lucuma-ui.git"
      )
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    )
  ) ++ lucumaPublishSettings
)

skip in publish := true

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS::stopWebpackDevServer; demo/fastOptJS::startWebpackDevServer; ~demo/fastOptJS"
)

lazy val demo =
  project
    .in(file("modules/demo"))
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      version in webpack := "4.44.1",
      version in startWebpackDevServer := "3.11.0",
      webpackConfigFile in fastOptJS := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      webpackConfigFile in fullOptJS := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (resourceDirectory in Compile).value,
      webpackResources := (baseDirectory.value / "webpack") * "*.js",
      includeFilter in webpackMonitoredFiles := "*",
      useYarn := true,
      webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
      webpackBundlingMode in fullOptJS := BundlingMode.Application,
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      test := {},
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
      npmDependencies in Compile ++= Seq(
        "react"            -> reactJS,
        "react-dom"        -> reactJS,
        "fomantic-ui-less" -> FUILess
      ),
      skip in publish := true
    )
    .dependsOn(ui)

lazy val ui =
  project
    .in(file("modules/ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(lucumaScalaJsSettings: _*)
    .settings(
      name := "lucuma-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"         % catsVersion,
        "com.github.japgolly.scalajs-react" %%% "core"              % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "ext-monocle-cats"  % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"       % lucumaCoreVersion,
        "io.github.cquiroz.react"           %%% "common"            % reactCommonVersion,
        "io.github.cquiroz.react"           %%% "react-semantic-ui" % reactSemanticUIVersion,
        "com.github.julien-truffaut"        %%% "monocle-core"      % monocleVersion,
        "com.rpiaggio"                      %%% "crystal"           % crystalVersion
      ),
      addCompilerPlugin(
        ("org.typelevel" %% "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)
      )
    )
