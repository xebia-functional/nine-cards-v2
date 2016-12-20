package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types.{AddedWidgetToMomentAction, AddedWidgetToMomentValue, WidgetScreen}

trait WidgetTrackEventTestData {

  val momentEvent = TrackEvent(
    screen = WidgetScreen,
    category = momentCategory,
    action = AddedWidgetToMomentAction,
    label = Option(s"$momentPackageName:$momentClassName"),
    value = Option(AddedWidgetToMomentValue))

}
