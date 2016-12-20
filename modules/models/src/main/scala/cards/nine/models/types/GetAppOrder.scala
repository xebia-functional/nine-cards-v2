package cards.nine.models.types

sealed trait GetAppOrder {
  val ascending: Boolean
  val name: String
}

case class GetByName(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY NAME"
}

object GetByName extends GetByName(true)

case class GetByInstallDate(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY INSTALL DATE"
}

object GetByInstallDate extends GetByInstallDate(false)

case class GetByCategory(ascending: Boolean) extends GetAppOrder {
  override val name: String = "GET BY CATEGORY"
}

object GetByCategory extends GetByCategory(true)
