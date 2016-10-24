package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait CollectionDetailTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  val collectionDetailScreen = CollectionDetailScreen

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = collectionDetailScreen,
      category = category,
      action = OpenCardAction,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = collectionDetailScreen,
      category = category,
      action = AddedToCollectionAction,
      label = Option(packageName),
      value = Option(AddedToCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = collectionDetailScreen,
      category = category,
      action = RemovedFromCollectionAction,
      label = Option(packageName),
      value = Option(RemovedFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
