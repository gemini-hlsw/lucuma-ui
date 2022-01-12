val clueVersion            = "0.20.0"
val lucumaCoreVersion      = "0.23.1"
val munitVersion           = "0.7.29"
val munitCatsEffectVersion = "1.0.7"

inThisBuild(
  List(
    homepage                                                 := Some(url("https://github.com/gemini-hlsw/lucuma-schemas")),
    Global / onChangedBuildSource                            := ReloadOnSourceChanges,
    scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion,
    scalafixScalaBinaryVersion                               := "2.13",
    ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false,
    semanticdbEnabled                                        := true,
    semanticdbVersion                                        := scalafixSemanticdb.revision
  ) ++ lucumaPublishSettings
)

val scala2Version = "2.13.7"
val allVersions   = List(scala2Version)

val coreDependencies = List(
  "edu.gemini" %% "clue-core"   % clueVersion,
  "edu.gemini" %% "lucuma-core" % lucumaCoreVersion
)

val schemasDependencies = List(
  "org.scalameta" %% "munit"               % munitVersion           % Test,
  "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectVersion % Test
)

lazy val root = project
  .in(file("."))
  .aggregate(lucumaSchemas.projectRefs: _*)
  .settings(
    publish / skip := true
  )

val templates =
  project
    .in(file("templates"))
    .settings(
      publish / skip := true,
      libraryDependencies ++= coreDependencies
    )

val lucumaSchemas =
  projectMatrix
    .in(file("lucuma-schemas"))
    .settings(
      moduleName := "lucuma-schemas",
      libraryDependencies ++= coreDependencies,
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
    .jsPlatform(allVersions,
                List(scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)))
    )
