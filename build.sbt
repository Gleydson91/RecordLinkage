
name := """RecordLinkage"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

//addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "1.0.0")

libraryDependencies ++= Seq(
  "no.priv.garshol.duke" % "duke" % "1.2",
  "org.webjars" % "bootstrap" % "3.3.4",
  javaJdbc,
  cache,
  javaWs
)
