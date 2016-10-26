package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait ProfileTrackEventProcessImpl  extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  val profileScreen = ProfileScreen

  override def showAccountsContent() = {
    val event = TrackEvent(
      screen = profileScreen,
      category = AccountCategory,
      action = ShowAccountsContentAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def copyConfiguration() = {
    val event = TrackEvent(
      screen = profileScreen,
      category = AccountCategory,
      action = CopyConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def synchronizeConfiguration() = {
    val event = TrackEvent(
      screen = profileScreen,
      category = AccountCategory,
      action = SynchronizeConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def changeConfigurationName() = {
    val event = TrackEvent(
      screen = profileScreen,
      category = AccountCategory,
      action = ChangeConfigurationNameAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def deleteConfiguration() = {
    val event = TrackEvent(
      screen = profileScreen,
      category = AccountCategory,
      action = DeleteConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
