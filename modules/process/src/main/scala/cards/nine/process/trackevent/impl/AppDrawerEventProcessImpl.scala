package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{
  ImplicitsTrackEventException,
  TrackEventException,
  TrackEventProcess
}

trait AppDrawerEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def usingFastScroller() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = FastScrollerCategory,
      action = UsingFastScrollerAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToContacts() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = GestureActionsCategory,
      action = GoToContactsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToApps() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = GestureActionsCategory,
      action = GoToAppsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String) = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = GestureActionsCategory,
      action = AddAppToCollectionAction,
      label = Option(packageName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addContactToCollection() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = GestureActionsCategory,
      action = AddContactToCollectionAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToGooglePlayButton() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = SearchButtonsCategory,
      action = GoToGooglePlayButtonAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToGoogleCallButton() = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = SearchButtonsCategory,
      action = GoToGoogleCallButtonAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToFiltersByButton(filterName: String) = {
    val event = TrackEvent(
      screen = AppDrawerScreen,
      category = SearchButtonsCategory,
      action = GoToFiltersByButtonAction,
      label = Option(filterName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
