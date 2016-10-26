package cards.nine.models.types

sealed trait Category {
  def name: String
}

case object AccountCategory extends Category {
  override def name: String = "ACCOUNT"
}

case class AppCategory(nineCardCategory: NineCardsCategory) extends Category {
  override def name: String = nineCardCategory.name
}

case object FreeCategory extends Category {
  override def name: String = "FREE"
}

case class MomentCategory(moment: NineCardsMoment) extends Category {
  override def name: String = moment.name
}

case object PublicationCategory extends Category {
  override def name: String = "PUBLICATION"
}

case object SubscriptionCategory extends Category {
  override def name: String = "SUBSCRIPTION"
}