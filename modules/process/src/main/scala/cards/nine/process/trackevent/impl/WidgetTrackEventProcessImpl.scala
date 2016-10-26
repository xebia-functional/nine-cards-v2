package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types.{AddedWidgetToMomentAction, AddedWidgetToMomentValue, MomentCategory, WidgetScreen}
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait WidgetTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory) = {
    val widgetLabel = s"$packageName:$className"
    val event = TrackEvent(
      screen = WidgetScreen,
      category = moment,
      action = AddedWidgetToMomentAction,
      label = Option(widgetLabel),
      value = Option(AddedWidgetToMomentValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
