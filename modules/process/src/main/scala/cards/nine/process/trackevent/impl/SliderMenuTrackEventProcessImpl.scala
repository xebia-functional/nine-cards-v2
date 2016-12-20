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
