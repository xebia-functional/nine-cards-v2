package cards.nine.services.track

import cards.nine.commons.services.TaskService.NineCardException

case class TrackServicesException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsTrackServicesException {
  implicit def trackServicesExceptionConverter =
    (t: Throwable) => TrackServicesException(t.getMessage, Option(t))
}
