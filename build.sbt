val clueVersion = "0.18.1+10-ec070959+20210929-2013-SNAPSHOT"

inThisBuild(
  List(
    scalaVersion                                             := "2.13.6",
    Global / onChangedBuildSource                            := ReloadOnSourceChanges,
    scalafixDependencies += "edu.gemini"                     %% "clue-generator" % clueVersion,
    scalafixScalaBinaryVersion                               := "2.13",
    ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false
  ) ++ lucumaPublishSettings
)

lazy val root = project
  .in(file("."))
  .aggregate(lucumaDBClient)
  .settings(
    publish / skip := true
  )

val templates =
  project
    .in(file("templates"))
    .settings(
      publish / skip                      := true,
      libraryDependencies += "edu.gemini" %% "clue-core" % clueVersion
    )

val lucumaDBClient =
  project
    .in(file("lucuma-db-client"))
    .settings(
      moduleName                          := "lucuma-db-client",
      libraryDependencies += "edu.gemini" %% "clue-core" % clueVersion,
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
      }.taskValue
    )
