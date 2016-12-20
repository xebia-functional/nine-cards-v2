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

case object EditMomentCategory extends Category {
  override def name: String = "EDIT MOMENT"
}

case object FastScrollerCategory extends Category {
  override def name: String = "FAST SCROLLER"
}

case object FreeCategory extends Category {
  override def name: String = "FREE"
}

case object GestureActionsCategory extends Category {
  override def name: String = "GESTURE ACTIONS"
}

case object LinksReceivedCategory extends Category {
  override def name: String = "LINKS RECEIVED"
}

case object IconBarCategory extends Category {
  override def name: String = "ICON BAR"
}

case class MomentCategory(moment: NineCardsMoment) extends Category {
  override def name: String = moment.name
}

case object MomentsMenuCategory extends Category {
  override def name: String = "MOMENTS MENU"
}

case object PublicationCategory extends Category {
  override def name: String = "PUBLICATION"
}

case object SearchButtonsCategory extends Category {
  override def name: String = "SEARCH BUTTONS"
}

case object SliderOptionCategory extends Category {
  override def name: String = "SLIDER OPTION"
}

case object SubscriptionCategory extends Category {
  override def name: String = "SUBSCRIPTION"
}

case object TopBarCategory extends Category {
  override def name: String = "TOP BAR"
}

case object WizardStartCategory extends Category {
  override def name: String = "WIZARD START"
}

case object WizardConfigurationCategory extends Category {
  override def name: String = "WIZARD CONFIGURATION"
}

case object WizardCollectionsCategory extends Category {
  override def name: String = "WIZARD COLLECTIONS"
}

case object WizardMomentsWifiCategory extends Category {
  override def name: String = "WIZARD MOMENTS WIFI"
}

case object WizardOtherMomentsCategory extends Category {
  override def name: String = "WIZARD OTHER MOMENTS"
}

case object WorkSpaceCategory extends Category {
  override def name: String = "WORKSPACE"
}

case object WorkSpaceActionsCategory extends Category {
  override def name: String = "WORKSPACE ACTIONS"
}

case object WorkSpaceBottomActionsCategory extends Category {
  override def name: String = "WORKSPACE BOTTOM ACTIONS"
}

case object WorkSpaceDragAndDropCategory extends Category {
  override def name: String = "WORKSPACE DRAG AND DROP"
}

case object WorkSpaceGestureActionsCategory extends Category {
  override def name: String = "WORKSPACE GESTURE ACTIONS"
}

case object WorkSpaceLinkReceived extends Category {
  override def name: String = "WORKSPACE LINK RECEIVED"
}
