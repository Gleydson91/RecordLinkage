
name := """RecordLinkage"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  "no.priv.garshol.duke" % "duke" % "1.2",
  "no.priv.garshol.duke" % "duke" % "1.2",
  "org.json" % "json" % "20160212",
  "commons-codec" % "commons-codec" % "1.10",
  javaJdbc,
  cache,
  javaWs
)
