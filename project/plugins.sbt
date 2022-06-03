addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
val sbtLucumaVersion = "0.8.3"
addSbtPlugin("edu.gemini" % "sbt-lucuma-lib"         % sbtLucumaVersion)
addSbtPlugin("edu.gemini" % "sbt-lucuma-sjs-bundler" % sbtLucumaVersion)
