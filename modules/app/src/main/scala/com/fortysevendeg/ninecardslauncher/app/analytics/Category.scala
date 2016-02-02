package com.fortysevendeg.ninecardslauncher.app.analytics

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import Category._

sealed trait Category {
  def name: String
}

// Special categories for cards

case class AppCategory(nineCardCategory: NineCardCategory) extends Category {
  override def name: String = nineCardCategory.name
}

case object MomentCategory extends Category { // TODO Add Moments here
  override def name: String = toString
}

case object FreeCategory extends Category {
  override def name: String = freeName
}

object Category {
  val freeName = "FREE"
}