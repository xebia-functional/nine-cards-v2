package cards.nine.models.types

sealed trait Action {
  def name: String
}

/* AppDrawerScreen */

case object UsingFastScrollerAction extends Action {
  override def name: String = "UsingFastScroller"
}

case object GoToContactsAction extends Action {
  override def name: String = "GoToContacts"
}

case object GoToAppsAction extends Action {
  override def name: String = "GoToApps"
}

case object AddAppToCollectionAction extends Action {
  override def name: String = "AddAppToCollection"
}

case object AddContactToCollectionAction extends Action {
  override def name: String = "AddContactToCollection"
}

case object GoToGooglePlayButtonAction extends Action {
  override def name: String = "GoToGooglePlayButton"
}

case object GoToGoogleCallButtonAction extends Action {
  override def name: String = "GoToGoogleCallButton"
}

case object GoToFiltersByButtonAction extends Action {
  override def name: String = "GoToFiltersByButton"
}

/* CollectionDetailScreen */

case object NavigationBarAction extends Action {
  override def name: String = "NavigationBar"
}

case object ReorderApplicationAction extends Action {
  override def name: String = "ReorderApplication"
}

case object MoveApplicationsAction extends Action {
  override def name: String = "MoveApplications"
}

case object RemoveApplicationsAction extends Action {
  override def name: String = "RemoveApplicationsAction"
}

case object CloseCollectionByGestureAction extends Action {
  override def name: String = "CloseCollectionByGesture"
}

case object AddShortcutByFabAction extends Action {
  override def name: String = "AddShortcutByFab"
}

case object AddRecommendationByFabAction extends Action {
  override def name: String = "AddRecommendationByFab"
}

case object AddContactByFabAction extends Action {
  override def name: String = "AddContactByFab"
}

case object AddAppsByFabAction extends Action {
  override def name: String = "AddAppsByFab"
}

case object RemoveAppsByFabAction extends Action {
  override def name: String = "RemoveAppsByFabAction"
}

case object AddCardByMenuAction extends Action {
  override def name: String = "AddCardByMenu"
}

case object PublishCollectionByMenuAction extends Action {
  override def name: String = "PublishCollectionByMenu"
}

case object ShareCollectionAfterPublishingAction extends Action {
  override def name: String = "ShareCollectionAfterPublishing"
}

case object ShareCollectionByMenuAction extends Action {
  override def name: String = "ShareCollectionByMenu"
}

case object OpenCardAction extends Action {
  override def name: String = "OpenCard"
}

case object AddedToCollectionAction extends Action {
  override def name: String = "AddedToCollection"
}

case object RemovedFromCollectionAction extends Action {
  override def name: String = "RemovedFromCollection"
}

/* LauncherScreen */

case object OpenAction extends Action {
  override def name: String = "Open"
}

/* ProfileScreen */

case object AddToMyCollectionsFromProfileAction extends Action {
  override def name: String = "AddToMyCollectionsFromProfile"
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

/* WidgetScreen */

case object AddedWidgetToMomentAction extends Action {
  override def name: String = "AddedWidgetToMoment"
}

/* WizardScreen */

case object ChooseAccountAction extends Action {
  override def name: String = "ChooseAccount"
}

case object ChooseNewConfigurationAction extends Action {
  override def name: String = "ChooseNewConfiguration"
}

case object ChooseExistingDeviceAction extends Action {
  override def name: String = "ChooseExistingDevice"
}

case object ChooseMomentAction extends Action {
  override def name: String = "ChooseMoment"
}

case object ChooseMomentWifiAction extends Action {
  override def name: String = "ChooseMomentWifi"
}

case object ChooseOtherMomentAction extends Action {
  override def name: String = "ChooseOtherMoment"
}




