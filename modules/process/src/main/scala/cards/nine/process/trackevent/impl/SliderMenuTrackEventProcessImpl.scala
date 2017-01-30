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

trait SliderMenuTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def goToCollectionsByMenu() = {
    val event = TrackEvent(
      screen = SliderMenuScreen,
      category = SliderOptionCategory,
      action = GoToCollectionsByMenuAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToMomentsByMenu() = {
    val event = TrackEvent(
      screen = SliderMenuScreen,
      category = SliderOptionCategory,
      action = GoToMomentsByMenuAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToProfileByMenu() = {
    val event = TrackEvent(
      screen = SliderMenuScreen,
      category = SliderOptionCategory,
      action = GoToProfileByMenuAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToSendUsFeedback() = {
    val event = TrackEvent(
      screen = SliderMenuScreen,
      category = SliderOptionCategory,
      action = GoToSendUsFeedbackAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToHelpByMenu() = {
    val event = TrackEvent(
      screen = SliderMenuScreen,
      category = SliderOptionCategory,
      action = GoToHelpAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
