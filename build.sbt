val clueVersion            = "0.23.1"
val lucumaCoreVersion2     = "0.45.0"
val lucumaCoreVersion      = "0.54.0"
val munitVersion           = "0.7.29"
val munitCatsEffectVersion = "1.0.7"
val kittensVersion         = "3.0.0-M4"

ThisBuild / tlBaseVersion       := "0.37"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / crossScalaVersions  := Seq("3.2.0")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion
ThisBuild / scalafixScalaBinaryVersion                               := "2.13"
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

val schemasDependencies = List(
  "org.scalameta" %% "munit"               % munitVersion           % Test,
  "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectVersion % Test
)

lazy val root = tlCrossRootProject.aggregate(lucumaSchemas)

val templates =
  project
    .in(file("templates"))
    .enablePlugins(NoPublishPlugin)
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
    .settings(
      moduleName := "lucuma-schemas",
      libraryDependencies ++= Seq(
        "edu.gemini"    %% "clue-core"   % clueVersion,
        "edu.gemini"    %% "lucuma-core" % lucumaCoreVersion,
        "org.typelevel" %% "kittens"     % kittensVersion
      ),
      libraryDependencies ++= schemasDependencies,
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
