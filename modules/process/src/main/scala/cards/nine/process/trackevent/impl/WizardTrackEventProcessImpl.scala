/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{
  ImplicitsTrackEventException,
  TrackEventException,
  TrackEventProcess
}

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

  override def chooseExistingDevice() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseExistingDeviceAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseMoment(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseMomentAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseMomentWifi(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseMomentWifiAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseOtherMoment(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardOtherMomentsCategory,
      action = ChooseOtherMomentAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
