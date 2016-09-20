package com.fortysevendeg.ninecardslauncher.process.trackevent.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.{Entertainment, GameAdventure, HomeMorningMoment}
import com.fortysevendeg.ninecardslauncher.process.trackevent.models._
import com.fortysevendeg.ninecardslauncher.services.track.TrackEvent

trait TrackEventProcessData {

  val entertainmentPackageName = "package.name.entertainment"
  val entertainmentCategory = AppCategory(Entertainment)

  val gamePackageName = "package.name.game"
  val gameCategory = AppCategory(GameAdventure)

  val momentPackageName = "package.name.moment"
  val momentCategory = MomentCategory(HomeMorningMoment)
  val momentClassName = "class.name.moment"

  val openAppEntertainmentEvent = TrackEvent(
    screen = LauncherScreen.name,
    category = entertainmentCategory.name,
    action = OpenAction.name,
    label = Option(entertainmentPackageName),
    value = Option(OpenAppFromAppDrawerValue.value))

  val openAppGameEvent = TrackEvent(
    screen = LauncherScreen.name,
    category = gameCategory.name,
    action = OpenAction.name,
    label = Option(gamePackageName),
    value = Option(OpenAppFromAppDrawerValue.value))

  val openAppFromCollectionEvent = TrackEvent(
    screen = CollectionDetailScreen.name,
    category = entertainmentCategory.name,
    action = OpenCardAction.name,
    label = Option(entertainmentPackageName),
    value = Option(OpenAppFromCollectionValue.value))

  val addAppEvent = TrackEvent(
    screen = CollectionDetailScreen.name,
    category = entertainmentCategory.name,
    action = AddedToCollectionAction.name,
    label = Option(entertainmentPackageName),
    value = Option(AddedToCollectionValue.value))

  val removeEvent =TrackEvent(
    screen = CollectionDetailScreen.name,
    category = entertainmentCategory.name,
    action = RemovedFromCollectionAction.name,
    label = Option(entertainmentPackageName),
    value = Option(RemovedFromCollectionValue.value))

  val momentEvent = TrackEvent(
    screen = WidgetScreen.name,
    category = s"WIDGET_${momentCategory.name}",
    action = AddedWidgetToMomentAction.name,
    label = Option(s"$momentPackageName:$momentClassName"),
    value = Option(AddedWidgetToMomentValue.value))


}
