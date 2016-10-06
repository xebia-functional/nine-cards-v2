package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent._
import cards.nine.services.track.TrackServices
import monix.eval.Task

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

