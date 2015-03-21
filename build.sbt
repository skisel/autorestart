import sbt.Keys._
import sbt._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import NativePackagerHelper._


name := "autorestart"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT",
  "com.typesafe.akka" %% "akka-kernel" % "2.4-SNAPSHOT"
)

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.11.5")

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

enablePlugins(JavaServerAppPackaging)

mainClass in Compile := Some("com.skisel.Main")

