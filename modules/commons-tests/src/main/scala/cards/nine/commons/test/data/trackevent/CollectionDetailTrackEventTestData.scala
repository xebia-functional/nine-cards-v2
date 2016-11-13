package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait CollectionDetailTrackEventTestData {

  val useNavigationBarEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = NavigationBarAction,
    label = None,
    value = None)

  val reorderApplicationEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = ReorderApplicationAction,
    label = Option(newPosition.toString),
    value = None)

  val moveApplicationsEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = MoveApplicationsAction,
    label = Option(collectionName),
    value = None)

  val removeApplicationsEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = RemoveApplicationsAction,
    label = Option(packageNameSeqStr),
    value = None)

  val closeCollectionByGestureEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = CloseCollectionByGestureAction,
    label = None,
    value = None)

  val addShortcutByFabEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = AddShortcutByFabAction,
    label = Option(shortcutName),
    value = None)

  val addRecommendationByFabEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = AddRecommendationByFabAction,
    label = Option(packageName),
    value = None)

  val addContactByFabEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = AddContactByFabAction,
    label = None,
    value = None)

  val addAppsByFabEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = AddAppsByFabAction,
    label = Option(packageNameSeqStr),
    value = None)

  val removeAppsByFabEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = RemoveAppsByFabAction,
    label = Option(packageNameSeqStr),
    value = None)

  val addCardByMenuEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = AddCardByMenuAction,
    label = None,
    value = None)

  val publishCollectionByMenuEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = PublishCollectionByMenuAction,
    label = Option(collectionName),
    value = None)

  val shareCollectionAfterPublishingEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = ShareCollectionAfterPublishingAction,
    label = Option(sharedCollectionId),
    value = None)

  val shareCollectionByMenuEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = GestureActionsCategory,
    action = ShareCollectionByMenuAction,
    label = Option(sharedCollectionId),
    value = None)

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
