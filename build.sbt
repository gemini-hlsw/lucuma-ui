val clueVersion       = "0.18.4"
val lucumaCoreVersion = "0.14.1"

inThisBuild(
  List(
    scalaVersion                                             := "2.13.6",
    homepage                                                 := Some(url("https://github.com/gemini-hlsw/lucuma-schemas")),
    Global / onChangedBuildSource                            := ReloadOnSourceChanges,
    scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion,
    scalafixScalaBinaryVersion                               := "2.13",
    ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false
  ) ++ lucumaPublishSettings
)

val scala2Version = "2.13.6"
val allVersions   = List(scala2Version)

val dependencies = List(
  "edu.gemini" %% "clue-core"   % clueVersion,
  "edu.gemini" %% "lucuma-core" % lucumaCoreVersion
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
      libraryDependencies ++= dependencies
    )

val lucumaSchemas =
  projectMatrix
    .in(file("lucuma-schemas"))
    .settings(
      moduleName := "lucuma-schemas",
      libraryDependencies ++= dependencies,
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
