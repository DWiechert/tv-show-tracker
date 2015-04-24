name := "tv-show-tracker"

version := "0.1"

scalaVersion := "2.11.2"

EclipseKeys.withSource := true

libraryDependencies ++= Seq(
  "com.typesafe.slick" % "slick_2.11" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
)