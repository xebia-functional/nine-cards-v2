package cards.nine.services.track.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types.MomentCategory
import cards.nine.services.track.TrackServices
import monix.eval.Task

class ConsoleTrackServices extends TrackServices {

  override def trackEvent(event: TrackEvent): TaskService[Unit] = TaskService {
    val categoryName = event.category match {
      case MomentCategory(moment) => s"WIDGET_${moment.name}"
      case _ => event.category.name
    }

    Task(Right(println(
      s"""Track
          | Action ${event.action.name}
          | Category $categoryName
          | Label ${event.label.getOrElse("")}
          | Screen ${event.screen.name}
          | Value ${event.value.map(_.value).getOrElse(0)}""".stripMargin)))
  }
}