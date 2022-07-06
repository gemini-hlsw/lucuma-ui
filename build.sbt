val clueVersion            = "0.23.0"
val lucumaCoreVersion2     = "0.43.0"
val lucumaCoreVersion      = "0.44-13de521-SNAPSHOT"
val munitVersion           = "0.7.29"
val munitCatsEffectVersion = "1.0.7"

ThisBuild / tlBaseVersion       := "0.30"
ThisBuild / tlCiReleaseBranches := Seq("main", "scala3")
ThisBuild / crossScalaVersions  := Seq("2.13.8", "3.1.2")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion
ThisBuild / scalafixScalaBinaryVersion                               := "2.13"
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

val scala2Version = "3.1.2"
val allVersions   = List("3.1.2")

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
      libraryDependencies ++= {
        if (tlIsScala3.value) {
          Seq(
            "edu.gemini" %% "clue-core"   % clueVersion,
            "edu.gemini" %% "lucuma-core" % lucumaCoreVersion
          )
        } else {
          Seq(
            "edu.gemini" %% "clue-core"   % clueVersion,
            "edu.gemini" %% "lucuma-core" % lucumaCoreVersion2
          )
        }
      }
    )

val lucumaSchemas =
  projectMatrix
    .in(file("lucuma-schemas"))
    .settings(
      moduleName := "lucuma-schemas",
      libraryDependencies ++= {
        if (tlIsScala3.value) {
          Seq(
            "edu.gemini" %% "clue-core"   % clueVersion,
            "edu.gemini" %% "lucuma-core" % lucumaCoreVersion
          )
        } else {
          Seq(
            "edu.gemini" %% "clue-core"   % clueVersion,
            "edu.gemini" %% "lucuma-core" % lucumaCoreVersion2
          )
        }
      },
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
    .defaultAxes(VirtualAxis.jvm, VirtualAxis.scalaPartialVersion(scala2Version))
    .jvmPlatform(allVersions)
    .jsPlatform(allVersions)
