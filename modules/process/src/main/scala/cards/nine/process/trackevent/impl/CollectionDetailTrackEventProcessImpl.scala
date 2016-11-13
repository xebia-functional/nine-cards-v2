package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait CollectionDetailTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def useNavigationBar() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = NavigationBarAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def reorderApplication(newPosition: Int) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = ReorderApplicationAction,
      label = Option(newPosition.toString),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def moveApplications(collectionName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = MoveApplicationsAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeApplications(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = RemoveApplicationsAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def closeCollectionByGesture() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = CloseCollectionByGestureAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addShortcutByFab(shortcutName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddShortcutByFabAction,
      label = Option(shortcutName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addRecommendationByFab(recommendationName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddRecommendationByFabAction,
      label = Option(recommendationName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addContactByFab() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddContactByFabAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppsByFab(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddAppsByFabAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeAppsByFab(packageNames: Seq[String]) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = RemoveAppsByFabAction,
      label = Option(packageNames.mkString(",")),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addCardByMenu() = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = AddCardByMenuAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def publishCollectionByMenu(collectionName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = PublishCollectionByMenuAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def shareCollectionByMenu(collectionName: String) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = GestureActionsCategory,
      action = ShareCollectionByMenuAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = OpenCardAction,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = AddedToCollectionAction,
      label = Option(packageName),
      value = Option(AddedToCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = RemovedFromCollectionAction,
      label = Option(packageName),
      value = Option(RemovedFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
