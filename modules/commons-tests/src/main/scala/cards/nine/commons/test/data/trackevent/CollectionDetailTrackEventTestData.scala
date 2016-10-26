package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait CollectionDetailTrackEventTestData {

  val openAppFromCollectionEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = OpenCardAction,
    label = Option(entertainmentPackageName),
    value = Option(OpenAppFromCollectionValue))

  val addAppEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = AddedToCollectionAction,
    label = Option(entertainmentPackageName),
    value = Option(AddedToCollectionValue))

  val removeEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = RemovedFromCollectionAction,
    label = Option(entertainmentPackageName),
    value = Option(RemovedFromCollectionValue))

}
