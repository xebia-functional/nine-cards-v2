package com.fortysevendeg.ninecardslauncher.process.trackevent

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class TrackEventException(message: String,cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsTrackEventException {
  implicit def trackEventExceptionConverter = (t: Throwable) => TrackEventException(t.getMessage, Option(t))
}