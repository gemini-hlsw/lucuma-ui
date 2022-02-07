resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "sonatype-s01-snapshots".at(
  "https://s01.oss.sonatype.org/content/repositories/snapshots"
)

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("edu.gemini"    % "sbt-lucuma-lib"      % "0.6-cde3ac9-SNAPSHOT")
