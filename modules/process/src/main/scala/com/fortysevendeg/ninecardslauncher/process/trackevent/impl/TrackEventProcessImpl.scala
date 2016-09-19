package com.fortysevendeg.ninecardslauncher.process.trackevent.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.Game
import com.fortysevendeg.ninecardslauncher.process.trackevent._
import monix.eval.Task
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.process.trackevent.models._
import com.fortysevendeg.ninecardslauncher.services.track.{TrackEvent, TrackServices}

class TrackEventProcessImpl(trackServices: TrackServices)
  extends TrackEventProcess
  with ImplicitsTrackEventException {

  private[this] val startNameGame = "GAME_"

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = LauncherScreen.name,
      category = category.name,
      action = OpenAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromAppDrawerValue.value))

    def eventForGames(isGame: Boolean): TaskService[Unit] = if (isGame) {
      trackServices.trackEvent(event.copy(category = Game.name)).resolve[TrackEventException]
    } else {
      TaskService(Task(Right(())))
    }

    (trackServices.trackEvent(event) *> eventForGames(category.name.startsWith(startNameGame))).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = OpenCardAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue.value))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = AddedToCollectionAction.name,
      label = Option(packageName),
      value = Option(AddedToCollectionValue.value))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = RemovedFromCollectionAction.name,
      label = Option(packageName),
      value = Option(RemovedFromCollectionValue.value))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory) = {
    val widgetLabel = s"$packageName:$className"
    val widgetCategory = s"WIDGET_${moment.name}"
    val event = TrackEvent(
      screen = WidgetScreen.name,
      category = widgetCategory,
      action = AddedWidgetToMomentAction.name,
      label = Option(widgetLabel),
      value = Option(AddedWidgetToMomentValue.value))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}

