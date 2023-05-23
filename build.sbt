ThisBuild / version := "1.0-0"
ThisBuild / organization := "com.github.CuriosityWeb"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, JooqCodegenPlugin)
  .settings(
    name := "play-jooq-sample",
    jooqVersion := "3.17.4",
    jooqCodegenConfig := file("conf/db/jooq-codegen.xml"))

libraryDependencies ++= Seq(
  guice,
  "com.google.inject" % "guice" % "5.1.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
  "javax.xml.bind" % "jaxb-api" % "2.3.1")

libraryDependencies ++= Seq(
  "org.jooq" % "jooq-meta-extensions" % jooqVersion.value.toString % JooqCodegen,
  "org.jooq" %% "jooq-scala" % jooqVersion.value.toString)

libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  "org.flywaydb" %% "flyway-play" % "7.24.0",
  "com.h2database" % "h2" % "2.1.214")
