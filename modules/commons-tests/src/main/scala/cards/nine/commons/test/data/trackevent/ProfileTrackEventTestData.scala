package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait ProfileTrackEventTestData {

  val logoutEvent = TrackEvent(
    screen = ProfileScreen,
    category = AccountCategory,
    action = LogoutAction,
    label = None,
    value = None)

  val showAccountsContentEvent = TrackEvent(
    screen = ProfileScreen,
    category = AccountCategory,
    action = ShowAccountsContentAction,
    label = None,
    value = None)

  val copyConfigurationEvent = TrackEvent(
    screen = ProfileScreen,
    category = AccountCategory,
    action = CopyConfigurationAction,
    label = None,
    value = None)

  val synchronizeConfigurationEvent = TrackEvent(
    screen = ProfileScreen,
    category = AccountCategory,
    action = SynchronizeConfigurationAction,
    label = None,
    value = None)

  val deleteConfigurationEvent = TrackEvent(
    screen = ProfileScreen,
    category = AccountCategory,
    action = DeleteConfigurationAction,
    label = None,
    value = None)

  val showPublicationsContentEvent = TrackEvent(
    screen = ProfileScreen,
    category = PublicationCategory,
    action = ShowPublicationsContentAction,
    label = None,
    value = None)

  val addToMyCollectionsFromProfileEvent = TrackEvent(
    screen = ProfileScreen,
    category = PublicationCategory,
    action = AddToMyCollectionsFromProfileAction,
    label = Option(publication),
    value = None)

  val shareCollectionFromProfileEvent = TrackEvent(
    screen = ProfileScreen,
    category = PublicationCategory,
    action = ShareCollectionFromProfileAction,
    label = Option(publication),
    value = None)

  val showSubscriptionsContentEvent = TrackEvent(
    screen = ProfileScreen,
    category = SubscriptionCategory,
    action = ShowSubscriptionsContentAction,
    label = None,
    value = None)

  val subscribeToCollectionEvent = TrackEvent(
    screen = ProfileScreen,
    category = SubscriptionCategory,
    action = SubscribeToCollectionAction,
    label = Option(sharedCollectionId),
    value = None)

  val unsubscribeFromCollectionEvent = TrackEvent(
    screen = ProfileScreen,
    category = SubscriptionCategory,
    action = UnsubscribeFromCollectionAction,
    label = Option(sharedCollectionId),
    value = None)

}
