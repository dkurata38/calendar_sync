import sbt.Keys._
import sbt._
import sbtrelease.Version

name := "calendar_sync"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.12.6"
releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }
assemblyJarName in assembly := "calendar_sync.jar"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.10.2"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events" % "2.2.1",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"
)

libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.23.0",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev305-1.23.0"
)

libraryDependencies += "com.google.inject" % "guice" % "4.0"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings")
