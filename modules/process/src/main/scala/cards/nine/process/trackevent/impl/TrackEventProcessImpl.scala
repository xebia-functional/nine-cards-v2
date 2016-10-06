package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent._
import cards.nine.services.track.TrackServices
import cats.implicits._
import monix.eval.Task

class TrackEventProcessImpl(trackServices: TrackServices)
  extends TrackEventProcess
  with ImplicitsTrackEventException {

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = LauncherScreen,
      category = category,
      action = OpenAction,
      label = Option(packageName),
      value = Option(OpenAppFromAppDrawerValue))

    def eventForGames(category: Category): TaskService[Unit] =
      category match {
        case AppCategory(nineCardCategory) if nineCardCategory.isGameCategory =>
          trackServices.trackEvent(event.copy(category = AppCategory(Game))).resolve[TrackEventException]
        case _ => TaskService(Task(Right(())))
      }

    (trackServices.trackEvent(event) *> eventForGames(category)).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = OpenCardAction,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def addAppToCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = AddedToCollectionAction,
      label = Option(packageName),
      value = Option(AddedToCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def removeFromCollection(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = CollectionDetailScreen,
      category = category,
      action = RemovedFromCollectionAction,
      label = Option(packageName),
      value = Option(RemovedFromCollectionValue))
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

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

