package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}
import cats.implicits._
import monix.eval.Task

trait LauncherTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  val launcherScreen = LauncherScreen

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = launcherScreen,
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

}
