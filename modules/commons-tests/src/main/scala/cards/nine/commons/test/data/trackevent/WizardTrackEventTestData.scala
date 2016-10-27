package cards.nine.commons.test.data.trackevent

import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait WizardTrackEventTestData {

  val chooseAccountEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardStartCategory,
    action = ChooseAccountAction,
    label = None,
    value = None)

  val chooseNewConfigurationEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardConfigurationCategory,
    action = ChooseNewConfigurationAction,
    label = None,
    value = None)

  val chooseCurrentDeviceEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardConfigurationCategory,
    action = ChooseCurrentDeviceAction,
    label = None,
    value = None)

  val chooseOtherDevicesEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardConfigurationCategory,
    action = ChooseOtherDevicesAction,
    label = None,
    value = None)

  val chooseAllAppsEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardCollectionsCategory,
    action = ChooseAllAppsAction,
    label = None,
    value = None)

  val chooseBestNineAppsEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardCollectionsCategory,
    action = ChooseBestNineAppsAction,
    label = None,
    value = None)

  val chooseHomeEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseHomeAction,
    label = None,
    value = None)

  val chooseHomeWifiEvent = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseHomeWifiAction,
      label = None,
      value = None)

  val chooseWorkEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseWorkAction,
    label = None,
    value = None)

  val chooseWorkWifiEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseWorkWifiAction,
    label = None,
    value = None)

  val chooseStudyEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseStudyAction,
    label = None,
    value = None)

  val chooseStudyWifiEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseStudyWifiAction,
    label = None,
    value = None)

  val chooseMusicEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardOtherMomentsCategory,
    action = ChooseMusicAction,
    label = None,
    value = None)

  val chooseCarEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardOtherMomentsCategory,
    action = ChooseCarAction,
    label = None,
    value = None)

  val chooseSportEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardOtherMomentsCategory,
    action = ChooseSportAction,
    label = None,
    value = None)

}
