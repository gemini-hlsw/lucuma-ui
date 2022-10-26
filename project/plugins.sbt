addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.0")
val sbtLucumaVersion = "0.10.4"
addSbtPlugin("edu.gemini" % "sbt-lucuma-lib"         % sbtLucumaVersion)
addSbtPlugin("edu.gemini" % "sbt-lucuma-sjs-bundler" % sbtLucumaVersion)
