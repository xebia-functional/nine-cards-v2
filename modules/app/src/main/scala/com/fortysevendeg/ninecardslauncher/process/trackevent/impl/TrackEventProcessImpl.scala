package com.fortysevendeg.ninecardslauncher.process.trackevent.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.Game
import com.fortysevendeg.ninecardslauncher.process.trackevent._
import com.fortysevendeg.ninecardslauncher.services.analytics.{AnalyticEvent, AnalyticsServices}
import monix.eval.Task
import cats.implicits._

class TrackEventProcessImpl(analyticsServices: AnalyticsServices)
  extends TrackEventProcess
  with ImplicitsTrackEventException {

  private[this] val startNameGame = "GAME_"

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = LauncherScreen.name,
      category = category.name,
      action = OpenAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromAppDrawerValue.value))

    def eventForGames(isGame: Boolean): TaskService[Unit] = if (isGame) {
      analyticsServices.trackEvent(event.copy(category = Game.name)).resolve[TrackEventException]
    } else {
      TaskService(Task(Right(())))
    }

    (analyticsServices.trackEvent(event) *> eventForGames(category.name.startsWith(startNameGame))).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = OpenCardAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = AddedToCollectionAction.name,
      label = Option(packageName),
      value = Option(AddedToCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removedInCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = RemovedInCollectionAction.name,
      label = Option(packageName),
      value = Option(RemovedInCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory) = {
    val widgetLabel = s"$packageName:$className"
    val widgetCategory = s"WIDGET_${moment.name}"
    val event = AnalyticEvent(
      screen = WidgetScreen.name,
      category = widgetCategory,
      action = AddedWidgetToMomentAction.name,
      label = Option(widgetLabel),
      value = Option(AddedWidgetToMomentValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

}
