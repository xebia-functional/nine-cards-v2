package cards.nine.process.trackevent.impl

import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait TrackEventProcessData {

  val entertainmentPackageName = "package.name.entertainment"
  val entertainmentCategory = AppCategory(Entertainment)

  val gamePackageName = "package.name.game"
  val gameCategory = AppCategory(GameAdventure)

  val momentPackageName = "package.name.moment"
  val momentCategory = MomentCategory(HomeMorningMoment)
  val momentClassName = "class.name.moment"

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

  val openAppFromCollectionEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = OpenCardAction,
    label = Option(entertainmentPackageName),
    value = Option(OpenAppFromCollectionValue))

  val addAppEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = AddedToCollectionAction,
    label = Option(entertainmentPackageName),
    value = Option(AddedToCollectionValue))

  val removeEvent = TrackEvent(
    screen = CollectionDetailScreen,
    category = entertainmentCategory,
    action = RemovedFromCollectionAction,
    label = Option(entertainmentPackageName),
    value = Option(RemovedFromCollectionValue))

  val momentEvent = TrackEvent(
    screen = WidgetScreen,
    category = momentCategory,
    action = AddedWidgetToMomentAction,
    label = Option(s"$momentPackageName:$momentClassName"),
    value = Option(AddedWidgetToMomentValue))


}
