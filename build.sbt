name := """bloob-backend"""
organization := "org.vivaconagua"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

//resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
//resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
resolvers ++= Seq(
  Resolver.sonatypeRepo("public"),
  Resolver.bintrayRepo("scalaz", "releases")
)

libraryDependencies += ehcache
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "org.vivaconagua" %% "play2-oauth-client" % "0.4.7-play27"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.8.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.vivaconagua.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.vivaconagua.binders._"

//for db
libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.h2database" % "h2" % "1.4.199",
  "mysql" % "mysql-connector-java" % "8.0.15",
)
