version:="0.0.1"
scalaVersion:="3.1.1"
libraryDependencies += "org.imgscalr" % "imgscalr-lib" % "4.2"
libraryDependencies +=
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"

lazy val nativeProject = project.enablePlugins(NativeImagePlugin)
  .settings(
    Compile / mainClass :=  Some("de.vkoop.imageconverter.MyApp")
  )
