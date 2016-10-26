package cards.nine.models.types

sealed trait Action {
  def name: String
}

case object AddedToCollectionAction extends Action {
  override def name: String = "AddedToCollection"
}

case object AddToMyCollectionsFromProfileAction extends Action {
  override def name: String = "AddToMyCollectionsFromProfile"
}

case object AddedWidgetToMomentAction extends Action {
  override def name: String = "AddedWidgetToMoment"
}

case object ChangeConfigurationNameAction extends Action {
  override def name: String = "ChangeConfigurationName"
}

case object CopyConfigurationAction extends Action {
  override def name: String = "CopyConfiguration"
}

case object DeleteConfigurationAction extends Action {
  override def name: String = "DeleteConfiguration"
}

case object LogoutAction extends Action {
  override def name: String = "Logout"
}

case object OpenAction extends Action {
  override def name: String = "Open"
}

case object OpenCardAction extends Action {
  override def name: String = "OpenCard"
}

case object RemovedFromCollectionAction extends Action {
  override def name: String = "RemovedFromCollection"
}

case object ShareCollectionFromProfileAction extends Action {
  override def name: String = "ShareCollectionFromProfile"
}

case object ShowAccountsContentAction extends Action {
  override def name: String = "ShowAccountsContent"
}

case object ShowPublicationsContentAction extends Action {
  override def name: String = "ShowPublicationsContent"
}

case object ShowSubscriptionsContentAction extends Action {
  override def name: String = "ShowSubscriptionsContent"
}

case object SubscribeToCollectionAction extends Action {
  override def name: String = "SubscribeToCollection"
}

case object SynchronizeConfigurationAction extends Action {
  override def name: String = "SynchronizeConfiguration"
}

case object UnsubscribeFromCollectionAction extends Action {
  override def name: String = "UnsubscribeFromCollection"
}





