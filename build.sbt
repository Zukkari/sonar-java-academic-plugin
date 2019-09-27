import sbt._
import org.sonar.updatecenter.common.PluginManifest

name := "sonar-android"
organization := "io.github.zukkari"
homepage := Some(url("https://github.com/Zukkari/sonar-android-plugin"))
description := "Implementation of Android code smells for Sonar"

version := "0.1"

scalaVersion := "2.13.0"
scalacOptions := Seq(
  "-Xfatal-warnings"
)


// Dependencies
val sonarVersion = "7.9"
libraryDependencies ++= List(
  "org.sonarsource.sonarqube" % "sonar-plugin-api" % sonarVersion % Provided,
  "org.slf4j" % "slf4j-api" % "1.7.28" % Provided,
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.scalatest" %% "scalatest" % "3.2.0-M1" % Test,
)

// Manifest attributes
packageOptions in(Compile, packageBin) += Package.ManifestAttributes(
  PluginManifest.KEY -> "sonar-android-plugin",
  PluginManifest.NAME -> "Sonar Android Plugin",
  PluginManifest.DESCRIPTION -> description.value,
  PluginManifest.HOMEPAGE -> "https://github.com/Zukkari/sonar-android-plugin",
  PluginManifest.SOURCES_URL -> "https://github.com/Zukkari/sonar-android-plugin",
  PluginManifest.ISSUE_TRACKER_URL -> "https://github.com/Zukkari/sonar-android-plugin/issues",
  PluginManifest.ORGANIZATION -> "Stanislav Mõškovski",
  PluginManifest.ORGANIZATION_URL -> "https://github.com/Zukkari",
  PluginManifest.DEVELOPERS -> "Stanislav Mõškovski",
  PluginManifest.VERSION -> version.value,
  PluginManifest.DISPLAY_VERSION -> version.value,
  PluginManifest.SONAR_VERSION -> sonarVersion,
  PluginManifest.LICENSE -> "GNU LGPL 3",
  PluginManifest.SONARLINT_SUPPORTED -> "false",
  PluginManifest.MAIN_CLASS -> "io.github.zukkari.SonarAndroidPlugin",
  PluginManifest.USE_CHILD_FIRST_CLASSLOADER -> "false"
)


// Assembly
test in assembly := {}
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
assemblyMergeStrategy in assembly := {
  case "log4j.properties" => MergeStrategy.first
  case "reference.conf" => MergeStrategy.concat
  case "application.conf" => MergeStrategy.concat
  case PathList("META-INF", xs@_*) =>
    xs match {
      case ("MANIFEST.MF" :: Nil) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}
artifact in(Compile, assembly) := {
  val art = (artifact in(Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}
addArtifact(artifact in(Compile, assembly), assembly)
