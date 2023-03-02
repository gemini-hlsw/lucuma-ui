val clueVersion            = "0.25.1"
val disciplineMUnitVersion = "1.0.9"
val lucumaCoreVersion      = "0.70.0"
val fs2Version             = "3.6.1"
val munitVersion           = "0.7.29"
val munitCatsEffectVersion = "1.0.7"
val kittensVersion         = "3.0.0"

ThisBuild / tlBaseVersion       := "0.45"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / crossScalaVersions  := Seq("3.2.2")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion
ThisBuild / scalafixScalaBinaryVersion                               := "2.13"
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

lazy val root = tlCrossRootProject.aggregate(model, testkit, lucumaSchemas, modelTests)

val model =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("model"))
    .settings(
      moduleName := "lucuma-schemas-model",
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "lucuma-core" % lucumaCoreVersion,
        "org.typelevel" %%% "kittens"     % kittensVersion
      )
    )

val testkit =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("testkit"))
    .dependsOn(model)
    .settings(
      moduleName := "lucuma-schemas-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % lucumaCoreVersion
      )
    )

val modelTests =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("tests"))
    .dependsOn(testkit)
    .enablePlugins(NoPublishPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit"            % munitVersion           % Test,
        "org.typelevel" %%% "discipline-munit" % disciplineMUnitVersion % Test
      )
    )

val templates =
  project
    .in(file("templates"))
    .enablePlugins(NoPublishPlugin)
    .dependsOn(model.jvm)
    .settings(
      libraryDependencies ++= Seq(
        "edu.gemini" %% "clue-core"   % clueVersion,
        "edu.gemini" %% "lucuma-core" % lucumaCoreVersion
      )
    )

val lucumaSchemas =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lucuma-schemas"))
    .dependsOn(model)
    .settings(
      moduleName := "lucuma-schemas",
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "clue-core"           % clueVersion,
        "co.fs2"        %%% "fs2-io"              % fs2Version             % Test,
        "org.scalameta" %%% "munit"               % munitVersion           % Test,
        "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion % Test
      ),
      Compile / sourceGenerators += Def.taskDyn {
        val root    = (ThisBuild / baseDirectory).value.toURI.toString
        val from    = (templates / Compile / sourceDirectory).value
        val to      = (Compile / sourceManaged).value
        val outFrom = from.toURI.toString.stripSuffix("/").stripPrefix(root)
        val outTo   = to.toURI.toString.stripSuffix("/").stripPrefix(root)
        Def.task {
          (templates / Compile / scalafix)
            .toTask(s" GraphQLGen --out-from=$outFrom --out-to=$outTo")
            .value
          (to ** "*.scala").get
        }
      }.taskValue,
      // Include schema files from templates in jar.
      Compile / unmanagedResourceDirectories += (templates / Compile / resourceDirectory).value
    )
    .jsSettings(
      Test / scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
    )
