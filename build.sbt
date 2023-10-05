val circeVersion           = "0.14.6"
val disciplineMUnitVersion = "1.0.9"
val fs2Version             = "3.9.2"
val kittensVersion         = "3.0.0"
val munitVersion           = "0.7.29"
val munitCatsEffectVersion = "1.0.7"
val lucumaCoreVersion      = "0.85.1"
val lucumaODBSchema        = "0.6.0"

ThisBuild / tlBaseVersion       := "0.62"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / crossScalaVersions  := Seq("3.3.1")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
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
        "edu.gemini"    %%% "lucuma-core"       % lucumaCoreVersion,
        "edu.gemini"    %%% "lucuma-odb-schema" % lucumaODBSchema,
        "io.circe"      %%% "circe-core"        % circeVersion,
        "io.circe"      %%% "circe-generic"     % circeVersion,
        "org.typelevel" %%% "kittens"           % kittensVersion
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

val lucumaSchemas =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lucuma-schemas"))
    .dependsOn(model)
    .settings(
      moduleName                    := "lucuma-schemas",
      libraryDependencies ++= Seq(
        "co.fs2"        %%% "fs2-io"              % fs2Version             % Test,
        "org.scalameta" %%% "munit"               % munitVersion           % Test,
        "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion % Test
      ),
      Compile / clueSourceDirectory := (ThisBuild / baseDirectory).value / "lucuma-schemas" / "src" / "clue",
      // Include schema files in jar.
      Compile / unmanagedResourceDirectories += (Compile / clueSourceDirectory).value / "resources"
    )
    .jsSettings(
      Test / scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
    )
    .enablePlugins(CluePlugin)
