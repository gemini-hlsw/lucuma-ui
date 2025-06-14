import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / tlBaseVersion       := "0.146"
ThisBuild / tlCiReleaseBranches := Seq("master")

val Versions = new {
  val cats              = "2.13.0"
  val catsRetry         = "3.1.3"
  val catsTime          = "0.5.1"
  val circe             = "0.14.13"
  val crystal           = "0.48.0"
  val disciplineMunit   = "2.0.0"
  val fs2Dom            = "0.3.0-M1"
  val kittens           = "3.5.0"
  val http4s            = "0.23.30"
  val http4sDom         = "0.2.12"
  val log4catsLogLevel  = "0.3.1"
  val lucumaCore        = "0.135.0"
  val lucumaPrimeStyles = "0.3.0"
  val lucumaReact       = "0.84.0"
  val lucumaRefined     = "0.1.4"
  val lucumaSchemas     = "0.141.0"
  val lucumaSso         = "0.8.20"
  val monocle           = "3.3.0"
  val mouse             = "1.3.2"
  val munit             = "1.1.1"
  val pprint            = "0.9.0"
  val scalaJsReact      = "3.0.0-beta12"
}

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports"
)

ThisBuild / turbo                    := true
ThisBuild / Test / parallelExecution := false
ThisBuild / scalaVersion             := "3.7.1"
ThisBuild / crossScalaVersions       := Seq("3.7.1")
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions", "-explain-cyclic")

enablePlugins(NoPublishPlugin)

lazy val demo =
  project
    .in(file("modules/demo"))
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin, LucumaCssPlugin)
    .dependsOn(ui, css)
    .settings(
      Compile / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      Compile / fastLinkJS / scalaJSLinkerConfig ~= (_.withModuleSplitStyle(
        ModuleSplitStyle.SmallestModules
      )),
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats_effect" % Versions.scalaJsReact,
        "com.rpiaggio"                      %%% "log4cats-loglevel"        % Versions.log4catsLogLevel,
        "edu.gemini"                        %%% "lucuma-react-grid-layout" % Versions.lucumaReact
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
        "org.typelevel"                     %%% "cats-time"                    % Versions.catsTime,
        "org.typelevel"                     %%% "kittens"                      % Versions.kittens,
        "org.typelevel"                     %%% "mouse"                        % Versions.mouse,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % Versions.scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % Versions.scalaJsReact,
        "edu.gemini"                        %%% "lucuma-core"                  % Versions.lucumaCore,
        "edu.gemini"                        %%% "lucuma-ags"                   % Versions.lucumaCore,
        "edu.gemini"                        %%% "lucuma-react-common"          % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % Versions.lucumaReact,
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
        "edu.gemini"    %%% "lucuma-core-testkit"    % Versions.lucumaCore      % Test,
        "edu.gemini"    %%% "lucuma-schemas-testkit" % Versions.lucumaSchemas   % Test,
        "org.scalameta" %%% "munit"                  % Versions.munit           % Test,
        "org.typelevel" %%% "discipline-munit"       % Versions.disciplineMunit % Test
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
