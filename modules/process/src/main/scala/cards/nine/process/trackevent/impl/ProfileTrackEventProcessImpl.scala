package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{
  ImplicitsTrackEventException,
  TrackEventException,
  TrackEventProcess
}

trait ProfileTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def logout() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = LogoutAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def showAccountsContent() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = ShowAccountsContentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def copyConfiguration() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = CopyConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def synchronizeConfiguration() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = SynchronizeConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def changeConfigurationName() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = ChangeConfigurationNameAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def deleteConfiguration() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = AccountCategory,
      action = DeleteConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def showPublicationsContent() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = PublicationCategory,
      action = ShowPublicationsContentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addToMyCollectionsFromProfile(collectionName: String) = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = PublicationCategory,
      action = AddToMyCollectionsFromProfileAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def shareCollectionFromProfile(collectionName: String) = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = PublicationCategory,
      action = ShareCollectionFromProfileAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def showSubscriptionsContent() = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = SubscriptionCategory,
      action = ShowSubscriptionsContentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def subscribeToCollection(sharedCollectionId: String) = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = SubscriptionCategory,
      action = SubscribeToCollectionAction,
      label = Option(sharedCollectionId),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def unsubscribeFromCollection(sharedCollectionId: String) = {
    val event = TrackEvent(
      screen = ProfileScreen,
      category = SubscriptionCategory,
      action = UnsubscribeFromCollectionAction,
      label = Option(sharedCollectionId),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
