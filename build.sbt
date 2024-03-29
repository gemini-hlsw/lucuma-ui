import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / tlBaseVersion       := "0.96"
ThisBuild / tlCiReleaseBranches := Seq("master")

val Versions = new {
  val cats              = "2.10.0"
  val catsRetry         = "3.1.3"
  val circe             = "0.14.6"
  val crystal           = "0.37.3"
  val fs2Dom            = "0.3.0-M1"
  val kittens           = "3.3.0"
  val http4s            = "0.23.26"
  val http4sDom         = "0.2.11"
  val lucumaCore        = "0.94.1"
  val lucumaPrimeStyles = "0.2.10"
  val lucumaReact       = "0.55.0"
  val lucumaRefined     = "0.1.2"
  val lucumaSchemas     = "0.77.2"
  val lucumaSso         = "0.6.14"
  val monocle           = "3.2.0"
  val mouse             = "1.2.3"
  val pprint            = "0.8.1"
  val scalaJsReact      = "3.0.0-beta3"
}

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
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats_effect" % Versions.scalaJsReact
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
        "org.typelevel"                     %%% "cats-core"                    % Versions.cats,
        "org.typelevel"                     %%% "kittens"                      % Versions.kittens,
        "org.typelevel"                     %%% "mouse"                        % Versions.mouse,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % Versions.scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % Versions.scalaJsReact,
        "edu.gemini"                        %%% "lucuma-core"                  % Versions.lucumaCore,
        "edu.gemini"                        %%% "lucuma-react-common"          % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-floatingui"      % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-prime-react"     % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-prime-styles"          % Versions.lucumaPrimeStyles,
        "edu.gemini"                        %%% "lucuma-schemas"               % Versions.lucumaSchemas,
        "dev.optics"                        %%% "monocle-core"                 % Versions.monocle,
        "dev.optics"                        %%% "monocle-macro"                % Versions.monocle,
        "edu.gemini"                        %%% "crystal"                      % Versions.crystal,
        "com.lihaoyi"                       %%% "pprint"                       % Versions.pprint,
        "com.armanbilge"                    %%% "fs2-dom"                      % Versions.fs2Dom,
        "org.http4s"                        %%% "http4s-core"                  % Versions.http4s,
        "org.http4s"                        %%% "http4s-circe"                 % Versions.http4s,
        "org.http4s"                        %%% "http4s-dom"                   % Versions.http4sDom,
        "com.github.cb372"                  %%% "cats-retry"                   % Versions.catsRetry,
        "io.circe"                          %%% "circe-core"                   % Versions.circe,
        "io.circe"                          %%% "circe-parser"                 % Versions.circe,
        "edu.gemini"                        %%% "lucuma-sso-frontend-client"   % Versions.lucumaSso
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
        "edu.gemini" %%% "lucuma-core-testkit" % Versions.lucumaCore
      )
    )

lazy val tests =
  project
    .in(file("modules/tests"))
    .dependsOn(testkit)
    .settings(
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "lucuma-core-testkit"    % Versions.lucumaCore    % Test,
        "edu.gemini"    %%% "lucuma-schemas-testkit" % Versions.lucumaSchemas % Test,
        "org.scalameta" %%% "munit"                  % "0.7.29"               % Test,
        "org.typelevel" %%% "discipline-munit"       % "1.0.9"                % Test
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
            |  "": "${version.value}",
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
