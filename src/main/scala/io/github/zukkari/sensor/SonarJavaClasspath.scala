package io.github.zukkari.sensor

import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.config.Configuration
import org.sonar.java.JavaClasspath

class SonarJavaClasspath(val config: Configuration, val fileSystem: FileSystem) extends JavaClasspath(config, fileSystem) {

  override def init(): Unit = super.init()

}
