import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / tlBaseVersion       := "0.94"
ThisBuild / tlCiReleaseBranches := Seq("master")

lazy val catsVersion              = "2.10.0"
lazy val catsRetryVersion         = "3.1.0"
lazy val circeVersion             = "0.14.6"
lazy val crystalVersion           = "0.37.3"
lazy val fs2DomVersion            = "0.3.0-M1"
lazy val kittensVersion           = "3.3.0"
lazy val http4sVersion            = "0.23.26"
lazy val http4sDomVersion         = "0.2.11"
lazy val lucumaCoreVersion        = "0.93.0"
lazy val lucumaPrimeStylesVersion = "0.2.10"
lazy val lucumaReactVersion       = "0.53.0"
lazy val lucumaRefinedVersion     = "0.1.2"
lazy val lucumaSchemasVersion     = "0.75.0"
lazy val lucumaSsoVersion         = "0.6.13"
lazy val monocleVersion           = "3.2.0"
lazy val mouseVersion             = "1.2.3"
lazy val pprintVersion            = "0.8.1"
lazy val scalaJsReactVersion      = "3.0.0-beta3"

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports"
)

ThisBuild / turbo                    := true
ThisBuild / Test / parallelExecution := false
ThisBuild / scalaVersion             := "3.4.0"
ThisBuild / crossScalaVersions       := Seq("3.4.0")
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
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin)
    .dependsOn(ui, css)
    .settings(
      Compile / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      Compile / fastLinkJS / scalaJSLinkerConfig ~= (_.withModuleSplitStyle(
        ModuleSplitStyle.SmallestModules
      )),
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats_effect" % scalaJsReactVersion
      ),
      Keys.test := {}
    )

lazy val ui =
  project
    .in(file("modules/ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"                    % catsVersion,
        "org.typelevel"                     %%% "kittens"                      % kittensVersion,
        "org.typelevel"                     %%% "mouse"                        % mouseVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"                  % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-react-common"          % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-clipboard"       % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-floatingui"      % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-prime-react"     % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-prime-styles"          % lucumaPrimeStylesVersion,
        "edu.gemini"                        %%% "lucuma-schemas"               % lucumaSchemasVersion,
        "dev.optics"                        %%% "monocle-core"                 % monocleVersion,
        "dev.optics"                        %%% "monocle-macro"                % monocleVersion,
        "edu.gemini"                        %%% "crystal"                      % crystalVersion,
        "com.lihaoyi"                       %%% "pprint"                       % pprintVersion,
        "com.armanbilge"                    %%% "fs2-dom"                      % fs2DomVersion,
        "org.http4s"                        %%% "http4s-core"                  % http4sVersion,
        "org.http4s"                        %%% "http4s-circe"                 % http4sVersion,
        "org.http4s"                        %%% "http4s-dom"                   % http4sDomVersion,
        "com.github.cb372"                  %%% "cats-retry"                   % catsRetryVersion,
        "io.circe"                          %%% "circe-core"                   % circeVersion,
        "io.circe"                          %%% "circe-parser"                 % circeVersion,
        "edu.gemini"                        %%% "lucuma-sso-frontend-client"   % lucumaSsoVersion
      )
    )

lazy val testkit =
  project
    .in(file("modules/testkit"))
    .dependsOn(ui)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % lucumaCoreVersion
      )
    )

lazy val tests =
  project
    .in(file("modules/tests"))
    .dependsOn(testkit)
    .settings(
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "lucuma-core-testkit"    % lucumaCoreVersion    % Test,
        "edu.gemini"    %%% "lucuma-schemas-testkit" % lucumaSchemasVersion % Test,
        "org.scalameta" %%% "munit"                  % "0.7.29"             % Test,
        "org.typelevel" %%% "discipline-munit"       % "1.0.9"              % Test
      )
    )
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin)

// for publishing CSS to npm
lazy val npmPublish = taskKey[Unit]("Run npm publish")

lazy val css = project
  .in(file("modules/css"))
  .dependsOn(ui)
  .enablePlugins(LucumaCssPlugin, NoPublishPlugin)
  .settings(
    npmPublish := {
      import scala.sys.process._
      val _      = (Compile / lucumaCss).value
      val cssDir = target.value / "lucuma-css"
      IO.write(
        cssDir / "package.json",
        s"""|{
            |  "name": "lucuma-ui-css",
            |  "version": "${version.value}",
            |  "license": "${licenses.value.head._1}"
            |}
            |""".stripMargin
      )
      Process(List("npm", "publish"), cssDir).!!
    }
  )

ThisBuild / githubWorkflowSbtCommand := "sbt -v -J-Xmx6g"

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(
    UseRef.Public("actions", "setup-node", "v4"),
    Map(
      "node-version" -> "20",
      "registry-url" -> "https://registry.npmjs.org"
    )
  )

ThisBuild / githubWorkflowPublish ++= Seq(
  WorkflowStep.Sbt(
    List("css/npmPublish"),
    name = Some("NPM Publish"),
    env = Map("NODE_AUTH_TOKEN" -> s"$${{ secrets.NPM_REPO_TOKEN }}")
  )
)
