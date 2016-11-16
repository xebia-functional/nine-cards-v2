package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait AppDrawerTrackEventTestData {

  val usingFastScrollerEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = FastScrollerCategory,
    action = UsingFastScrollerAction,
    label = None,
    value = None)

  val goToContactsEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = GestureActionsCategory,
    action = GoToContactsAction,
    label = None,
    value = None)

  val goToAppsEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = GestureActionsCategory,
    action = GoToAppsAction,
    label = None,
    value = None)

  val addAppToCollectionEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = GestureActionsCategory,
    action = AddAppToCollectionAction,
    label = Option(packageName),
    value = None)

  val addContactToCollectionEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = GestureActionsCategory,
    action = AddContactToCollectionAction,
    label = None,
    value = None)

  val goToGooglePlayButtonEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = SearchButtonsCategory,
    action = GoToGooglePlayButtonAction,
    label = None,
    value = None)

  val goToGoogleCallButtonEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = SearchButtonsCategory,
    action = GoToGoogleCallButtonAction,
    label = None,
    value = None)

  val goToFiltersByButtonEvent = TrackEvent(
    screen = AppDrawerScreen,
    category = SearchButtonsCategory,
    action = GoToFiltersByButtonAction,
    label = Option(filterName),
    value = None)

}
