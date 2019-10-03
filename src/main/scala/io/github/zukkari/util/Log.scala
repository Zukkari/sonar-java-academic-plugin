package io.github.zukkari.util

import cats.effect.IO
import org.sonar.api.utils.log.Loggers

trait Logger {
  def debug(fn: () => String): Unit

  def warn(fn: () => String): Unit

  def error(fn: () => String): Unit

  def info(fn: () => String): Unit
}

object Log {
  def apply(c: Class[_]): Logger = new Logger {
    private val log = Loggers.get(c)

    override def debug(fn: () => String): Unit = wrap(fn, log.debug).unsafeRunAsyncAndForget()

    override def warn(fn: () => String): Unit = wrap(fn, log.warn).unsafeRunAsyncAndForget()

    override def error(fn: () => String): Unit = wrap(fn, log.error).unsafeRunAsyncAndForget()

    override def info(fn: () => String): Unit = wrap(fn, log.info).unsafeRunAsyncAndForget()

    def wrap(fn: () => String, sideEffect: String => Unit): IO[Unit] = IO(sideEffect(fn()))
  }
}
