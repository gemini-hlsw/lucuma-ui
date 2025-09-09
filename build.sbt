import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / tlBaseVersion       := "0.161"
ThisBuild / tlCiReleaseBranches := Seq("main")

val Versions = new {
  val cats              = "2.13.0"
  val catsRetry         = "3.1.3"
  val catsTime          = "0.6.0"
  val circe             = "0.14.14"
  val circeRefined      = "0.15.1"
  val crystal           = "0.49.0"
  val disciplineMunit   = "2.0.0"
  val fs2               = "3.12.2"
  val fs2Dom            = "0.3.0-M1"
  val kittens           = "3.5.0"
  val http4s            = "0.23.30"
  val http4sDom         = "0.2.12"
  val log4catsLogLevel  = "0.3.1"
  val lucumaCore        = "0.143.0"
  val lucumaOdbSchema   = "0.28.2"
  val lucumaItc         = "0.45.3"
  val lucumaPrimeStyles = "0.5.0"
  val lucumaReact       = "0.85.0"
  val lucumaRefined     = "0.1.4"
  val monocle           = "3.3.0"
  val mouse             = "1.3.2"
  val munit             = "1.1.1"
  val munitCatsEffect   = "2.1.0"
  val pprint            = "0.9.3"
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
ThisBuild / scalaVersion             := "3.7.3"
ThisBuild / crossScalaVersions       := Seq("3.7.3")
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions", "-explain-cyclic")

enablePlugins(NoPublishPlugin)

lazy val schemasModel =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/schemas/model"))
    .settings(
      name := "lucuma-schemas-model",
      libraryDependencies ++= Seq(
        "io.circe"      %%% "circe-core"        % Versions.circe,
        "io.circe"      %%% "circe-generic"     % Versions.circe,
        "io.circe"      %%% "circe-refined"     % Versions.circeRefined,
        "org.typelevel" %%% "kittens"           % Versions.kittens,
        "edu.gemini"    %%% "lucuma-core"       % Versions.lucumaCore,
        "edu.gemini"    %%% "lucuma-odb-schema" % Versions.lucumaOdbSchema
      )
    )

lazy val schemasTestkit =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/schemas/testkit"))
    .dependsOn(schemasModel)
    .settings(
      name := "lucuma-schemas-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % Versions.lucumaCore
      )
    )

lazy val schemasTests =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Full)
    .in(file("modules/schemas/tests"))
    .dependsOn(schemasTestkit)
    .enablePlugins(NoPublishPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "discipline-munit" % Versions.disciplineMunit % Test,
        "org.scalameta" %%% "munit"            % Versions.munit           % Test
      )
    )

lazy val schemas =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/schemas/lucuma-schemas"))
    .dependsOn(schemasModel)
    .settings(
      name                          := "lucuma-schemas",
      libraryDependencies ++= Seq(
        "co.fs2"        %%% "fs2-io"            % Versions.fs2             % Test,
        "org.scalameta" %%% "munit"             % Versions.munit           % Test,
        "org.typelevel" %%% "munit-cats-effect" % Versions.munitCatsEffect % Test
      ),
      Compile / clueSourceDirectory := (ThisBuild / baseDirectory).value / "modules" / "schemas" / "lucuma-schemas" / "src" / "clue",
      // Include schema files in jar.
      Compile / unmanagedResourceDirectories += (Compile / clueSourceDirectory).value / "resources",
      createNpmProject              := {
        val npmDir = target.value / "npm"

        val schemaFile =
          (Compile / clueSourceDirectory).value / "resources" / "lucuma" / "schemas" / "ObservationDB.graphql"

        IO.write(
          npmDir / "package.json",
          s"""|{
             |  "name": "lucuma-schemas",
             |  "version": "${version.value}",
             |  "license": "${licenses.value.head._1}",
             |  "exports": {
             |    "./odb": "./${schemaFile.getName}"
             |  },
             |  "repository": {
             |    "type": "git",
             |    "url": "git+https://github.com/gemini-hlsw/lucuma-ui.git"
             |  }
             |}
             |""".stripMargin
        )

        IO.copyFile(schemaFile, npmDir / schemaFile.getName)

        streams.value.log.info(s"Created NPM project in ${npmDir}")
      },
      npmPublish                    := {
        import scala.sys.process._
        val npmDir = target.value / "npm"

        val _ = createNpmProject.value
        Process(List("npm", "publish"), npmDir).!!
      }
    )
    .jsSettings(
      Test / scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
    )
    .enablePlugins(CluePlugin)

lazy val lucumaUi =
  project
    .in(file("modules/ui/ui"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(schemas.js)
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
        "edu.gemini"                        %%% "lucuma-itc-client"            % Versions.lucumaItc,
        "edu.gemini"                        %%% "lucuma-react-common"          % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-react-prime-react"     % Versions.lucumaReact,
        "edu.gemini"                        %%% "lucuma-prime-styles"          % Versions.lucumaPrimeStyles,
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
        "edu.gemini"                        %%% "lucuma-sso-frontend-client"   % Versions.lucumaOdbSchema
      )
    )

lazy val testkit =
  project
    .in(file("modules/ui/testkit"))
    .dependsOn(lucumaUi, schemasTestkit.js)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % Versions.lucumaCore
      )
    )

lazy val tests =
  project
    .in(file("modules/ui/tests"))
    .dependsOn(testkit)
    .settings(
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "lucuma-core-testkit" % Versions.lucumaCore      % Test,
        "org.scalameta" %%% "munit"               % Versions.munit           % Test,
        "org.typelevel" %%% "discipline-munit"    % Versions.disciplineMunit % Test
      )
    )
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin)

// for publishing packages to npm
lazy val createNpmProject = taskKey[Unit]("Create NPM project, package.json and files")
lazy val npmPublish       = taskKey[Unit]("Run npm publish")

lazy val css = project
  .in(file("modules/ui/css"))
  .dependsOn(lucumaUi)
  .enablePlugins(LucumaCssPlugin, NoPublishPlugin)
  .settings(
    createNpmProject := {
      val _      = (Compile / lucumaCss).value
      val cssDir = target.value / "lucuma-css"
      IO.write(
        cssDir / "package.json",
        s"""|{
          |  "name": "lucuma-ui-css",
          |  "version": "${version.value}",
          |  "license": "${licenses.value.head._1}",
          |  "repository": {
          |    "type": "git",
          |    "url": "git+https://github.com/gemini-hlsw/lucuma-ui.git"
          |  }
          |}
          |""".stripMargin
      )
      streams.value.log.info(s"Created NPM project in ${cssDir}")
    },
    npmPublish       := {
      import scala.sys.process._
      val cssDir = target.value / "lucuma-css"

      val _ = createNpmProject.value
      Process(List("npm", "publish"), cssDir).!!
    }
  )

lazy val demo =
  project
    .in(file("modules/ui/demo"))
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin, LucumaCssPlugin)
    .dependsOn(lucumaUi, css)
    .settings(
      Compile / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      Compile / fastLinkJS / scalaJSLinkerConfig ~= (_.withModuleSplitStyle(
        ModuleSplitStyle.FewestModules
      )),
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats_effect" % Versions.scalaJsReact,
        "com.rpiaggio"                      %%% "log4cats-loglevel"        % Versions.log4catsLogLevel,
        "edu.gemini"                        %%% "lucuma-react-grid-layout" % Versions.lucumaReact
      ),
      Keys.test := {}
    )

ThisBuild / githubWorkflowSbtCommand := "sbt -v -J-Xmx6g"

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(
    UseRef.Public("actions", "setup-node", "v4"),
    Map(
      "node-version" -> "24",
      "registry-url" -> "https://registry.npmjs.org",
      "cache"        -> "npm"
    )
  )

ThisBuild / githubWorkflowPublish ++= Seq(
  WorkflowStep.Sbt(
    List("css/npmPublish", "schemasJVM/npmPublish"),
    name = Some("NPM Publish"),
    env = Map("NODE_AUTH_TOKEN" -> s"$${{ secrets.NPM_REPO_TOKEN }}"),
    cond = Some("startsWith(github.ref, 'refs/tags/v')")
  )
)
