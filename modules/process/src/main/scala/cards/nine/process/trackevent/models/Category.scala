package cards.nine.process.trackevent.models

import cards.nine.process.commons.types.{NineCardCategory, NineCardsMoment}

sealed trait Category {
  def name: String
}

// Special categories for cards

case class AppCategory(nineCardCategory: NineCardCategory) extends Category {
  override def name: String = nineCardCategory.name
}

case class MomentCategory(moment: NineCardsMoment) extends Category {
  override def name: String = moment.name
}

case object FreeCategory extends Category {
  override def name: String = Category.freeName
}

object Category {
  val freeName = "FREE"
}