package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types.{LauncherScreen, OpenAction, OpenAppFromAppDrawerValue}

trait LauncherTrackEventTestData {

  val openAppEntertainmentEvent = TrackEvent(
    screen = LauncherScreen,
    category = entertainmentCategory,
    action = OpenAction,
    label = Option(entertainmentPackageName),
    value = Option(OpenAppFromAppDrawerValue))

  val openAppGameEvent = TrackEvent(
    screen = LauncherScreen,
    category = gameCategory,
    action = OpenAction,
    label = Option(gamePackageName),
    value = Option(OpenAppFromAppDrawerValue))

}
