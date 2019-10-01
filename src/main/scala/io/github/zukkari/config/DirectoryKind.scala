package io.github.zukkari.config

sealed trait DirectoryKind {
  val name: String
  val ext: String
}

case object MetadataDirectory extends DirectoryKind {
  override val name: String = "metadata"
  override val ext: String = "json"
}

case object Template extends DirectoryKind {
  override val name: String = "templates"
  override val ext: String = "html"
}
