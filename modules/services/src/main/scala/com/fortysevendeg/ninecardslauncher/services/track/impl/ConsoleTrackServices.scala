package com.fortysevendeg.ninecardslauncher.services.track.impl

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.track.{TrackEvent, TrackServices}
import monix.eval.Task

class ConsoleTrackServices extends TrackServices {

  override def trackEvent(event: TrackEvent): TaskService[Unit] = TaskService {
    Task(Right(println(
      s"""Track
          | Action ${event.action}
          | Category ${event.category}
          | Label ${event.label.getOrElse("")}
          | Screen ${event.screen}
          | Value ${event.value.getOrElse(0)}""".stripMargin)))
  }
}