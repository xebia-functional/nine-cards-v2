package cards.nine.models.types

sealed trait TypeSharedCollection {
  val name: String
}

case object TopSharedCollection extends TypeSharedCollection {
  override val name: String = "top"
}

case object LatestSharedCollection extends TypeSharedCollection {
  override val name: String = "latest"
}

object TypeSharedCollection {

  val cases = Seq(TopSharedCollection, LatestSharedCollection)

  def apply(name: String): TypeSharedCollection =
    cases find (_.name == name) getOrElse
      (throw new IllegalArgumentException(s"$name not found"))

}
