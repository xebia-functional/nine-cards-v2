package cards.nine.commons.test.data.trackevent

import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait SliderMenuTrackEventTestData {
  
  val goToCollectionsByMenuEvent = TrackEvent(
    screen = SliderMenuScreen,
    category = SliderOptionCategory,
    action = GoToCollectionsByMenuAction,
    label = None,
    value = None)

  val goToMomentsByMenuEvent = TrackEvent(
    screen = SliderMenuScreen,
    category = SliderOptionCategory,
    action = GoToMomentsByMenuAction,
    label = None,
    value = None)

  val goToProfileByMenuEvent = TrackEvent(
    screen = SliderMenuScreen,
    category = SliderOptionCategory,
    action = GoToProfileByMenuAction,
    label = None,
    value = None)

  val goToSendUsFeedbackEvent = TrackEvent(
    screen = SliderMenuScreen,
    category = SliderOptionCategory,
    action = GoToSendUsFeedbackAction,
    label = None,
    value = None)

  val goToHelpByMenuEvent = TrackEvent(
    screen = SliderMenuScreen,
    category = SliderOptionCategory,
    action = GoToHelpAction,
    label = None,
    value = None)

}
