addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")
val sbtLucumaVersion = "0.10.11"
addSbtPlugin("edu.gemini" % "sbt-lucuma-lib"         % sbtLucumaVersion)
addSbtPlugin("edu.gemini" % "sbt-lucuma-css"         % sbtLucumaVersion)
addSbtPlugin("edu.gemini" % "sbt-lucuma-sjs-bundler" % sbtLucumaVersion)
