package io.github.zukkari.sensor

import io.github.zukkari.sonar.java.JavaClasspath
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.config.Configuration

class SonarJavaClasspath(val config: Configuration, val fileSystem: FileSystem)
    extends JavaClasspath(config, fileSystem) {

  override def init(): Unit = super.init()

}
