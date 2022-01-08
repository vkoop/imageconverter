name:="converter"

version:="0.0.1"

scalaVersion:="3.1.0"

mainClass := Some("converter.App")

exportJars := true

libraryDependencies += "org.imgscalr" % "imgscalr-lib" % "4.2"

libraryDependencies +=
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
