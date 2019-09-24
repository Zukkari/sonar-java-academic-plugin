scalaVersion := "2.12.8"

resolvers += Resolver.bintrayRepo("eed3si9n", "sbt-plugins")

libraryDependencies ++= Seq(
  "org.sonarsource.update-center" % "sonar-update-center-common" % "1.22.0.644"
)
