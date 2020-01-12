import sbt._
import org.sonar.updatecenter.common.PluginManifest

name := "sonar-java-academic-plugin"
organization := "io.github.zukkari"
homepage := Some(url("https://github.com/Zukkari/sonar-java-academic-plugin"))
description := "Implementation of Java academic code smells for Sonar"

version := "0.1"

scalaVersion := "2.13.0"
scalacOptions := Seq(
  "-encoding", "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-language:higherKinds",
)

// Dependencies
val sonarVersion = "7.9"
libraryDependencies ++= List(
  "org.scala-lang" % "scala-library" % "2.13.0",
  "org.sonarsource.sonarqube" % "sonar-plugin-api" % sonarVersion % Provided,
  "org.slf4j" % "slf4j-api" % "1.7.28" % Provided,
  "org.sonarsource.java" % "sonar-java-plugin" % "5.14.0.18788",
  "org.sonarsource.sslr" % "sslr-core" % "1.23",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "io.circe" %% "circe-parser" % "0.12.1",
  "io.circe" %% "circe-core" % "0.12.1",
  "org.scala-graph" %% "graph-core" % "1.13.1",
  "org.scalatest" %% "scalatest" % "3.2.0-M1" % Test,
  "org.mockito" %% "mockito-scala" % "1.5.17" % Test,
  "org.scalatestplus" %% "scalatestplus-mockito" % "1.0.0-M2" % Test,
  "org.sonarsource.java" % "java-checks-testkit" % "5.14.0.18788" % Test,
)

// Manifest attributes
packageOptions in(Compile, packageBin) += Package.ManifestAttributes(
  PluginManifest.KEY -> "sonar-java-academic-plugin",
  PluginManifest.NAME -> "Sonar Java academic plugin",
  PluginManifest.DESCRIPTION -> description.value,
  PluginManifest.HOMEPAGE -> "https://github.com/Zukkari/sonar-java-academic-plugin",
  PluginManifest.SOURCES_URL -> "https://github.com/Zukkari/sonar-java-academic-plugin",
  PluginManifest.ISSUE_TRACKER_URL -> "https://github.com/Zukkari/sonar-java-academic-plugin/issues",
  PluginManifest.ORGANIZATION -> "Stanislav Mõškovski",
  PluginManifest.ORGANIZATION_URL -> "https://github.com/Zukkari",
  PluginManifest.DEVELOPERS -> "Stanislav Mõškovski",
  PluginManifest.VERSION -> version.value,
  PluginManifest.DISPLAY_VERSION -> version.value,
  PluginManifest.SONAR_VERSION -> sonarVersion,
  PluginManifest.LICENSE -> "GNU LGPL 3",
  PluginManifest.SONARLINT_SUPPORTED -> "false",
  PluginManifest.MAIN_CLASS -> "io.github.zukkari.SonarJavaAcademicPlugin",
  PluginManifest.USE_CHILD_FIRST_CLASSLOADER -> "false"
)

def isSignatureFile(f: String): Boolean = {
  f.endsWith("DSA") ||
    f.endsWith("SF") ||
    f.endsWith("RSA")
}

// Assembly
test in assembly := {}
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
assemblyMergeStrategy in assembly := {
  case "log4j.properties" => MergeStrategy.first
  case "reference.conf" => MergeStrategy.concat
  case "application.conf" => MergeStrategy.concat
  case signed if isSignatureFile(signed)=> MergeStrategy.discard
  case PathList("META-INF", xs@_*) =>
    xs match {
      case "MANIFEST.MF" :: Nil => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}
artifact in(Compile, assembly) := {
  val art = (artifact in(Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}
addArtifact(artifact in(Compile, assembly), assembly)
