name := """bloob-backend"""
organization := "org.vivaconagua"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies += "org.vivaconagua" %% "play2-oauth-client" % "0.4.3-play27"
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.vivaconagua.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.vivaconagua.binders._"
