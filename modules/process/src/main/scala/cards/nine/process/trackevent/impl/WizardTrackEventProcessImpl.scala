package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait WizardTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def chooseAccount() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardStartCategory,
      action = ChooseAccountAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseNewConfiguration() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseNewConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseCurrentDevice() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseCurrentDeviceAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseOtherDevices() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseOtherDevicesAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseAllApps() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardCollectionsCategory,
      action = ChooseAllAppsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseBestNineApps() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardCollectionsCategory,
      action = ChooseBestNineAppsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseHome() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseHomeAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseHomeWifi() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseHomeWifiAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseWork() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseWorkAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseWorkWifi() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseWorkWifiAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseStudy() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseStudyAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseStudyWifi() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseStudyWifiAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseMusic() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardOtherMomentsCategory,
      action = ChooseMusicAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseCar() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardOtherMomentsCategory,
      action = ChooseCarAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseSport() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardOtherMomentsCategory,
      action = ChooseSportAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
