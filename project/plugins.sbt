addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
val sbtLucumaVersion = "0.9.5"
addSbtPlugin("edu.gemini" % "sbt-lucuma-lib"         % sbtLucumaVersion)
addSbtPlugin("edu.gemini" % "sbt-lucuma-sjs-bundler" % sbtLucumaVersion)
