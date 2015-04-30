name := "tv-show-tracker"

version := "0.1"

scalaVersion := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

EclipseKeys.withSource := true

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
	"com.typesafe.slick" % "slick_2.11" % "2.1.0",
	"org.slf4j" % "slf4j-nop" % "1.6.4",
	"ch.qos.logback" % "logback-classic" % "1.1.3",
	"org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
	"io.spray"            %%  "spray-can"     % sprayV,
	"io.spray"            %%  "spray-routing" % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV
  )
}

Revolver.settings

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

TwirlKeys.templateImports += "com.github.dwiechert.tvtracker.{ Show, Season }"