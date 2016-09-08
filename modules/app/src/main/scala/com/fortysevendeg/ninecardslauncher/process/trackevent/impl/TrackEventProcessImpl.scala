package com.fortysevendeg.ninecardslauncher.process.trackevent.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.Game
import com.fortysevendeg.ninecardslauncher.process.trackevent._
import com.fortysevendeg.ninecardslauncher.services.analytics.{AnalyticEvent, AnalyticsServices}
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService

import scalaz.concurrent.Task

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
      TaskService(Task(Xor.Right(())))
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

  def addToCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = AddedToCollectionAction.name,
      label = Option(packageName),
      value = Option(AddedToCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  def removedInCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = RemovedInCollectionAction.name,
      label = Option(packageName),
      value = Option(RemovedInCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

}
