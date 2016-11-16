package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait MomentsTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def goToApplicationByMoment(momentName: String) = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = IconBarCategory,
      action = GoToApplicationByMomentAction,
      label = Option(momentName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def editMoment(momentName: String) = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = WorkSpaceActionsCategory,
      action = EditMomentAction,
      label = Option(momentName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def changeMoment(momentName: String) = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = WorkSpaceActionsCategory,
      action = ChangeMomentAction,
      label = Option(momentName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addMoment(momentName: String) = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = WorkSpaceActionsCategory,
      action = AddMomentAction,
      label = Option(momentName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addWidget(widgetName: String) = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = WorkSpaceActionsCategory,
      action = AddWidgetAction,
      label = Option(widgetName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def unpinMoment() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = TopBarCategory,
      action = UnpinMomentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToWeather() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = TopBarCategory,
      action = GoToWeatherAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def quickAccessToCollection() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = EditMomentCategory,
      action = QuickAccessToCollectionAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def setHours() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = EditMomentCategory,
      action = SetHoursAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def setWifi() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = EditMomentCategory,
      action = SetWifiAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def deleteMoment() = {
    val event = TrackEvent(
      screen = MomentsScreen,
      category = MomentsMenuCategory,
      action = DeleteMomentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
