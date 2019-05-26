import sbt.Keys._
import sbt._
import sbtrelease.Version
import StoreOauthCredential._

name := "calendar_sync"
scalaVersion := "2.12.8"
isSnapshot := true


resolvers += Resolver.sonatypeRepo("public")
releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }

assemblyMergeStrategy in assembly := {
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "mailcap.default") => MergeStrategy.discard
  case PathList("META-INF", "mimetypes.default") => MergeStrategy.discard
  case _  => MergeStrategy.first
}
assemblyJarName in assembly := "calendar_sync.jar"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events" % "2.2.1",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.8" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test
)

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.534"

libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.23.0",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev305-1.23.0"
)

libraryDependencies += "com.google.inject" % "guice" % "4.0"

//libraryDependencies ++=Seq(
//  "org.scalactic" %% "scalactic" % "3.0.5",
//  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
//)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings")


clientSecretsJson := (resourceDirectory in Compile).value / "token" / "client_secrets.json"
scopes := Seq("https://www.googleapis.com/auth/calendar")
